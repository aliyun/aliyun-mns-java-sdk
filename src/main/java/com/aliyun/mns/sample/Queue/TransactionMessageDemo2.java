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

package com.aliyun.mns.sample.Queue;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.TransactionQueue;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.QueueMeta;

public class TransactionMessageDemo2 {
    public static boolean doLocalOperation(String messageHandler) {
        //TODO: add your own operation and return op result properly.
        return true;
    }

    public static void main(String[] args) {
        System.out.println("Start TransactionMessageDemo");
        String transQueueName = "transQueueName";
        String accessKeyId = ServiceSettings.getMNSAccessKeyId();
        String accessKeySecret = ServiceSettings.getMNSAccessKeySecret();
        String endpoint = ServiceSettings.getMNSAccountEndpoint();

        // WARNING： Please do not hard code your accessId and accesskey in next lines.
        //(more information: https://yq.aliyun.com/articles/55947)
        CloudAccount account = new CloudAccount(accessKeyId, accessKeySecret, endpoint);
        MNSClient client = account.getMNSClient(); //this client need only initialize once

        //create queue for transaction queue.
        QueueMeta queueMeta = new QueueMeta();
        queueMeta.setQueueName(transQueueName);
        queueMeta.setPollingWaitSeconds(15);
        TransactionQueue transQueue = client.createTransQueue(queueMeta, null);

        // do transaction.
        String handler = null;
        try {
            Message msg = new Message();
            String messageBody = "prepare message with the infomation of local operation going to do.";
            msg.setMessageBody(messageBody);
            Message prepareMsg = transQueue.sendPrepareMessage(msg);
            if (prepareMsg != null) {
                handler = prepareMsg.getReceiptHandle();
            } else {
                throw new Exception("send prepareMessage fail.");
            }

            //do local transaction operation.
            boolean localOpResult = doLocalOperation(prepareMsg.getReceiptHandle());

            if (localOpResult) {
                //commit message, it will retry 3 times by default if it was fail.
                transQueue.commitMessage(prepareMsg.getReceiptHandle());
            } else {
                throw new Exception("message is committed fail");
            }
        } catch (Exception e) {
            if (handler != null) {
                transQueue.rollbackMessage(handler);
            }
        }

        // delete queue and close client if we won't use them.
        transQueue.getInnerQueue().delete();
        client.close();
        System.out.println("End TransactionMessageDemo");
    }

}
