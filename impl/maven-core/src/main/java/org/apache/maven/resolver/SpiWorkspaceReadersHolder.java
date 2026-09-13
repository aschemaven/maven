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
package org.apache.maven.resolver;

import java.util.Map;

import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.spi.WorkspaceReader;

/**
 * Collects the {@link WorkspaceReader} SPI implementations contributed by extensions.
 * <p>
 * Injecting a {@code Map} into a holder is what reaches implementations contributed by core
 * extensions; {@code lookupList(WorkspaceReader.class)}, which goes to the Plexus container, was
 * measured to return an empty list for the very same setup. Core extension realms are read into
 * the Maven 4 DI injector — see {@code PlexusContainerCapsuleFactory}, which calls
 * {@code Injector.discover(extension.entry().getClassRealm())} — and this holder's constructor
 * parameter is resolved by that injector.
 * <p>
 * The same shape is used by {@code PropertyContributorsHolder} in maven-cli. Looking the holder up
 * lazily also means the injection happens after core extensions have been loaded.
 */
@Named
public final class SpiWorkspaceReadersHolder {

    private final Map<String, WorkspaceReader> workspaceReaders;

    @Inject
    public SpiWorkspaceReadersHolder(Map<String, WorkspaceReader> workspaceReaders) {
        this.workspaceReaders = workspaceReaders;
    }

    public Map<String, WorkspaceReader> getWorkspaceReaders() {
        return workspaceReaders;
    }
}
