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

import com.aliyun.mns.common.BatchDeleteException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.model.ErrorMessageResult;
import com.aliyun.mns.model.serialize.XMLDeserializer;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static com.aliyun.mns.common.MNSConstants.ERROR_CODE_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_HOST_ID_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_LIST_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_REQUEST_ID_TAG;
import static com.aliyun.mns.common.MNSConstants.ERROR_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ERROR_CODE_TAG;
import static com.aliyun.mns.common.MNSConstants.MESSAGE_ERROR_MESSAGE_TAG;
import static com.aliyun.mns.common.MNSConstants.RECEIPT_HANDLE_TAG;

public class ErrorReceiptHandleListDeserializer extends XMLDeserializer<Exception> {
    @Override
    public Exception deserialize(InputStream stream) throws Exception {

        Document doc = getDocumentBuilder().parse(stream);
        Exception ret = null;
        Element root = doc.getDocumentElement();

        if (root != null) {
            String rootName = root.getNodeName();

            if (rootName.equals(ERROR_LIST_TAG)) {
                NodeList list = doc.getElementsByTagName(ERROR_TAG);
                if (list != null && list.getLength() > 0) {
                    Map<String, ErrorMessageResult> results = new HashMap<>();

                    for (int i = 0; i < list.getLength(); i++) {
                        String receiptHandle = parseReceiptHandle((Element) list.item(i));
                        ErrorMessageResult result = parseErrorResult((Element) list.item(i));
                        results.put(receiptHandle, result);

                    }
                    ret = new BatchDeleteException(results);
                }
            } else if (rootName.equals(ERROR_TAG)) {
                String code = safeGetElementContent(root, ERROR_CODE_TAG, "");
                String message = safeGetElementContent(root, ERROR_MESSAGE_TAG, "");
                String requestId = safeGetElementContent(root, ERROR_REQUEST_ID_TAG, "");
                String hostId = safeGetElementContent(root, ERROR_HOST_ID_TAG, "");
                ret = new ServiceException(message, null, code, requestId, hostId);
            }
        }
        return ret;
    }

    private String parseReceiptHandle(Element root) {
        return safeGetElementContent(root, RECEIPT_HANDLE_TAG,
            null);
    }

    private ErrorMessageResult parseErrorResult(Element root) {
        ErrorMessageResult result = new ErrorMessageResult();
        String errorCode = safeGetElementContent(root, MESSAGE_ERROR_CODE_TAG,
            null);
        result.setErrorCode(errorCode);

        String errorMessage = safeGetElementContent(root,
                MESSAGE_ERROR_MESSAGE_TAG, null);
        result.setErrorMessage(errorMessage);
        return result;
    }
}
