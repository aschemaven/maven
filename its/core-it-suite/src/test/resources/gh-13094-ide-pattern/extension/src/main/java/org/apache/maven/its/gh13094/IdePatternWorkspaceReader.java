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
package org.apache.maven.its.gh13094;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.maven.api.Artifact;
import org.apache.maven.api.di.Inject;
import org.apache.maven.api.di.Named;
import org.apache.maven.api.spi.WorkspaceReader;
import org.apache.maven.plugin.PluginRealmCache;

/**
 * Models what an IDE has to do under the design proposed in apache/maven#13094: serve workspace
 * artifacts through the new SPI, and purge stale plugin realms through
 * {@link PluginRealmCache#invalidate}.
 * <p>
 * The probe is the constructor. {@code PluginRealmCache} lives in {@code org.apache.maven.plugin}
 * (maven-core), while this class is discovered by the Maven 4 DI injector via
 * {@code org.apache.maven.api.di}. Whether the one can be injected into the other is exactly the
 * question the SPI has to answer for m2e and NetBeans — neither can drop its
 * {@code PluginDependenciesResolver} override otherwise.
 */
@Named("ide-pattern")
public class IdePatternWorkspaceReader implements WorkspaceReader {

    private final PluginRealmCache pluginRealmCache;

    @Inject
    public IdePatternWorkspaceReader(PluginRealmCache pluginRealmCache) {
        this.pluginRealmCache = pluginRealmCache;
        System.out.println("[IDE-PROBE] constructed, pluginRealmCache="
                + (pluginRealmCache == null ? "null" : pluginRealmCache.getClass().getName()));
    }

    @Override
    public Optional<Path> findArtifact(Artifact artifact) {
        return Optional.empty();
    }

    @Override
    public List<String> findVersions(Artifact artifact) {
        return Collections.emptyList();
    }
}
