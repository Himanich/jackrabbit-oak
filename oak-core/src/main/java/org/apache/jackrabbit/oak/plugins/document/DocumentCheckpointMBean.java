/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.jackrabbit.oak.plugins.document;

import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.TabularDataSupport;

import org.apache.jackrabbit.oak.util.AbstractCheckpointMBean;

/**
 * {@code CheckpointMBean} implementation for the {@code DocumentNodeStore}.
 */
public class DocumentCheckpointMBean extends AbstractCheckpointMBean {
    private final DocumentNodeStore store;

    public DocumentCheckpointMBean(DocumentNodeStore store) {
        this.store = store;
    }

    @Override
    protected void collectCheckpoints(TabularDataSupport tab) throws OpenDataException {
        Map<Revision, String> checkpoints = store.getCheckpoints().getCheckpoints();
        if (checkpoints == null) {
            checkpoints = Collections.emptyMap();
        }

        for (Entry<Revision, String> checkpoint : checkpoints.entrySet()) {
            String id = checkpoint.getKey().toString();
            Date created = new Date(checkpoint.getKey().getTimestamp());
            Date expires = new Date(Long.parseLong(checkpoint.getValue()));
            tab.put(id, toCompositeData(
                    id, created.toString(), expires.toString(), store.checkpointInfo(id)));
        }
    }

    @Override
    public String createCheckpoint(long lifetime) {
        return store.checkpoint(lifetime);
    }

    @Override
    public boolean releaseCheckpoint(String checkpoint) {
        return store.release(checkpoint);
    }

}
