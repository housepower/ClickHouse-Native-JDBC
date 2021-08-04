package com.github.housepower.client.ssl;

import javax.net.ssl.X509TrustManager;
import java.security.cert.X509Certificate;

/**
 * An insecure {@link javax.net.ssl.TrustManager}, that don't validate the certificate.
 */
public class NonValidatingTrustManager implements X509TrustManager {

    public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[0];
    }

    public void checkClientTrusted(X509Certificate[] certs, String authType) {
    }

    public void checkServerTrusted(X509Certificate[] certs, String authType) {
    }
}
