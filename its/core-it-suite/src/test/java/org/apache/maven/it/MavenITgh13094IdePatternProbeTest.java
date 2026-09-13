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
package org.apache.maven.it;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

/**
 * Probe for the design proposed in <a href="https://github.com/apache/maven/pull/13094">#13094</a>.
 * <p>
 * The PR gives IDEs a supported way to <em>provide</em> workspace artifacts, and expects them to
 * keep plugin realms fresh through {@code PluginRealmCache.invalidate(Artifact)} instead of opting
 * out of plugin resolution. For m2e and NetBeans — the two integrations that override
 * {@code PluginDependenciesResolver} precisely because plugin realms cannot be purged (MNG-4194) —
 * that only works if an SPI implementation can actually obtain the cache.
 * <p>
 * The SPI is discovered by the Maven 4 DI injector via {@code org.apache.maven.api.di}, while
 * {@code PluginRealmCache} is a maven-core component in {@code org.apache.maven.plugin}. This test
 * asserts that one can be injected into the other, which is the precondition for either IDE
 * dropping its override.
 */
class MavenITgh13094IdePatternProbeTest extends AbstractMavenIntegrationTestCase {

    @Test
    void spiWorkspaceReaderCanObtainPluginRealmCache() throws Exception {
        Path testDir = extractResources("gh-13094-ide-pattern");

        Verifier extensionVerifier = newVerifier(testDir.resolve("extension"));
        extensionVerifier.deleteArtifacts("org.apache.maven.its.gh13094");
        extensionVerifier.addCliArgument("install");
        extensionVerifier.execute();
        extensionVerifier.verifyErrorFreeLog();

        Verifier clientVerifier = newVerifier(testDir.resolve("project"));
        clientVerifier.setAutoclean(false);
        clientVerifier.addCliArgument("validate");
        clientVerifier.execute();
        clientVerifier.verifyErrorFreeLog();

        // the extension must have been constructed at all — otherwise discovery is broken
        clientVerifier.verifyTextInLog("[IDE-PROBE] constructed");
        // ... and the cache must actually have arrived, not been silently null
        clientVerifier.verifyTextInLog("pluginRealmCache=org.apache.maven.plugin.DefaultPluginRealmCache");
    }
}
