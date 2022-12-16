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

package com.aliyun.mns.model.serialize.topic;

import com.aliyun.mns.model.PagingListResult;
import com.aliyun.mns.model.SubscriptionMeta;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static com.aliyun.mns.common.MNSConstants.NEXT_MARKER_TAG;
import static com.aliyun.mns.common.MNSConstants.SUBSCRIPTION_TAG;

public class SubscriptionArraryDeserializer extends AbstractSubscriptionDeserializer<PagingListResult<SubscriptionMeta>> {
    public PagingListResult<SubscriptionMeta> deserialize(InputStream stream) throws Exception {
        Document doc = getDocmentBuilder().parse(stream);
        NodeList list = doc.getElementsByTagName(SUBSCRIPTION_TAG);
        List<SubscriptionMeta> topics = new ArrayList<SubscriptionMeta>();

        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            topics.add(parseMeta(e));
        }

        PagingListResult<SubscriptionMeta> result = null;
        if (topics.size() > 0) {
            result = new PagingListResult<SubscriptionMeta>();
            list = doc.getElementsByTagName(NEXT_MARKER_TAG);
            if (list.getLength() > 0) {
                result.setMarker(list.item(0).getTextContent());
            }
            result.setResult(topics);
        }
        return result;
    }
}
