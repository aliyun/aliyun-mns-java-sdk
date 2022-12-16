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

import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;

public class BatchSmsAttributes implements BaseAttributes {
    private String FreeSignName; // SMS Sign
    private String TemplateCode; // SMS TemplateCode
    private transient Map<String, Map<String, String>> SmsReceivers = new HashMap<String, Map<String, String>>(); //短信模板变量，key的名字须和申请模板中的变量名一致
    private String SmsParams;
    private String Type = "multiContent";
    private String ExtendCode;

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();

        if (FreeSignName == null || TemplateCode == null) {
            result.setSuccess(false);
            result.setMessage("Invalid Params");
            return result;
        }

        result.setSuccess(true);
        return result;
    }

    public String toJson(Gson gson) {
        SmsParams = gson.toJson(SmsReceivers);
        return gson.toJson(this);
    }

    public void addSmsReceiver(String phone, SmsReceiverParams params) {
        SmsReceivers.put(phone, params.getParams());
    }

    public static class SmsReceiverParams {
        private Map<String, String> SmsParamsMap;

        public SmsReceiverParams() {
            SmsParamsMap = new HashMap<String, String>();
        }

        public void setParam(String key, String value) {
            SmsParamsMap.put(key, value);
        }

        public Map<String, String> getParams() {
            return SmsParamsMap;
        }
    }

    public String getFreeSignName() {
        return FreeSignName;
    }

    public void setFreeSignName(String freeSignName) {
        FreeSignName = freeSignName;
    }

    public String getSmsParams() {
        return SmsParams;
    }

    public void setSmsParams(String smsParams) {
        SmsParams = smsParams;
    }

    public String getTemplateCode() {
        return TemplateCode;
    }

    public void setTemplateCode(String templateCode) {
        TemplateCode = templateCode;
    }

    public String getType() {
        return "multiContent";
    }

    public void setType(String type) {
    }

    public String getExtendCode() {
        return ExtendCode;
    }

    public void setExtendCode(String extendCode) {
        ExtendCode = extendCode;
    }
}
