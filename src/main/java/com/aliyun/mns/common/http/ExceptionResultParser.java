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

package com.aliyun.mns.common.http;

import com.aliyun.mns.common.ClientException;
import com.aliyun.mns.common.ServiceException;
import com.aliyun.mns.common.parser.JAXBResultParser;
import com.aliyun.mns.common.parser.ResultParseException;
import com.aliyun.mns.common.parser.ResultParser;
import com.aliyun.mns.common.utils.IOUtils;
import com.aliyun.mns.model.ErrorMessage;
import java.io.IOException;

public class ExceptionResultParser implements ResultParser<Exception> {
    private String userRequestId;

    public ExceptionResultParser(String userRequestId) {
        super();
        this.userRequestId = userRequestId;
    }

    @Override
    public Exception parse(ResponseMessage response) throws ResultParseException {
        assert response != null;

        if (response.isSuccessful()) {
            return null;
        }

        Exception result = null;
        String content = null;
        try {
            content = IOUtils.readStreamAsString(response.getContent(), "UTF-8");
        } catch (IOException e) {
            return new ServiceException(e.getMessage(), userRequestId, e);
        }

        try {
            // 使用jaxb common parser
            JAXBResultParser d = new JAXBResultParser(ErrorMessage.class);
            Object obj = d.parse(content);
            if (obj instanceof ErrorMessage) {
                ErrorMessage err = (ErrorMessage) obj;
                result = new ServiceException(err.Message, null, err.Code, err.RequestId, err.HostId);
            }
        } catch (Exception e) {
            // now treat it as unknown formats
            String message = e.getMessage() + "\n" + content;
            result = new ClientException(message, null, e.getCause());
        }

        return result;
    }

}
