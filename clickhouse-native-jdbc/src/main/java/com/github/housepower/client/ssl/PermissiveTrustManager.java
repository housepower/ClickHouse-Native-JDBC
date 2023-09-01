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

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * An implementation of X509TrustManager that trusts all certificates.
 * This class is not secure and should only be used for debugging or
 * in a completely isolated environment.
 */
public class PermissiveTrustManager implements X509TrustManager {

    /**
     * Checks the client certificates but does nothing.
     * It effectively trusts all client certificates.
     *
     * @param x509Certificates Array of client certificates to check
     * @param s The auth type (e.g., "RSA", "DSS")
     */
    @Override
    public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {
        // Do nothing to bypass client checks
    }

    /**
     * Checks the server certificates but does nothing.
     * It effectively trusts all server certificates.
     *
     * @param x509Certificates Array of server certificates to check
     * @param s The auth type (e.g., "RSA", "DSS")
     */
    @Override
    public void checkServerTrusted(X509Certificate[] x509Certificates, String s) {
        // Do nothing to bypass server checks
    }

    /**
     * Returns an empty array of certificate authorities, indicating
     * that all certificates are trusted, subject to the
     * verification done in the checkClientTrusted and checkServerTrusted methods.
     *
     * @return An empty X509Certificate array
     */
    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }
}
