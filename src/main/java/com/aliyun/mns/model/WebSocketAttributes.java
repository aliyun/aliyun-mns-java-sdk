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

import com.aliyun.mns.common.MNSConstants;

public class WebSocketAttributes implements BaseAttributes {
    private Integer importanceLevel;

    @Override
    public AttributesValidationResult validate() {
        AttributesValidationResult result = new AttributesValidationResult();

        if (importanceLevel < MNSConstants.MIN_IMPORTANCE
            || importanceLevel > MNSConstants.MAX_IMPORTANCE) {
            result.setSuccess(false);
            result.setMessage("Invalid ImportanceLevel: " + importanceLevel);
            return result;
        }

        result.setSuccess(true);
        return result;
    }

    public Integer getImportanceLevel() {
        return importanceLevel;
    }

    public void setImportanceLevel(Integer importanceLevel) {
        this.importanceLevel = importanceLevel;
    }
}
