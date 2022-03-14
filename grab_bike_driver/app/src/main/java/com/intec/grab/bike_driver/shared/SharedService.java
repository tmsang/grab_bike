package com.intec.grab.bike_driver.shared;

import android.content.Context;

import com.intec.grab.bike_driver.configs.Constants;
import com.intec.grab.bike_driver.forgot_password.ForgotPasswordApi;
import com.intec.grab.bike_driver.map.MapApi;
import com.intec.grab.bike_driver.login.LoginApi;
import com.intec.grab.bike_driver.messages.MessageApi;
import com.intec.grab.bike_driver.register.RegisterApi;
import com.intec.grab.bike_driver.reset_password.ResetPasswordApi;
import com.intec.grab.bike_driver.utils.api.ClientFactory;
import com.intec.grab.bike_driver.utils.helper.SQLiteHelper;
import com.intec.grab.bike_driver.utils.helper.CommonHelper;
import com.intec.grab.bike_driver.utils.api.SSLSettings;

public class SharedService {
    public static SharedService instance(Context context) {
        return new SharedService(context);
    }

    private Context _context;
    private SQLiteHelper _helper;

    public SharedService(Context context) {
        _context = context;
        _helper = new SQLiteHelper(context);
    }

    // load token in sqlite
    public String getToken() {
        String jwt = _helper.scalar("SELECT value from configs WHERE name = '" + Constants.ROW_KEY_TOKEN_JWT + "'");
        return jwt;
    }

    public void saveToken(String value) {
        _helper.action("INSERT OR REPLACE INTO configs(name, value) values('" + Constants.ROW_KEY_TOKEN_JWT + "', '" + value + "')");
    }

    // client check
    public int CheckValidAccount(String username, String password) {
        // check null or empty
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) return -1;

        // check policy password
        if (!CommonHelper.ValidPasswordRule(password)) return -2;

        // check exist in database (RestAPI from IRIS)
        // ...

        return 1;
    }


    // ========================================
    // CREATE INSTANCE from Interface (like DI)
    // ========================================

    //public static VersionApi versionApi(String baseUrl, SSLSettings sslSettings) {
    //    return ClientFactory.unauthorized(baseUrl, sslSettings).createService(VersionApi.class);
    //}

    public static LoginApi LoginApi(String baseUrl, SSLSettings sslSettings) {
        return ClientFactory.unauthorized(baseUrl, sslSettings).createService(LoginApi.class);
    }
    public static RegisterApi RegisterApi(String baseUrl, SSLSettings sslSettings) {
        return ClientFactory.unauthorized(baseUrl, sslSettings).createService(RegisterApi.class);
    }
    public static ForgotPasswordApi ForgotPasswordApi(String baseUrl, SSLSettings sslSettings) {
        return ClientFactory.unauthorized(baseUrl, sslSettings).createService(ForgotPasswordApi.class);
    }
    public static ResetPasswordApi ResetPasswordApi(String baseUrl, SSLSettings sslSettings) {
        return ClientFactory.unauthorized(baseUrl, sslSettings).createService(ResetPasswordApi.class);
    }
    public static MapApi MapApi(String baseUrl, SSLSettings sslSettings) {
        return ClientFactory.unauthorized(baseUrl, sslSettings).createService(MapApi.class);
    }
    public static MessageApi MessageApi(String baseUrl, SSLSettings sslSettings) {
        return ClientFactory.unauthorized(baseUrl, sslSettings).createService(MessageApi.class);
    }

    /*
    public static UserApi authenticate(String baseUrl, SSLSettings sslSettings, String username, String password) {
        ApiClient client =
                ClientFactory.basicAuth(baseUrl, sslSettings, username, password);
        return client.createService(UserApi.class);
    }

    public static UserApi userApiWithToken(SETTING settings) {
        return ClientFactory.clientToken(settings.url(), settings.sslSettings(), settings.jwtToken())
                .createService(UserApi.class);
    }
     */
}
