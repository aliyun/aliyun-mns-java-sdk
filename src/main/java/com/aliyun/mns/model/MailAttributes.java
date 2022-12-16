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

public class MailAttributes implements BaseAttributes {
    /*
     * 参数定义请参照 https://help.aliyun.com/document_detail/directmail/api-reference/sendmail-related/SingleSendMail.html
     */
    private String subject; // 邮件主题,必须提供
    private String accountName; // 发信地址,必须提供
    private boolean replyToAddress = false; // 是否允许回信
    private int addressType = 0; // 发信账号类型
    private boolean isHtml = false;  // 邮件正文是否是html

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();

        if (subject == null || subject.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("No Subject in MailAttributes");
            return result;
        }

        if (accountName == null || accountName.isEmpty()) {
            result.setSuccess(false);
            result.setMessage("No AccountName in MailAttributes");
            return result;
        }

        result.setSuccess(true);
        return result;
    }

    public String toJson(Gson gson) {
        return gson.toJson(this);
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public int getAddressType() {
        return addressType;
    }

    public void setAddressType(int addressType) {
        this.addressType = addressType;
    }

    public boolean isHtml() {
        return isHtml;
    }

    public void setHtml(boolean html) {
        isHtml = html;
    }

    public boolean isReplyToAddress() {
        return replyToAddress;
    }

    public void setReplyToAddress(boolean replyToAddress) {
        this.replyToAddress = replyToAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }
}
