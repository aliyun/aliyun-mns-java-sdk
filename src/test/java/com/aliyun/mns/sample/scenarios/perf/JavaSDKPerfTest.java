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

package com.aliyun.mns.sample.scenarios.perf;

import com.aliyun.mns.client.CloudAccount;
import com.aliyun.mns.client.CloudQueue;
import com.aliyun.mns.client.MNSClient;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.ServiceHandlingRequiredException;
import com.aliyun.mns.common.http.ClientConfiguration;
import com.aliyun.mns.common.utils.ServiceSettings;
import com.aliyun.mns.common.utils.ThreadUtil;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.sample.utils.ReCreateUtil;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import org.apache.commons.lang3.StringUtils;

/**
 * 并发测试示例代码
 * 前置要求
 * 1. 遵循阿里云规范，env 设置 ak、sk，详见：https://help.aliyun.com/zh/sdk/developer-reference/configure-the-alibaba-cloud-accesskey-environment-variable-on-linux-macos-and-windows-systems
 * 2.  ${"user.home"}/.aliyun-mns.properties 文件配置如下：
 *          mns.endpoint=http://xxxxxxx
 *          mns.perf.queueName=JavaSDKPerfTestQueue # queue名称
 *          mns.perf.threadNum=200 # 并发线程数
 *          mns.perf.durationTime=180 # 测试持续时间（秒）
 */
public class JavaSDKPerfTest {
    private static MNSClient client = null;

    private static String endpoint = null;

    private static String queueName;
    private static int threadNum;

    /**
     * 测试持续时间（秒）
     */
    private static long durationTime;


    public static void main(String[] args) throws InterruptedException, ServiceException {
        if (!parseConf()) {
            return;
        }

        // 1. init client
        ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setMaxConnections(threadNum);
        clientConfiguration.setMaxConnectionsPerRoute(threadNum);
        CloudAccount cloudAccount = new CloudAccount(endpoint, clientConfiguration);
        client = cloudAccount.getMNSClient();

        // 2. reCreateQueue
        ReCreateUtil.reCreateQueue(client,queueName);
        // 3. SendMessage
        Function<CloudQueue,Message> sendFunction = new Function<CloudQueue, Message>() {
            @Override
            public Message apply(CloudQueue queue) throws ServiceException {
                Message message = new Message();
                message.setMessageBody("BodyTest");
                return queue.putMessage(message);
            }
        };
        actionProcess("SendMessage", sendFunction , durationTime);
        // 4. Now is the ReceiveMessage
        Function<CloudQueue,Message> receiveFunction = new Function<CloudQueue, Message>() {
            @Override
            public Message apply(CloudQueue queue) throws ServiceException, ServiceHandlingRequiredException {
                Message message = queue.popMessage();
                String handle = message == null?null:message.getReceiptHandle();
                if (StringUtils.isNotBlank(handle)) {
                    queue.deleteMessage(handle);
                }
                return message;
            }
        };
        actionProcess("ReceiveAndDelMessage", receiveFunction, durationTime);



        client.close();
        System.out.println("=======end=======");
    }

    private static void actionProcess(String actionName, final Function<CloudQueue, Message> function, final long durationSeconds) throws InterruptedException {
        System.out.println(actionName +" start!");

        final AtomicLong totalCount = new AtomicLong(0);

        ThreadPoolExecutor executor = ThreadUtil.initThreadPoolExecutorAbort();
        ThreadUtil.asyncWithReturn(executor, threadNum, new ThreadUtil.AsyncRunInterface() {
            @Override
            public void run() {
                try {
                    String threadName = Thread.currentThread().getName();

                    CloudQueue queue = client.getQueueRef(queueName);
                    Message message = new Message();
                    message.setMessageBody("BodyTest");
                    long count = 0;

                    Date startDate = new Date();
                    long startTime = startDate.getTime();

                    System.out.printf("[Thread%s]startTime:%s %n", threadName, getBjTime(startDate));
                    long endTime = startTime + durationSeconds * 1000L;
                    while (true) {
                        for (int i = 0; i < 50; ++i) {
                            function.apply(queue);
                        }
                        count += 50;

                        if (System.currentTimeMillis() >= endTime) {
                            break;
                        }
                    }

                    System.out.printf("[Thread%s]endTime:%s,count:%d %n", threadName, getBjTime(new Date()),count);

                    totalCount.addAndGet(count);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        executor.shutdown();
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        System.out.println(actionName +" QPS: "+(totalCount.get() / durationSeconds));
    }


    protected static boolean parseConf() {

        // init the member parameters
        endpoint = ServiceSettings.getMNSAccountEndpoint();
        System.out.println("Endpoint: " + endpoint);

        queueName = ServiceSettings.getMNSPropertyValue("perf.queueName","JavaSDKPerfTestQueue");
        System.out.println("QueueName: " + queueName);
        threadNum = Integer.parseInt(ServiceSettings.getMNSPropertyValue("perf.threadNum","2"));
        System.out.println("ThreadNum: " + threadNum);
        durationTime = Long.parseLong(ServiceSettings.getMNSPropertyValue("perf.totalSeconds","6"));
        System.out.println("DurationTime: " + durationTime);

        return true;
    }

    /**
     * 获取北京时间
     */
    private static String getBjTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        return sdf.format(date);
    }


    public interface Function<T, R> {

        /**
         * Applies this function to the given argument.
         *
         * @param t the function argument
         * @return the function result
         */
        R apply(T t) throws ServiceException, ServiceHandlingRequiredException;

    }
}
