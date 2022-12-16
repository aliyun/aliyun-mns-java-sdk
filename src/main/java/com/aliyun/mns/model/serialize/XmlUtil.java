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

import java.io.OutputStream;
import java.io.StringWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Node;

public class XmlUtil {
    private static TransformerFactory transFactory = TransformerFactory.newInstance();

    public static void output(Node node, String encoding,
        OutputStream outputStream) throws TransformerException {
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty("encoding", encoding);

        DOMSource source = new DOMSource();
        source.setNode(node);

        StreamResult result = new StreamResult();
        result.setOutputStream(outputStream);

        transformer.transform(source, result);
    }

    public static String xmlNodeToString(Node node, String encoding)
        throws TransformerException {
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty("encoding", encoding);
        StringWriter strWtr = new StringWriter();

        DOMSource source = new DOMSource();
        source.setNode(node);
        StreamResult result = new StreamResult(strWtr);
        transformer.transform(source, result);
        return strWtr.toString();

    }
}
