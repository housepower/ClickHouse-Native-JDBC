package com.github.housepower.client.ssl;

import com.github.housepower.settings.ClickHouseConfig;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Iterator;

public class SSLContextBuilder {

    ClickHouseConfig configure;

    public SSLContextBuilder(ClickHouseConfig configure) {
        this.configure = configure;
    }

    public SSLContext getSSLContext()
            throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException, KeyManagementException {
        SSLContext ctx = SSLContext.getInstance("TLS");
        TrustManager[] tms = null;
        KeyManager[] kms = null;
        SecureRandom sr = null;
        String sslMode = configure.sslMode();

        if (sslMode.equals("none")) {
            tms = new TrustManager[]{new NonValidatingTrustManager()};
            kms = new KeyManager[]{};
            sr = new SecureRandom();
        } else if (sslMode.equals("strict")) {
            if (!configure.sslRootCertificate().isEmpty()) {
                TrustManagerFactory tmf = TrustManagerFactory
                        .getInstance(TrustManagerFactory.getDefaultAlgorithm());

                tmf.init(getKeyStore());
                tms = tmf.getTrustManagers();
                kms = new KeyManager[]{};
                sr = new SecureRandom();
            }
        } else {
            throw new IllegalArgumentException("unknown ssl mode '"+ sslMode +"'");
        }

        ctx.init(kms, tms, sr);
        return ctx;
    }

    private KeyStore getKeyStore()
            throws NoSuchAlgorithmException, IOException, CertificateException, KeyStoreException {
        KeyStore ks;
        try {
            ks = KeyStore.getInstance("jks");
            ks.load(null, null); // needed to initialize the key store
        } catch (KeyStoreException e) {
            throw new NoSuchAlgorithmException("jks KeyStore not available");
        }

        InputStream caInputStream;
        try {
            caInputStream = new FileInputStream(configure.sslRootCertificate());
        } catch (FileNotFoundException ex) {
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            caInputStream = cl.getResourceAsStream(configure.sslRootCertificate());
            if(caInputStream == null) {
                throw new IOException(
                        "Could not open SSL/TLS root certificate file '" + configure.sslRootCertificate() + "'", ex);
            }
        }

        try {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            Iterator<? extends Certificate> caIt = cf.generateCertificates(caInputStream).iterator();
            for (int i = 0; caIt.hasNext(); i++) {
                ks.setCertificateEntry("cert" + i, caIt.next());
            }
            return ks;
        } finally {
            caInputStream.close();
        }
    }
}
