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

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * please refer to https://help.aliyun.com/document_detail/48089.html
 */
public class PushAttributes implements BaseAttributes {
    private PushTarget target;
    private String targetValue;
    private PushDeviceType deviceType;
    private PushType pushType;
    private String title;
    private String body;

    private Map<String, String> params = new HashMap<String, String>();

    public enum PushTarget {
        DEVICE, ACCOUNT, ALIAS, TAG, ALL;
    }

    public enum PushDeviceType {
        iOS, ANDROID, ALL;
    }

    public enum PushType {
        MESSAGE, NOTICE;
    }

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();

        if (target == null) {
            result.setSuccess(false);
            result.setMessage("Invalid PushTarget");
            return result;
        }
        if (targetValue == null || (target == PushTarget.ALL && !"ALL".equals(targetValue))) {
            result.setSuccess(false);
            result.setMessage("Invalid TargetValue");
            return result;
        }
        if (deviceType == null) {
            result.setSuccess(false);
            result.setMessage("Invalid DeviceType");
            return result;
        }
        if (body == null) {
            result.setSuccess(false);
            result.setMessage("Invalid Body");
            return result;
        }
        if (title == null) {
            result.setSuccess(false);
            result.setMessage("Invalid Title");
            return result;
        }

        if (pushType == null) {
            pushType = PushType.MESSAGE; // use default value
        }
        result.setSuccess(true);
        return result;
    }

    public static class PushAttributesSerializer implements JsonSerializer<PushAttributes> {
        @Override
        public JsonElement serialize(PushAttributes pushAttributes, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("Target", pushAttributes.target.name());
            jsonObject.addProperty("TargetValue", pushAttributes.targetValue);
            jsonObject.addProperty("DeviceType", pushAttributes.deviceType.name());
            jsonObject.addProperty("PushType", pushAttributes.pushType.name());
            jsonObject.addProperty("Title", pushAttributes.title);
            jsonObject.addProperty("Body", pushAttributes.body);

            for (String key : pushAttributes.params.keySet()) {
                jsonObject.addProperty(key, pushAttributes.params.get(key));
            }
            return jsonObject;
        }
    }

    public void setParam(String key, String value) {
        if (value != null && !value.isEmpty()) {
            params.put(key, value);
        }
    }

    public PushTarget getTarget() {
        return target;
    }

    public void setTarget(PushTarget target) {
        this.target = target;
    }

    public String getTargetValue() {
        return targetValue;
    }

    public void setTargetValue(String targetValue) {
        this.targetValue = targetValue;
    }

    public PushDeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(PushDeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public PushType getPushType() {
        return pushType;
    }

    public void setPushType(PushType pushType) {
        this.pushType = pushType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
