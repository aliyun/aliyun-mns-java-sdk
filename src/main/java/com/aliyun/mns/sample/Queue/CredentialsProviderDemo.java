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
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyuncs.auth.InstanceProfileCredentialsProvider;

public class CredentialsProviderDemo {

    public static void main(String[] args) {
        // WARNING： Please do not hard code your accessId and accesskey in next line.
        //(more information: https://yq.aliyun.com/articles/55947)

        InstanceProfileCredentialsProvider provider = new InstanceProfileCredentialsProvider("{ecsRole}");
        String endpoint = "http://{accountId}.mns.{region}.aliyuncs.com";
        CloudAccount account = new CloudAccount(endpoint, provider);
        MNSClient client = account.getMNSClient(); //this client need only initialize once

        try {   //Create Queue
            CloudQueue queue = client.getQueueRef("gongshi-test");// replace with your queue name
            Message message = new Message();
            message.setMessageBody("demo_message_body"); // use your own message body here
            Message putMsg = queue.putMessage(message);
            System.out.println("msgId:" + putMsg.getMessageId());
            System.out.println("msgMd5:" + putMsg.getMessageBodyMD5());
        } catch (ClientException ce) {
            System.out.println("Something wrong with the network connection between client and MNS service."
                + "Please check your network and DNS availablity.");
            ce.printStackTrace();
        } catch (ServiceException se) {
            if (se.getErrorCode().equals("QueueNotExist")) {
                System.out.println("Queue is not exist.Please create before use");
            } else if (se.getErrorCode().equals("TimeExpired")) {
                System.out.println("The request is time expired. Please check your local machine timeclock");
            }
            /*
            you can get more MNS service error code in following link.
            https://help.aliyun.com/document_detail/mns/api_reference/error_code/error_code.html?spm=5176.docmns/api_reference/error_code/error_response
            */
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception happened!");
            e.printStackTrace();
        }

        client.close();
    }

}
