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

public class MessageAttributes implements BaseAttributes {
    private MailAttributes mailAttributes;
    private DayuAttributes dayuAttributes;
    private SmsAttributes smsAttributes;
    private WebSocketAttributes webSocketAttributes;
    private BatchSmsAttributes batchSmsAttributes;
    private PushAttributes pushAttributes;

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result;
        if (mailAttributes != null) {
            result = mailAttributes.validate();
            if (!result.isSuccess()) {
                return result;
            }
        }

        if (dayuAttributes != null) {
            result = dayuAttributes.validate();
            if (!result.isSuccess()) {
                return result;
            }
        }

        if (smsAttributes != null) {
            result = smsAttributes.validate();
            if (!result.isSuccess()) {
                return result;
            }
        }

        if (pushAttributes != null) {
            result = pushAttributes.validate();
            if (!result.isSuccess()) {
                return result;
            }
        }

        result = new AttributesValidationResult();
        result.setSuccess(true);
        return result;
    }

    public MailAttributes getMailAttributes() {
        return mailAttributes;
    }

    public void setMailAttributes(MailAttributes mailAttributes) {
        this.mailAttributes = mailAttributes;
    }

    public DayuAttributes getDayuAttributes() {
        return dayuAttributes;
    }

    public void setDayuAttributes(DayuAttributes dayuAttributes) {
        this.dayuAttributes = dayuAttributes;
    }

    public SmsAttributes getSmsAttributes() {
        return smsAttributes;
    }

    public void setSmsAttributes(SmsAttributes smsAttributes) {
        this.smsAttributes = smsAttributes;
    }

    public WebSocketAttributes getWebSocketAttributes() {
        return webSocketAttributes;
    }

    public void setWebSocketAttributes(WebSocketAttributes webSocketAttributes) {
        this.webSocketAttributes = webSocketAttributes;
    }

    public BatchSmsAttributes getBatchSmsAttributes() {
        return batchSmsAttributes;
    }

    public void setBatchSmsAttributes(BatchSmsAttributes batchSmsAttributes) {
        this.batchSmsAttributes = batchSmsAttributes;
    }

    public PushAttributes getPushAttributes() {
        return pushAttributes;
    }

    public void setPushAttributes(PushAttributes pushAttributes) {
        this.pushAttributes = pushAttributes;
    }
}
