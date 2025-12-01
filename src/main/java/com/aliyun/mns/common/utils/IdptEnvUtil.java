package com.aliyun.mns.common.utils;

import java.io.InputStream;
import java.util.Properties;

/**
 * 独立输出云环境，对客透出的 API/错误码/角色名/策略名等 做不同替换处理
 *
 * @author yuanzhi
 * @date 2025/4/30.
 */
public class IdptEnvUtil {
    private static volatile Boolean isIdptEnv;

    private static void lazyInit() {
        if (isIdptEnv == null) {
            synchronized (IdptEnvUtil.class) {
                if (isIdptEnv == null) {
                    loadConfigProperties();
                }
            }
        }
    }

    private static void loadConfigProperties() {
        // 加载 config.properties 文件
        try (InputStream input = IdptEnvUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new IllegalStateException("config.properties file not found");
            }
            Properties properties = new Properties();
            properties.load(input);
            isIdptEnv = Boolean.TRUE.toString().equalsIgnoreCase(properties.getProperty("idpt.env"));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load config.properties file", e);
        }
    }

    public static boolean isIdptEnv() {
        lazyInit();
        return isIdptEnv;
    }

}
