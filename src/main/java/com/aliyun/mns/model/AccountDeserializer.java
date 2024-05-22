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

package com.aliyun.mns.model;

import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.io.InputStream;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.ACCOUNT_ID_TAG;

public class AccountDeserializer extends XMLDeserializer<String> {

    @Override
    public String deserialize(InputStream stream) throws Exception {

        Element root = null;

//		 byte[] bytes = new byte[1024];
//		 while(stream.read(bytes, 0, stream.available())>0){
//		 System.out.println(new String(bytes));
//		 }
        Document doc = getDocumentBuilder().parse(stream);

        root = doc.getDocumentElement();
        String accountId = safeGetElementContent(root, ACCOUNT_ID_TAG, null);
        return accountId;
    }

}
