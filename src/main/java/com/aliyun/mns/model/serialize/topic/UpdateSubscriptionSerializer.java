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

package com.aliyun.mns.model.serialize.topic;

import com.aliyun.mns.model.SubscriptionMeta;
import com.aliyun.mns.model.serialize.XMLSerializer;
import com.aliyun.mns.model.serialize.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_XML_NAMESPACE;
import static com.aliyun.mns.common.MNSConstants.FILTER_TAG_TAG;
import static com.aliyun.mns.common.MNSConstants.NOTIFY_STRATEGY_TAG;
import static com.aliyun.mns.common.MNSConstants.SUBSCRIPTION_TAG;

public class UpdateSubscriptionSerializer extends XMLSerializer<SubscriptionMeta> {

    public InputStream serialize(SubscriptionMeta obj, String encoding) throws Exception {
        Document doc = getDocmentBuilder().newDocument();
        Element root = doc.createElementNS(DEFAULT_XML_NAMESPACE, SUBSCRIPTION_TAG);
        doc.appendChild(root);

        Element node = safeCreateContentElement(doc, NOTIFY_STRATEGY_TAG, obj.getNotifyStrategy(), null);
        if (node != null) {
            root.appendChild(node);
        }

        node = safeCreateContentElement(doc, FILTER_TAG_TAG, obj.getFilterTag(), null);
        if (node != null) {
            root.appendChild(node);
        }

        String xml = XmlUtil.xmlNodeToString(doc, encoding);
        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
