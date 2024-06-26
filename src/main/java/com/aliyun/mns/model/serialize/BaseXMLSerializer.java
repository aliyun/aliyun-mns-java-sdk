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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class BaseXMLSerializer<T> {

    protected static DocumentBuilderFactory factory = DocumentBuilderFactory
        .newInstance();

    private static ThreadLocal<DocumentBuilder> sps = new ThreadLocal<DocumentBuilder>();

    /**
     * 该方法已废弃，请使用 {@link #getDocumentBuilder()}
     */
    @Deprecated
    protected DocumentBuilder getDocmentBuilder() throws ParserConfigurationException {
        DocumentBuilder db = sps.get();
        if (db == null) {
            db = factory.newDocumentBuilder();
            sps.set(db);
        }
        return db;
    }

    protected DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        DocumentBuilder db = sps.get();
        if (db == null) {
            db = factory.newDocumentBuilder();
            sps.set(db);
        }
        return db;
    }
}
