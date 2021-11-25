package com.intec.grab.bike.utils.api;

public class SSLSettings {
    public boolean validateSSL;
    public String cert;

    public SSLSettings(boolean validateSSL, String cert) {
        this.validateSSL = validateSSL;
        this.cert = cert;
    }
}
