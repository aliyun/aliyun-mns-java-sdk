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

package com.aliyun.mns.model.serialize.account;

import com.aliyun.mns.model.AccountAttributes;
import com.aliyun.mns.model.serialize.XMLSerializer;
import com.aliyun.mns.model.serialize.XmlUtil;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.ACCOUNT_TAG;
import static com.aliyun.mns.common.MNSConstants.DEFAULT_XML_NAMESPACE;
import static com.aliyun.mns.common.MNSConstants.LOGGING_BUCKET_TAG;
import static com.aliyun.mns.common.MNSConstants.TRACE_ENABLED_TAG;

public class AccountAttributesSerializer extends XMLSerializer<AccountAttributes> {

    @Override
    public InputStream serialize(AccountAttributes obj, String encoding)
        throws Exception {
        Document doc = getDocumentBuilder().newDocument();

        Element root = doc.createElementNS(DEFAULT_XML_NAMESPACE, ACCOUNT_TAG);
        doc.appendChild(root);

        Element node = safeCreateContentElement(doc, LOGGING_BUCKET_TAG,
            obj.getLoggingBucket(), null);
        if (node != null) {
            root.appendChild(node);
        }
        node = safeCreateContentElement(doc, TRACE_ENABLED_TAG,
            obj.getTraceEnabled(), null);
        if (node != null) {
            root.appendChild(node);
        }

        String xml = XmlUtil.xmlNodeToString(doc, encoding);
        return new ByteArrayInputStream(xml.getBytes(encoding));
    }
}
