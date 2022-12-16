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

import com.aliyun.mns.common.BatchSendException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.Message;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.io.InputStream;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import static com.aliyun.mns.common.MNSConstants.ERROR_CODE_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_HOST_ID_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_REQUEST_ID_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_LIST_TAG;

public class ErrorMessageListDeserializer extends XMLDeserializer<Exception> {
    @Override
    public Exception deserialize(InputStream stream) throws Exception {

//       byte[] bytes = new byte[1024];
//       while(stream.read(bytes, 0, stream.available())>0){
//       System.out.println(new String(bytes));
//       }
        Document doc = getDocmentBuilder().parse(stream);

        Exception ret = null;
        Element root = doc.getDocumentElement();

        if (root != null) {
            String rootName = root.getNodeName();

            if (rootName == MESSAGE_LIST_TAG) {
                List<Message> msgs = new MessageListDeserializer().deserialize(doc);
                if (msgs != null) {
                    ret = new BatchSendException(msgs);
                }
            } else if (rootName == ERROR_TAG) {
                String code = safeGetElementContent(root, ERROR_CODE_TAG, "");
                String message = safeGetElementContent(root, ERROR_MESSAGE_TAG, "");
                String requestId = safeGetElementContent(root, ERROR_REQUEST_ID_TAG, "");
                String hostId = safeGetElementContent(root, ERROR_HOST_ID_TAG, "");

                ret = new ServiceException(message, null, code, requestId, hostId);
            }
        }
        return ret;
    }
}