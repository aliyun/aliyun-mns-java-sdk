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

/**
 * This class is no longer recommended for use
 */
@Deprecated
public class DayuAttributes implements BaseAttributes {
    private String FreeSignName; // 阿里大鱼控制台里的短信签名
    private String TemplateCode; // 阿里大鱼控制台里的短信模板ID
    private transient Map<String, String> SmsParamsMap = new HashMap<String, String>(); //短信模板变量，key的名字须和申请模板中的变量名一致
    private String SmsParams;

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();
        result.setSuccess(true);
        return result;
    }

    @Override
    public String toJson(Gson gson) {
        SmsParams = gson.toJson(SmsParamsMap);
        return gson.toJson(this);
    }

    public String getFreeSignName() {
        return FreeSignName;
    }

    public void setFreeSignName(String freeSignName) {
        this.FreeSignName = freeSignName;
    }

    public String getSmsParams() {
        return SmsParams;
    }

    private void setSmsParams(String smsParams) {
        this.SmsParams = smsParams;
    }

    public String getTemplateCode() {
        return TemplateCode;
    }

    public void setTemplateCode(String templateCode) {
        this.TemplateCode = templateCode;
    }

    public void setSmsParam(String key, String value) {
        SmsParamsMap.put(key, value);
    }
}
