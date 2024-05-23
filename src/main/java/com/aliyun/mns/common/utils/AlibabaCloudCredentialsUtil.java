package com.aliyun.mns.common.utils;

import com.aliyuncs.auth.AlibabaCloudCredentials;
import com.aliyuncs.auth.BasicSessionCredentials;
import com.aliyuncs.auth.InstanceProfileCredentials;

/**
 * @author zxg
 * @date 2024/05/22
 * @Description if it has bugs.please forgive me ~~~
 * @Description 待添加详细描述
 */
public class AlibabaCloudCredentialsUtil {


    /**
     * 通过credential provider获取security token
     *
     * @return securityToken.
     */
    public static String getSecurityToken(AlibabaCloudCredentials credential) {
        if (credential == null) {
            return null;
        }

        String tmpSecurityToken;
        try {
            if (credential instanceof InstanceProfileCredentials) {
                tmpSecurityToken = ((InstanceProfileCredentials) credential).getSessionToken();
                return tmpSecurityToken;
            }
            if (credential instanceof BasicSessionCredentials){
                return ((BasicSessionCredentials) credential).getSessionToken();
            }
        } catch (Exception e) {
            tmpSecurityToken = null;
        }
        return null;
    }
}
