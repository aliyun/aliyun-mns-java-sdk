package com.aliyun.mns.sample.scenarios.largeMessage;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.CloudTopic;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.sample.scenarios.largeMessage.service.MNSExtendedClient;
import com.aliyun.mns.sample.scenarios.largeMessage.service.bean.MNSExtendedConfiguration;
import com.aliyun.mns.sample.scenarios.largeMessage.service.impl.MNSExtendedClientImpl;
import com.aliyun.mns.sample.utils.ReCreateUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.EnvironmentVariableCredentialsProvider;
import com.aliyuncs.exceptions.ClientException;
import org.junit.Assert;

/**
 * 下述常量均可替换为 业务自身的变量
 */
public class LargeMessageDemo {

    private final static String OSS_ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    private final static String OSS_BUCKET_NAME = "mns-test-hangzhou-bucket-internet";
    private final static String MNS_QUEUE_NAME = "test-largeMessage-queue";
    private final static String MNS_TOPIC_NAME = "test-largeMessage-topic";
    /**
     * 4B为 临界值，大于 4B 即用 OSS 存储
     */
    private final static Long payloadSizeThreshold = 4L;

    public static void main(String[] args) throws ClientException {
        // 从环境变量中获取访问凭证。运行本代码示例之前，请先配置环境变量:https://help.aliyun.com/zh/oss/developer-reference/oss-java-configure-access-credentials?spm=a2c4g.11186623.0.i2#627002f2feie5

        EnvironmentVariableCredentialsProvider credentialsProvider = CredentialsProviderFactory.newEnvironmentVariableCredentialsProvider();


        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(OSS_ENDPOINT, credentialsProvider);

        // 创建 MNS 实例
        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        MNSClient client = account.getMNSClient();
        CloudQueue queue = client.getQueueRef(MNS_QUEUE_NAME);
        CloudTopic cloudTopic = client.getTopicRef(MNS_TOPIC_NAME);

        //reCreate, 服务端异常时重试
        boolean reCreateSuccess = false;
        while (!reCreateSuccess){
            try {
                ReCreateUtil.reCreateQueue(client, MNS_QUEUE_NAME);
                ReCreateUtil.reCreateTopic(client, MNS_TOPIC_NAME);
                reCreateSuccess = true;
            } catch (ServiceException e) {
                reCreateSuccess = false;
            }
        }

        // 配置 超大队列属性
        MNSExtendedConfiguration configuration = new MNSExtendedConfiguration()
            .setOssClient(ossClient).setOssBucketName(OSS_BUCKET_NAME)
            .setMNSQueue(queue)
            .setMNSTopic(cloudTopic)
            .setPayloadSizeThreshold(payloadSizeThreshold);

        MNSExtendedClient mnsExtendedClient = new MNSExtendedClientImpl(configuration);

        try {
            // 执行常规发送
            Message normalMessage = new Message();
            normalMessage.setMessageBodyAsRawString("1");
            mnsExtendedClient.sendMessage(normalMessage);
            Message message = mnsExtendedClient.receiveMessage(10);
            System.out.println("[normal]ReceiveMsg:"+message.getMessageBodyAsRawString());
            mnsExtendedClient.deleteMessage(message.getReceiptHandle());

            // 大文件发送:Queue 模型
            String largeMsgBody = "largeMessage";
            Assert.assertTrue(largeMsgBody.getBytes().length > payloadSizeThreshold);

            Message largeMessage = new Message();
            largeMessage.setMessageBodyAsRawString(largeMsgBody);

            mnsExtendedClient.sendMessage(largeMessage);
            Message receiveMessage = mnsExtendedClient.receiveMessage(10);
            System.out.println("[large]ReceiveMsg:"+receiveMessage.getMessageBodyAsRawString());
            mnsExtendedClient.deleteMessage(receiveMessage.getReceiptHandle());

            client.close();
            ossClient.shutdown();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
