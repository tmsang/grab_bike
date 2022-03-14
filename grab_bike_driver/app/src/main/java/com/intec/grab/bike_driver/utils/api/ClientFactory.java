package com.intec.grab.bike_driver.utils.api;

import com.intec.grab.bike_driver.utils.auth.ApiKeyAuth;
import com.intec.grab.bike_driver.utils.auth.CertUtils;
import com.intec.grab.bike_driver.utils.auth.HttpBasicAuth;

public class ClientFactory {
    public static ApiClient unauthorized(
            String baseUrl, SSLSettings sslSettings) {
        return defaultClient(new String[0], baseUrl + "/", sslSettings);
    }

    public static ApiClient basicAuth(
            String baseUrl, SSLSettings sslSettings, String username, String password) {
        ApiClient client = defaultClient(new String[] {"basicAuth"}, baseUrl + "/", sslSettings);
        HttpBasicAuth auth = (HttpBasicAuth) client.getApiAuthorizations().get("basicAuth");
        auth.setUsername(username);
        auth.setPassword(password);
        return client;
    }

    public static ApiClient clientToken(String baseUrl, SSLSettings sslSettings, String token) {
        ApiClient client =
                defaultClient(new String[] {"clientTokenHeader"}, baseUrl + "/", sslSettings);
        ApiKeyAuth tokenAuth = (ApiKeyAuth) client.getApiAuthorizations().get("clientTokenHeader");
        tokenAuth.setApiKey(token);
        return client;
    }

    private static ApiClient defaultClient(
            String[] authentications, String baseUrl, SSLSettings sslSettings) {
        ApiClient client = new ApiClient(authentications);
        CertUtils.applySslSettings(client.getOkBuilder(), sslSettings);
        client.getAdapterBuilder().baseUrl(baseUrl);
        return client;
    }
}
