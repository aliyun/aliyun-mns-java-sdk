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

package com.aliyun.mns.common.parser;

import com.aliyun.mns.common.http.ResponseMessage;
import com.aliyun.mns.common.utils.ResourceManager;
import com.aliyun.mns.common.utils.ServiceConstants;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implementation of ResultParser with JAXB.
 */
public class JAXBResultParser implements ResultParser<Object> {

    private static final SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
    // Because JAXBContext.newInstance() is a very slow method,
    // it can improve performance a lot to cache the instances of JAXBContext
    // for used context paths or class types.
    private static HashMap<Object, JAXBContext> cachedContexts = new HashMap<>();

    static {
        saxParserFactory.setNamespaceAware(true);
        saxParserFactory.setValidating(false);
    }

    // It allows to specify the class type, if the class type is specified,
    // the contextPath will be ignored.
    private Class<?> modelClass;

    public JAXBResultParser(Class<?> modelClass) {
        assert (modelClass != null);
        this.modelClass = modelClass;
    }

    private static synchronized void initJAXBContext(Class<?> c) throws JAXBException {
        if (!cachedContexts.containsKey(c)) {
            JAXBContext jc = JAXBContext.newInstance(c);
            cachedContexts.put(c, jc);
        }
    }

    private static SAXSource getSAXSource(InputStream content) throws SAXException, ParserConfigurationException {

        SAXParser saxParser = saxParserFactory.newSAXParser();
        return new SAXSource(saxParser.getXMLReader(), new InputSource(content));
    }

    public Object parse(String content) throws ResultParseException {
        assert (content != null);
        return parse(new ByteArrayInputStream(content.getBytes()));
    }

    public Object parse(ResponseMessage response) throws ResultParseException {
        assert (response != null && response.getContent() != null);
        return parse(response.getContent());
    }

    public Object parse(InputStream is) throws ResultParseException {
        assert (is != null);

        try {
            if (!cachedContexts.containsKey(modelClass)) {
                initJAXBContext(modelClass);
            }

            assert (cachedContexts.containsKey(modelClass));
            JAXBContext jc = cachedContexts.get(modelClass);
            Unmarshaller um = jc.createUnmarshaller();
            // It performs better to call Unmarshaller#unmarshal(Source)
            // than to call Unmarshaller#unmarshall(InputStream)
            // if XMLReader is specified in the SAXSource instance.
            return um.unmarshal(getSAXSource(is));
        } catch (JAXBException | SAXException | ParserConfigurationException e) {
            throw new ResultParseException(ResourceManager.getInstance(ServiceConstants.RESOURCE_NAME_COMMON)
                .getString("FailedToParseResponse"), e);
        }
    }
}