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

package com.aliyun.mns.sample.credential;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.Message;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyuncs.auth.AlibabaCloudCredentialsProvider;
import com.aliyuncs.auth.BasicCredentials;
import com.aliyuncs.auth.STSAssumeRoleSessionCredentialsProvider;
import com.aliyuncs.profile.DefaultProfile;

/**
 *  env 设置：
 * export MNS_ACCESS_KEY_ID=xxxx
 * export MNS_ACCESS_KEY_SECRET=xxxx
 * export MNS_STS_ROLE_ARN=acs:ram::xxxx:role/xxxxrole
 *
 * 子账号需要有如下权限：
 * AliyunSTSAssumeRoleAccess
 */
public class RamRoleCredentialsDemo {

    public static void main(String[] args) {

        String queueName = "testQueue";
        // 这个 region Id 和 mns endpoint 为一个region
        String regionId = "cn-hangzhou";

        // 从环境变量中获取RAM用户的访问密钥（AccessKey ID和AccessKey Secret）。
        String accessKeyId = System.getenv("MNS_ACCESS_KEY_ID");
        String accessKeySecret = System.getenv("MNS_ACCESS_KEY_SECRET");
        // 从环境变量中获取RAM角色的RamRoleArn。
        String roleArn = System.getenv("MNS_STS_ROLE_ARN");

        DefaultProfile profile = DefaultProfile.getProfile(regionId);
        AlibabaCloudCredentialsProvider provider = new STSAssumeRoleSessionCredentialsProvider(
            new BasicCredentials(accessKeyId, accessKeySecret),
            roleArn,
            profile
        );


        // 以 mns queue 发消息操作为业务逻辑模拟
        mnsQueueSendMessage(queueName, provider);
    }

    private static void mnsQueueSendMessage(String queueName, AlibabaCloudCredentialsProvider provider) {
        String endpoint = ServiceSettings.getMNSAccountEndpoint();
        CloudAccount account = new CloudAccount(endpoint, provider);
        MNSClient client = account.getMNSClient();

        try {
            CloudQueue queue = client.getQueueRef(queueName);
            Message message = new Message();
            message.setMessageBody("demo_message_body");
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
            se.printStackTrace();
        } catch (Exception e) {
            System.out.println("Unknown exception happened!");
            e.printStackTrace();
        }finally {

            client.close();
        }
    }

}
