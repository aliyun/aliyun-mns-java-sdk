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

import java.util.Arrays;
import java.util.List;

public class MessageAttributes implements BaseAttributes {

    private MailAttributes mailAttributes;
    private DayuAttributes dayuAttributes;
    private SmsAttributes smsAttributes;
    private WebSocketAttributes webSocketAttributes;
    private BatchSmsAttributes batchSmsAttributes;
    private PushAttributes pushAttributes;
    private DysmsAttributes dysmsAttributes;
    private DmAttributes dmAttributes;

    @Override
    public AttributesValidationResult validate() {
        List<BaseAttributes> attributesList = Arrays.asList(mailAttributes,
                dayuAttributes, smsAttributes, pushAttributes, dysmsAttributes, dmAttributes);
        AttributesValidationResult result = new AttributesValidationResult();
        for (BaseAttributes attributes : attributesList) {
            if (attributes != null) {
                result = attributes.validate();
                if (!result.isSuccess()) {
                    return result;
                }
            }
        }

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

    public DysmsAttributes getDysmsAttributes() {
        return dysmsAttributes;
    }

    public void setDysmsAttributes(DysmsAttributes dysmsAttributes) {
        this.dysmsAttributes = dysmsAttributes;
    }

    public DmAttributes getDmAttributes() {
        return dmAttributes;
    }

    public void setDmAttributes(DmAttributes dmAttributes) {
        this.dmAttributes = dmAttributes;
    }

}
