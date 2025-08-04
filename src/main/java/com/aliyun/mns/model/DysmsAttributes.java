package com.aliyun.mns.model;

import org.apache.commons.lang3.StringUtils;

/**
 * @author liuyikai
 */
public class DysmsAttributes implements BaseAttributes {

    private String phoneNumber;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();
        result.setSuccess(true);
        if (StringUtils.isNotBlank(phoneNumber) && !StringUtils.isNumeric(phoneNumber)) {
            result.setSuccess(false);
            result.setMessage("Invalid phone number.");
        }

        return result;
    }

}
