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

package com.github.housepower.settings;

public class KeyStoreConfig {
    private String keyStoreType;
    private String keyStorePath;
    private String keyStorePassword;

    public KeyStoreConfig() {
    }

    public KeyStoreConfig(String keyStoreType, String keyStorePath, String keyStorePassword) {
        this.keyStoreType = keyStoreType;
        this.keyStorePath = keyStorePath;
        this.keyStorePassword = keyStorePassword;
    }

    public String getKeyStorePath() {
        return this.keyStorePath;
    }

    public String getKeyStorePassword() {
        return this.keyStorePassword;
    }

    public String getKeyStoreType() {
        return this.keyStoreType;
    }
}
