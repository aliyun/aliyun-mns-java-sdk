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

import org.apache.commons.lang3.StringUtils;

/**
 * Utils for common coding.
 *
 * 
 */
public class CodingUtils {
    private static ResourceManager rm = ResourceManager
        .getInstance(ServiceConstants.RESOURCE_NAME_COMMON);

    public static ResourceManager getResourceManager() {
        return rm;
    }

    public static void assertParameterNotNull(Object param, String paramName) {
        if (param == null) {
            throw new NullPointerException(rm.getFormattedString(
                "ParameterIsNull", paramName));
        }
    }

    public static void assertStringNotEmpty(String param, String paramName) {
        if (StringUtils.isEmpty(param)) {
            throw new IllegalArgumentException(rm.getFormattedString(
                "ParameterStringIsEmpty", paramName));
        }
    }

}
