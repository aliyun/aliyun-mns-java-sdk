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

package com.aliyun.mns.sample.openService;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.model.response.commonbuy.OpenServiceResponse;

public class OpenServiceDemo {


    public static void main(String[] args) {
        // 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
        CloudAccount account = new CloudAccount(ServiceSettings.getMNSAccountEndpoint());
        MNSClient client = account.getMNSClient();

        try {
            OpenServiceResponse openServiceResponse = client.openService();
            System.out.println(openServiceResponse.getOrderId());
            System.out.println(openServiceResponse.getRequestId());
        } catch (ServiceException e) {
            String message = e.getMessage();
            if (message.toUpperCase().contains("INSTANCE_ID_IS_NOT_UNIQUE")){
                System.out.println("[OpenService] this account has opened,msg:"+ message);
                return;
            }
            e.printStackTrace();
        }finally {
            client.close();
        }
    }
}
