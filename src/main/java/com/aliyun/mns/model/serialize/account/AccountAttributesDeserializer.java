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
import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.LOGGING_BUCKET_TAG;
import static com.aliyun.mns.common.MNSConstants.TRACE_ENABLED_TAG;

public class AccountAttributesDeserializer extends XMLDeserializer<AccountAttributes> {

    @Override
    public AccountAttributes deserialize(InputStream stream) throws Exception {
        Document doc = getDocumentBuilder().parse(stream);

        Element root = doc.getDocumentElement();

        AccountAttributes accountAttributes = new AccountAttributes();

        String loggingBucket = safeGetElementContent(root, LOGGING_BUCKET_TAG, null);
        accountAttributes.setLoggingBucket(loggingBucket);
        String traceEnabled = safeGetElementContent(root, TRACE_ENABLED_TAG, null);
        if (traceEnabled != null) {
            accountAttributes.setTraceEnabled(Integer.parseInt(traceEnabled));
        }
        return accountAttributes;
    }
}
