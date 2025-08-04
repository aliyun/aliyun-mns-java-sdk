package com.aliyun.mns.model;


/**
 * @author liuyikai
 */
public class DmAttributes implements BaseAttributes {

    public String getMailAddress() {
        return mailAddress;
    }

    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Boolean getIsHtml() {
        return isHtml;
    }

    public void setIsHtml(Boolean isHtml) {
        this.isHtml = isHtml;
    }

    private String mailAddress;

    private String subject;

    private Boolean isHtml;

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();
        result.setSuccess(true);
        return result;
    }

}
