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

package com.github.housepower.client.ssl;

import com.github.housepower.client.NativeClient;
import com.github.housepower.log.Logger;
import com.github.housepower.log.LoggerFactory;
import com.github.housepower.settings.ClickHouseConfig;
import com.github.housepower.settings.KeyStoreConfig;
import com.github.housepower.settings.SettingKey;


import javax.net.ssl.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.*;
import java.security.cert.CertificateException;

public class SSLContextBuilder {
    private static final Logger LOG = LoggerFactory.getLogger(NativeClient.class);

    private ClickHouseConfig config;

    private KeyStoreConfig keyStoreConfig;

    public SSLContextBuilder(ClickHouseConfig config) {
        this.config = config;
        this.keyStoreConfig = new KeyStoreConfig(
                (String) config.settings().get(SettingKey.keyStoreType),
                (String) config.settings().get(SettingKey.keyStorePath),
                (String) config.settings().get(SettingKey.keyStorePassword)
        );
    }

    public SSLContext getSSLContext() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        TrustManager[] trustManager = null;
        KeyManager[] keyManager = null;
        SecureRandom secureRandom = new SecureRandom();
        String sslMode = config.sslMode();
        LOG.debug("Client SSL mode: '" + sslMode + "'");

        switch (sslMode) {
            case "disabled":
                trustManager = new TrustManager[]{new PermissiveTrustManager()};
                keyManager = new KeyManager[]{};
                break;
            case "verify_ca":
                KeyStore keyStore = KeyStore.getInstance(keyStoreConfig.getKeyStoreType());
                keyStore.load(Files.newInputStream(Paths.get(keyStoreConfig.getKeyStorePath()).toFile().toPath()),
                        keyStoreConfig.getKeyStorePassword().toCharArray());

                KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
                keyManagerFactory.init(keyStore, keyStoreConfig.getKeyStorePassword().toCharArray());

                TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
                trustManagerFactory.init(keyStore);

                trustManager = trustManagerFactory.getTrustManagers();
                keyManager = keyManagerFactory.getKeyManagers();
                break;
            default:
                throw new IllegalArgumentException("Unknown SSL mode: '" + sslMode + "'");
        }

        sslContext.init(keyManager, trustManager, secureRandom);
        return sslContext;
    }

}
