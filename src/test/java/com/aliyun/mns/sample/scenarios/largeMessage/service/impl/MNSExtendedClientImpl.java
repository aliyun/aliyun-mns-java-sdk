package com.aliyun.mns.sample.scenarios.largeMessage.service.impl;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.ServiceHandlingRequiredException;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.TopicMessage;
import com.aliyun.mns.sample.scenarios.largeMessage.service.MNSExtendedClient;
import com.aliyun.mns.sample.scenarios.largeMessage.service.bean.MNSExtendedConfiguration;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.VoidResult;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MNSExtendedClientImpl implements MNSExtendedClient {
    public static Logger logger = LoggerFactory.getLogger(MNSExtendedClientImpl.class);

    private MNSExtendedConfiguration extendedConfiguration;

    private String OSS_URL_MESSAGE_PREFIX = "#MNS_EXTENDED_OSS_MESSAGE_BODY#";
    private String OSS_BUCKET_Object_SPLIT = ":";

    private Map<String, String> messageId2OssUrlMap;


    public MNSExtendedClientImpl(MNSExtendedConfiguration extendedConfiguration) {
        Assert.assertNotNull(extendedConfiguration);

        this.extendedConfiguration = extendedConfiguration;

        messageId2OssUrlMap = new ConcurrentHashMap<String, String>();
    }

    @Override
    public Message sendMessage(Message message) throws ServiceException {
        Assert.assertNotNull(message);

        CloudQueue queue = extendedConfiguration.getMNSQueue();

        long msgSize = message.getOriginalMessageBody().length();
        if (msgSize < extendedConfiguration.getPayloadSizeThreshold()) {
            message = queue.putMessage(message);
            return message;
        }

        String ossUrl = this.putMessageBodyToOSS(message.getOriginalMessageBody());
        String newMessageBody = this.OSS_URL_MESSAGE_PREFIX + ossUrl;
        message.setBaseMessageBody(newMessageBody);
        message = queue.putMessage(message);
        logger.info("messageId:{}, ossUrl:{}, messageSize:{}KB, send message by oss", message.getMessageId(), ossUrl, msgSize / 1024);

        return message;
    }

    @Override
    public Message receiveMessage(int waitSeconds) throws ServiceHandlingRequiredException {
        CloudQueue queue = extendedConfiguration.getMNSQueue();
        Message msg = queue.popMessage(waitSeconds);
        if (msg == null) {
            return null;
        }
        return handleMessage(msg);
    }


    @Override
    public List<Message> batchReceiveMessage(int batchSize, int waitSeconds) throws ServiceHandlingRequiredException {
        CloudQueue queue = extendedConfiguration.getMNSQueue();
        List<Message> messages = queue.batchPopMessage(batchSize,waitSeconds);

        List<Message> resultList = new ArrayList<Message>();
        for (Message message : messages) {
            resultList.add(handleMessage(message));
        }
        return resultList;
    }

    @Override
    public void deleteMessage(String receiptHandle) throws ServiceException, ServiceHandlingRequiredException {
        extendedConfiguration.getMNSQueue().deleteMessage(receiptHandle);

        String ossUrl = this.messageId2OssUrlMap.get(receiptHandle);
        if (StringUtils.isNotBlank(ossUrl)) {
            this.deleteMessageBodyObjectOnOSS(ossUrl);
            this.messageId2OssUrlMap.remove(receiptHandle);
        }

    }

    @Override
    public TopicMessage publishMessage(TopicMessage message) throws ServiceException {
        Assert.assertNotNull(message);

        CloudTopic cloudTopic = extendedConfiguration.getMNSTopic();

        long msgSize = message.getOriginalMessageBody().length();
        if (msgSize < extendedConfiguration.getPayloadSizeThreshold()) {
            message = cloudTopic.publishMessage(message);
            return message;
        }

        String ossUrl = this.putMessageBodyToOSS(message.getOriginalMessageBody());
        String newMessageBody = this.OSS_URL_MESSAGE_PREFIX + ossUrl;
        message.setBaseMessageBody(newMessageBody);
        message = cloudTopic.publishMessage(message);
        logger.info("messageId:{}, ossUrl:{}, messageSize:{}KB, send message by oss", message.getMessageId(), ossUrl, msgSize / 1024);

        return message;
    }





    private Message handleMessage(Message msg) {
        String body = msg.getOriginalMessageBody();
        if (body.startsWith(this.OSS_URL_MESSAGE_PREFIX)){
            // oss 存储
            String ossUrl = body.substring(this.OSS_URL_MESSAGE_PREFIX.length());
            String messageBody = this.getMessageBodyFromOSS(ossUrl);
            msg.setMessageBodyAsRawString(messageBody);
            // 此处 存在重复覆盖的场景，实际使用需要考虑规避
            messageId2OssUrlMap.put(msg.getReceiptHandle(), ossUrl);
        }

        // 常规
        return msg;
    }
    private String putMessageBodyToOSS(String messageBody) {
        logger.debug("getMessageBodyFromOSS start");

        String bucketName = extendedConfiguration.getOssBucketName();

        String objectKey = "large-message-body-" + UUID.randomUUID();
        InputStream instream = new ByteArrayInputStream(messageBody.getBytes());
        extendedConfiguration.getOssClient().putObject(bucketName, objectKey, instream);

        String ossUrl = bucketName + OSS_BUCKET_Object_SPLIT + objectKey;
        logger.debug("putMessageBodyToOSS: ossUrl=" + ossUrl);
        return ossUrl;
    }


    private void deleteMessageBodyObjectOnOSS(String ossUrl) {
        String[] info = ossUrl.split(OSS_BUCKET_Object_SPLIT);
        String bucketName = info[0];
        String objectKey = info[1];
        logger.debug("deleteMessageBodyObjectOnOSS: bucketName=" + bucketName
            + " objectKey=" + objectKey);

        // 默认成功，生产建议，这里做容错相关逻辑
        VoidResult result = extendedConfiguration.getOssClient().deleteObject(bucketName, objectKey);
    }

    private String getMessageBodyFromOSS(String ossUrl) {
        logger.debug("getMessageBodyFromOSS ossUrl=" + ossUrl);
        String[] info = ossUrl.split(OSS_BUCKET_Object_SPLIT);
        String bucketName = info[0];
        String objectKey = info[1];
        logger.debug("getMessageBodyFromOSS: bucketName=" + bucketName +
            " objectKey" + objectKey);

        try {
            OSSObject obj = extendedConfiguration.getOssClient().getObject(bucketName, objectKey);

            StringBuilder sb = new StringBuilder("");
            InputStream istream = obj.getObjectContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(istream));
            try {
                while (true) {
                    String line = reader.readLine();
                    if (line == null) {
                        break;
                    }
                    sb.append(line);
                }
            } catch (IOException ioe) {
                logger.error("Exception occurs when getting message body from oss");
                sb = new StringBuilder("Error MessageBody!");
            } finally {
                try {
                    reader.close();
                } catch (IOException ioe) {
                }
            }

            return sb.toString();
        } catch (OSSException e) { // 兼容从mns可能收到重复消息的情况
            if ("NoSuchKey".equals(e.getErrorCode())) {
                return null;
            }
            throw e;
        }
    }
}
