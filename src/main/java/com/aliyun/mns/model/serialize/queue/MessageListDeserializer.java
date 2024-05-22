/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.aliyun.mns.model.serialize.queue;

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.model.ErrorMessageResult;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static com.aliyun.mns.common.MNSConstants.DEQUEUE_COUNT_TAG;
import static com.aliyun.mns.common.MNSConstants.ENQUEUE_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.FIRST_DEQUEUE_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_MD5_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ERRORCODE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ERRORMESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ID_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.NEXT_VISIBLE_TIME_TAG;
import static com.aliyun.mns.common.MNSConstants.PRIORITY_TAG;
import static com.aliyun.mns.common.MNSConstants.RECEIPT_HANDLE_TAG;

public class MessageListDeserializer extends XMLDeserializer<List<Message>> {
    @Override
    public List<Message> deserialize(InputStream stream) throws Exception {

        // byte[] bytes = new byte[1024];
        // while(stream.read(bytes, 0, stream.available())>0){
        // System.out.println(new String(bytes));
        // }
        Document doc = getDocumentBuilder().parse(stream);
        return deserialize(doc);

    }

    public List<Message> deserialize(Document doc) {
        NodeList list = doc.getElementsByTagName(MESSAGE_TAG);
        if (list != null && list.getLength() > 0) {
            List<Message> results = new ArrayList<Message>();

            for (int i = 0; i < list.getLength(); i++) {
                Message msg = parseMessage((Element) list.item(i));
                results.add(msg);
            }
            return results;
        }
        return null;
    }

    private ErrorMessageResult parseErrorMessageResult(Element root) {
        ErrorMessageResult result = new ErrorMessageResult();
        String errorCode = safeGetElementContent(root, MESSAGE_ERRORCODE_TAG,
            null);
        result.setErrorCode(errorCode);

        String errorMessage = safeGetElementContent(root,
            MESSAGE_ERRORMESSAGE_TAG, null);
        result.setErrorMessage(errorMessage);
        return result;
    }

    private Message parseMessage(Element root) throws ClientException {
        Message message = new Message();

        String messageId = safeGetElementContent(root, MESSAGE_ID_TAG, null);
        if (messageId == null) {
            message.setErrorMessage(parseErrorMessageResult(root));
            return message;
        }

        message.setMessageId(messageId);
        String messageBody = safeGetElementContent(root, MESSAGE_BODY_TAG, null);
        if (messageBody != null) {
            // try {
            message.setMessageBody(messageBody, Message.MessageBodyType.RAW_STRING);
            // } catch (UnsupportedEncodingException e) {
            // throw new RuntimeException("Not support enconding:"
            // + DEFAULT_CHARSET);
            // }
        }

        String messageBodyMD5 = safeGetElementContent(root,
            MESSAGE_BODY_MD5_TAG, null);
        message.setMessageBodyMD5(messageBodyMD5);

        String receiptHandle = safeGetElementContent(root, RECEIPT_HANDLE_TAG,
            null);
        message.setReceiptHandle(receiptHandle);

        String enqueTime = safeGetElementContent(root, ENQUEUE_TIME_TAG, null);
        if (enqueTime != null) {
            message.setEnqueueTime(new Date(Long.parseLong(enqueTime)));
        }

        String nextVisibleTime = safeGetElementContent(root,
            NEXT_VISIBLE_TIME_TAG, null);
        if (nextVisibleTime != null) {
            message.setNextVisibleTime(new Date(Long.parseLong(nextVisibleTime)));
        }

        String firstDequeueTime = safeGetElementContent(root,
            FIRST_DEQUEUE_TIME_TAG, null);
        if (firstDequeueTime != null) {
            message.setFirstDequeueTime(new Date(
                Long.parseLong(firstDequeueTime)));
        }

        String dequeueCount = safeGetElementContent(root, DEQUEUE_COUNT_TAG,
            null);
        if (dequeueCount != null) {
            message.setDequeueCount(Integer.parseInt(dequeueCount));
        }

        String priority = safeGetElementContent(root, PRIORITY_TAG,
            null);
        if (priority != null) {
            message.setPriority(Integer.parseInt(priority));
        }

        return message;
    }
}
