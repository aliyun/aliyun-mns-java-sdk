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

package com.aliyun.mns.common;

import java.util.Map;

import com.aliyun.mns.model.ErrorMessageResult;

public class BatchDeleteException extends ServiceException {
    /**
     *
     */
    private static final long serialVersionUID = -7705861423905005565L;
    private Map<String, ErrorMessageResult> errorMessages;

    public BatchDeleteException(Map<String, ErrorMessageResult> errorMsgs) {
        this.errorMessages = errorMsgs;
        if (errorMessages !=null && !errorMessages.isEmpty()) {
            this.errorCode = errorMessages.entrySet().iterator().next().getValue().getErrorCode();
        }
    }

    public Map<String, ErrorMessageResult> getErrorMessages() {
        return errorMessages;
    }

    public void setErrorMessages(Map<String, ErrorMessageResult> errorMessages) {
        this.errorMessages = errorMessages;
    }

    @Override
    public String getMessage() {
        return this.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (errorMessages !=null && !errorMessages.isEmpty()) {
            sb.append("{");
            for (Map.Entry<String, ErrorMessageResult> entry : errorMessages.entrySet()) {
                sb.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue().toString())
                    .append(", ");
            }
            if (sb.length() > 1) {
                // 移除最后的逗号和空格
                sb.delete(sb.length() - 2, sb.length());
            }
            sb.append("}");
        }
        return sb.toString();
    }
}
