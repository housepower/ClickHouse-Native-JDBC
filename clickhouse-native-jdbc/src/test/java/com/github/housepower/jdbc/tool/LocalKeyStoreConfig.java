/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.housepower.jdbc.tool;

import com.github.housepower.settings.KeyStoreConfig;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;

public class LocalKeyStoreConfig extends KeyStoreConfig {
    @Override
    public String getKeyStorePath() {
        return getKeyStoreAbsolutePath();
    }

    @Override
    public String getKeyStoreType() {
        return KeyStore.getDefaultType();
    }

    @Override
    public String getKeyStorePassword() {
        return "mypassword";
    }

    private String getKeyStoreAbsolutePath() {
        Path jksPath = Paths.get("src", "test", "resources", "clickhouse", "server.jks");
        return jksPath.toFile().getAbsolutePath();
    }
}
