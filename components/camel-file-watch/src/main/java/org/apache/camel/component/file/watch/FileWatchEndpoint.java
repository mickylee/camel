/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.component.file.watch;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.camel.Consumer;
import org.apache.camel.MultipleConsumersSupport;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.component.file.watch.constants.FileEventEnum;
import org.apache.camel.spi.Metadata;
import org.apache.camel.spi.UriEndpoint;
import org.apache.camel.spi.UriParam;
import org.apache.camel.spi.UriPath;
import org.apache.camel.support.DefaultEndpoint;

/**
 * The file-watch is used to monitor file events in directory using {@link java.nio.file.WatchService}
 */
@UriEndpoint(firstVersion = "3.0.0", scheme = "file-watch", title = "file-watch", syntax = "file-watch:path",
label = "file", consumerOnly = true)
public class FileWatchEndpoint extends DefaultEndpoint implements MultipleConsumersSupport {
    @UriPath(label = "consumer", description = "Path of directory to consume events from.")
    @Metadata(required = true)
    private String path;

    @UriParam(label = "consumer",
    description = "Coma separated list of events to watch. Allowed values are: CREATE, MODIFY, DELETE.",
    defaultValue = "CREATE,MODIFY,DELETE")
    private Set<FileEventEnum> events = new HashSet<>(Arrays.asList(FileEventEnum.values()));

    @UriParam(label = "consumer", description = "Auto create directory if does not exists.", defaultValue = "true")
    private boolean autoCreate = true;

    @UriParam(label = "consumer",
    description = "The number of concurrent consumers. Increase this value, if your route is slow to prevent buffering in queue.",
    defaultValue = "1")
    private int concurrentConsumers = 1;

    @UriParam(label = "consumer",
    description = "ANT style pattern to match files. The file is matched against path relative to endpoint path. "
    + "Pattern must be also relative (not starting with slash)",
    defaultValue = "**")
    private String antInclude;

    @UriParam(label = "consumer", description = "Maximum size of queue between WatchService and consumer. Unbounded by default.",
    defaultValue = "" + Integer.MAX_VALUE)
    private int queueSize = Integer.MAX_VALUE;

    public FileWatchEndpoint() {
    }

    public FileWatchEndpoint(String uri, FileWatchComponent component) {
        super(uri, component);
    }

    public FileWatchEndpoint(String uri, String remaining, FileWatchComponent component) {
        super(uri, component);
        setPath(remaining);
    }

    public Producer createProducer() throws Exception {
        throw new UnsupportedOperationException("This component does not support producer");
    }

    @Override
    public Consumer createConsumer(Processor processor) throws Exception {
        return new FileWatchConsumer(this, processor);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    Set<FileEventEnum> getEvents() {
        return events;
    }

    public void setEvents(Set<FileEventEnum> events) {
        this.events = events;
    }

    @SuppressWarnings("unused") //called via reflection
    public void setEvents(String commaSeparatedEvents) {
        String[] stringArray = commaSeparatedEvents.split(",");
        Set<FileEventEnum> eventsSet = new HashSet<>();
        for (String event : stringArray) {
            eventsSet.add(FileEventEnum.valueOf(event.trim()));
        }
        events = eventsSet.isEmpty() ? new HashSet<>(Arrays.asList(FileEventEnum.values())) : eventsSet;
    }

    public boolean isAutoCreate() {
        return autoCreate;
    }

    public void setAutoCreate(boolean autoCreate) {
        this.autoCreate = autoCreate;
    }

    public int getConcurrentConsumers() {
        return concurrentConsumers;
    }

    public void setConcurrentConsumers(int concurrentConsumers) {
        this.concurrentConsumers = concurrentConsumers;
    }

    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
    }

    public String getAntInclude() {
        return antInclude;
    }

    public void setAntInclude(String antInclude) {
        this.antInclude = antInclude;
    }

    @Override
    public FileWatchComponent getComponent() {
        return (FileWatchComponent) super.getComponent();
    }

    @Override
    public boolean isMultipleConsumersSupported() {
        return true;
    }
}