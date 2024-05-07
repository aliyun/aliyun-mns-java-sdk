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

import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.serialize.XMLSerializer;
import com.aliyun.mns.model.serialize.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.DEFAULT_XML_NAMESPACE;
import static com.aliyun.mns.common.MNSConstants.DELAY_SECONDS_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_BODY_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_LIST_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.PRIORITY_TAG;

public class MessageListSerializer extends XMLSerializer<List<Message>> {

    @Override
    public InputStream serialize(List<Message> msgs, String encoding) throws Exception {
        Document doc = getDocmentBuilder().newDocument();

        Element messages = doc.createElementNS(DEFAULT_XML_NAMESPACE, MESSAGE_LIST_TAG);

        doc.appendChild(messages);

        if (msgs != null) {
            for (Message msg : msgs) {
                Element root = doc.createElement(MESSAGE_TAG);
                messages.appendChild(root);

                Element node = safeCreateContentElement(doc, MESSAGE_BODY_TAG,
                    msg.getOriginalMessageBody(), "");

                if (node != null) {
                    root.appendChild(node);
                }

                node = safeCreateContentElement(doc, DELAY_SECONDS_TAG,
                    msg.getDelaySeconds(), null);
                if (node != null) {
                    root.appendChild(node);
                }

                node = safeCreateContentElement(doc, PRIORITY_TAG,
                    msg.getPriority(), null);
                if (node != null) {
                    root.appendChild(node);
                }

            }
        }
        String xml = XmlUtil.xmlNodeToString(doc, encoding);

        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
