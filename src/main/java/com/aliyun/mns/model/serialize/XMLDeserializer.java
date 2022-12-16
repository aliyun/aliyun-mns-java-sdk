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

package com.aliyun.mns.model.serialize;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class XMLDeserializer<T> extends BaseXMLSerializer<T> implements Deserializer<T> {

    public String safeGetElementContent(Element root, String tagName,
        String defualValue) {
        NodeList nodes = root.getElementsByTagName(tagName);
        if (nodes != null) {
            Node node = nodes.item(0);
            if (node == null) {
                return defualValue;
            } else {
                return node.getTextContent();
            }
        }
        return defualValue;
    }

}
