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

package com.aliyun.mns.common.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServiceSettings {

    private static final String SETTINGS_FILE_NAME =
        System.getProperty("user.home") +
            System.getProperty("file.separator") +
            ".aliyun-mns.properties";

    private static final Logger log = LoggerFactory.getLogger(ServiceSettings.class);

    private static Properties properties = new Properties();

    static {
        load();
    }

    public static String getMNSAccountEndpoint() {
        return properties.getProperty("mns.accountendpoint");
    }

    public static void setMNSAccountEndpoint(String accountEndpoint) {
        properties.setProperty("mns.accountendpoint", accountEndpoint);
    }

    public static String getMNSSecurityToken() {
        return properties.getProperty("mns.securitytoken");
    }

    public static void setMNSSecurityToken(String securityToken) {
        properties.setProperty("mns.securitytoken", securityToken);
    }

    /**
     * 获得指定 key 的 配置值
     */
    public static String getMNSPropertyValue(String propertyKey, String defaultValue) {
        if (StringUtils.isBlank(propertyKey)) {
            return defaultValue;
        }
        return properties.getProperty("mns." + propertyKey, defaultValue);
    }

    /**
     * @deprecated 该方案不再推荐使用，请使用非硬编码形式：https://help.aliyun.com/zh/sdk/developer-reference/ak-security-scheme?#faa5f4905bxkr
     */
    @Deprecated
    public static String getMNSAccessKeyId() {
        return properties.getProperty("mns.accesskeyid");
    }

    /**
     * @deprecated 该方案不再推荐使用，请使用非硬编码形式：https://help.aliyun.com/zh/sdk/developer-reference/ak-security-scheme?#faa5f4905bxkr
     */
    @Deprecated
    public static void setMNSAccessKeyId(String accessKeyId) {
        properties.setProperty("mns.accesskeyid", accessKeyId);
    }

    /**
     * @deprecated 该方案不再推荐使用，请使用非硬编码形式：https://help.aliyun.com/zh/sdk/developer-reference/ak-security-scheme?#faa5f4905bxkr
     */
    @Deprecated
    public static String getMNSAccessKeySecret() {
        return properties.getProperty("mns.accesskeysecret");
    }

    /**
     * @deprecated 该方案不再推荐使用，请使用非硬编码形式：https://help.aliyun.com/zh/sdk/developer-reference/ak-security-scheme?#faa5f4905bxkr
     */
    @Deprecated
    public static void setMNSAccessKeySecret(String accessKeySecret) {
        properties.setProperty("mns.accesskeysecret", accessKeySecret);
    }

    /**
     * Load settings from the configuration file.
     * <p>
     * The configuration format: mns.endpoint= mns.accesskeyid= mns.accesskeysecret= proxy.host= proxy.port=
     * </p>
     */
    public static void load() {
        InputStream is = null;
        try {
            is = new FileInputStream(SETTINGS_FILE_NAME);
            properties.load(is);
        } catch (FileNotFoundException e) {
            log.warn("The settings file '" + SETTINGS_FILE_NAME + "' does not exist.");
        } catch (IOException e) {
            log.warn("Failed to load the settings from the file: " + SETTINGS_FILE_NAME);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Load settings from a given configuration file.
     *
     * @param configFile configuration file name
     */
    public static void load(String configFile) {
        InputStream is = null;
        try {
            is = new FileInputStream(configFile);
            properties.load(is);
        } catch (FileNotFoundException e) {
            log.warn("The settings file '" + configFile + "' does not exist.");
        } catch (IOException e) {
            log.warn("Failed to load the settings from the file: " + configFile);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

    }
}
