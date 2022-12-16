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

public class ListObjectRequest extends AbstractRequest {
    private String prefix;
    private String marker;
    private Integer maxRet;
    private Boolean withMeta;

    public ListObjectRequest() {
        this.prefix = null;
        this.marker = null;
        this.maxRet = null;
        this.withMeta = null;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getMarker() {
        return marker;
    }

    public void setMarker(String marker) {
        this.marker = marker;
    }

    public Integer getMaxRet() {
        return maxRet;
    }

    public void setMaxRet(Integer maxRet) {
        this.maxRet = maxRet;
    }

    public Boolean getWithMeta() {
        return withMeta;
    }

    public void setWithMeta(Boolean withMeta) {
        this.withMeta = withMeta;
    }

}
