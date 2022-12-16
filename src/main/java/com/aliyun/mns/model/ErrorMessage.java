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
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error", namespace = MNSConstants.DEFAULT_XML_NAMESPACE)
public class ErrorMessage {
    @XmlElement(name = "Code", namespace = MNSConstants.DEFAULT_XML_NAMESPACE)
    public String Code;

    @XmlElement(name = "Message", namespace = MNSConstants.DEFAULT_XML_NAMESPACE)
    public String Message;

    @XmlElement(name = "RequestId", namespace = MNSConstants.DEFAULT_XML_NAMESPACE)
    public String RequestId;

    @XmlElement(name = "Method", namespace = MNSConstants.DEFAULT_XML_NAMESPACE)
    public String Method;

    @XmlElement(name = "HostId", namespace = MNSConstants.DEFAULT_XML_NAMESPACE)
    public String HostId;
}
