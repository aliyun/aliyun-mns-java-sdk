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

package com.aliyun.mns.model.serialize.queue;

import com.aliyun.mns.model.PagingListResult;
import com.aliyun.mns.model.QueueMeta;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import static com.aliyun.mns.common.MNSConstants.NEXT_MARKER_TAG;
import static com.aliyun.mns.common.MNSConstants.QUEUE_TAG;

public class QueueArrayDeserializer extends AbstractQueueMetaDeserializer<PagingListResult<QueueMeta>> {

    @Override
    public PagingListResult<QueueMeta> deserialize(InputStream stream)
        throws Exception {

//		 byte[] bytes = new byte[1024];
//		 while(stream.read(bytes, 0, stream.available())>0){
//		 System.out.println(new String(bytes));
//		 }
        Document doc = getDocumentBuilder().parse(stream);
        NodeList list = doc.getElementsByTagName(QUEUE_TAG);

        List<QueueMeta> queues = new ArrayList<QueueMeta>();

        for (int i = 0; i < list.getLength(); i++) {
            Element e = (Element) list.item(i);
            queues.add(parseQueueMeta(e));
        }

        PagingListResult<QueueMeta> result = null;
        if (queues.size() > 0) {
            result = new PagingListResult<QueueMeta>();
            list = doc.getElementsByTagName(NEXT_MARKER_TAG);
            if (list.getLength() > 0) {
                result.setMarker(list.item(0).getTextContent());
            }
            result.setResult(queues);
        }
        return result;
    }

}
