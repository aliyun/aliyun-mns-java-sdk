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

/*
 * Class for CloudTopicForPull which provides function to broadcast message to queue by given topic
 * and queue name list. The consumer only receive message from queue and do not need expose it's address.
 */

package com.aliyun.mns.client;

import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.SubscriptionMeta.NotifyContentFormat;
import com.aliyun.mns.model.TopicMessage;
import java.util.List;
import java.util.Vector;

public class CloudPullTopic {
    private CloudTopic rawTopic;
    private Vector<String> queueNameList;
    private Vector<CloudQueue> queueList;
    private Vector<String> tagList;
    private String QUEUE_SUB_NAME_PREFIX;

    /*
     * use queues in the queueNameList to subscribe the topic.
     */
    private void subscribe(Vector<String> queueNameList) throws ServiceException {
        for (int i = 0; i < queueNameList.size(); i++) {
            String queueName = queueNameList.get(i);
            String queueEndpoint = this.rawTopic.generateQueueEndpoint(queueName);
            String subName = QUEUE_SUB_NAME_PREFIX + queueName;
            SubscriptionMeta subMeta = new SubscriptionMeta();
            subMeta.setSubscriptionName(subName);
            subMeta.setNotifyContentFormat(NotifyContentFormat.SIMPLIFIED);
            subMeta.setNotifyStrategy(SubscriptionMeta.NotifyStrategy.EXPONENTIAL_DECAY_RETRY);
            subMeta.setEndpoint(queueEndpoint);
            try {
                this.rawTopic.subscribe(subMeta);
            } catch (ServiceException se) {
                if (!"SubscriptionAlreadyExist".equals(se.getErrorCode())) {
                    throw se;
                }
            }
        }
    }

    /*
     * use queues in the queueNameList to subscribe the topic with tagList.
     */
    private void subscribe(Vector<String> queueNameList, Vector<String> tagList) throws ServiceException {
        for (int i = 0; i < queueNameList.size(); i++) {
            String queueName = queueNameList.get(i);
            String queueEndpoint = this.rawTopic.generateQueueEndpoint(queueName);
            String subName = QUEUE_SUB_NAME_PREFIX + queueName;
            SubscriptionMeta subMeta = new SubscriptionMeta();
            subMeta.setSubscriptionName(subName);
            subMeta.setNotifyContentFormat(NotifyContentFormat.SIMPLIFIED);
            subMeta.setNotifyStrategy(SubscriptionMeta.NotifyStrategy.EXPONENTIAL_DECAY_RETRY);
            subMeta.setEndpoint(queueEndpoint);
            String tmpTag = tagList.get(i);
            if (tmpTag != null && tmpTag != "") {
                subMeta.setFilterTag(tmpTag);
            }

            try {
                this.rawTopic.subscribe(subMeta);
            } catch (ServiceException se) {
                if (!"SubscriptionAlreadyExist".equals(se.getErrorCode())) {
                    throw se;
                }
            }
        }
    }

    /*
     * Constructor.
     */
    public CloudPullTopic(CloudTopic rawTopic, Vector<String> queueNameList, Vector<CloudQueue> queueList)
        throws ServiceException {
        this.rawTopic = rawTopic;
        this.queueNameList = queueNameList;
        this.queueList = queueList;
        this.QUEUE_SUB_NAME_PREFIX = "sub-for-queue-";

        this.subscribe(this.queueNameList);
    }

    /*
     * Constructor with tagList.
     */
    public CloudPullTopic(CloudTopic rawTopic, Vector<String> queueNameList, Vector<CloudQueue> queueList,
        Vector<String> tagList) throws ServiceException {
        this.rawTopic = rawTopic;
        this.queueNameList = queueNameList;
        this.queueList = queueList;
        this.tagList = tagList;
        this.QUEUE_SUB_NAME_PREFIX = "sub-for-queue-";

        this.subscribe(this.queueNameList, this.tagList);
    }

    /*
     * Publish message to topic.
     */
    public TopicMessage publishMessage(TopicMessage msg) throws ServiceException {
        return this.rawTopic.publishMessage(msg);
    }

    /*
     * get the raw topic.
     */
    public CloudTopic getRawTopic() {
        return this.rawTopic;
    }

    /*
     * delete the raw topic and related queues;
     */
    public void delete() throws ServiceException {
        this.delete(true);
    }

    /*
     * delete the raw topic and delete related queues if need
     */
    public void delete(boolean needDeleteQueues) throws ServiceException {
        this.rawTopic.delete();

        if (needDeleteQueues) {
            for (int i = 0; i < this.queueList.size(); i++) {
                CloudQueue queue = queueList.get(i);
                queue.delete();
            }
        }
    }

    /*
     * get the queue name list.
     */
    public List<String> getQueueNameList() {
        return this.queueNameList;
    }

    /*
     * get the default subscription name for queue.
     */
    public String getQueueSubNamePrefix() {
        return this.QUEUE_SUB_NAME_PREFIX;
    }

    /*
     * set the default prefix of subscription name.
     */
    public void setQueueSubNamePrefix(String queueSubNamePrefix) {
        this.QUEUE_SUB_NAME_PREFIX = queueSubNamePrefix;
    }
}
