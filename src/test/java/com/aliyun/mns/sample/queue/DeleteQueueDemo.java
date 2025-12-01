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

package com.aliyun.mns.sample.queue;

import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.client.MNSClientBuilder;
import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.auth.SignVersion;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
/**
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2. ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *           mns.accountendpoint=http://xxxxxxx
 *           mns.regionId=cn-xxxx
 */
public class DeleteQueueDemo {

    public static void main(String[] args) {
        ClientConfiguration clientConfig = new ClientConfiguration();
        clientConfig.setSignatureVersion(SignVersion.V4);
        MNSClient client = MNSClientBuilder.create()
            .accountEndpoint(ServiceSettings.getMNSAccountEndpoint()) // eg: http://123.mns.cn-hangzhou.aliyuncs.com
            .clientConfiguration(clientConfig)
            .region(ServiceSettings.getMNSRegion()) // eg: "cn-hangzhou"
            .build();

        try {   //Delete Queue
            CloudQueue queue = client.getQueueRef("cloud-queue-demo");
            queue.delete();
            System.out.println("Delete cloud-queue-demo successfully!");
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
