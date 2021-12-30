package com.intec.grab.bike.utils.base;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.intec.grab.bike.utils.api.SSLSettings;

public class SETTING {
    private final SharedPreferences sharedPreferences;

    public static SETTING instance(Context context) {
        return new SETTING(context);
    }

    public SETTING(Context context) {
        sharedPreferences = context.getSharedPreferences("INTEC", Context.MODE_PRIVATE);
    }

    public void url(String url) {
        sharedPreferences.edit().putString("url", url).apply();
    }

    public String url() {
        return sharedPreferences.getString("url", null);
    }

    public boolean tokenExists() {
        return jwtToken() != null;
    }

    public String jwtToken() {
        return sharedPreferences.getString("jwtToken", null);
    }

    public void jwtToken(String token) {
        sharedPreferences.edit().putString("jwtToken", token).apply();
    }

    public String fcmToken() {
        return sharedPreferences.getString("fcmToken", null);
    }

    public void fcmToken(String token) {
        sharedPreferences.edit().putString("fcmToken", token).apply();
    }

    public String userKey() {
        return sharedPreferences.getString("userKey", null);
    }

    public void userKey(String userKey) {
        sharedPreferences.edit().putString("userKey", userKey).apply();
    }

    public String email() {
        return sharedPreferences.getString("email", null);
    }

    public void email(String email) {
        sharedPreferences.edit().putString("email", email).apply();
    }

    public String currentLat() {
        return sharedPreferences.getString("currentLat", null);
    }
    public void currentLat(String lat) {
        sharedPreferences.edit().putString("currentLat", lat).apply();
    }
    public String currentLng() {
        return sharedPreferences.getString("currentLng", null);
    }
    public void currentLng(String _long) {
        sharedPreferences.edit().putString("currentLng", _long).apply();
    }
    public String currentAddress() {
        return sharedPreferences.getString("currentAddress", null);
    }
    public void currentAddress(String address) {
        sharedPreferences.edit().putString("currentAddress", address).apply();
    }

    public void clear() {
        jwtToken(null);
        fcmToken(null);
        userKey(null);

        url(null);
        validateSSL(true);
        cert(null);
    }

    public void username(String name) {
        sharedPreferences.edit().putString("username", name).apply();
    }

    public void admin(boolean admin) {
        sharedPreferences.edit().putBoolean("admin", admin).apply();
    }

    public String username() {
        return sharedPreferences.getString("username", null);
    }

    public boolean admin() {
        return sharedPreferences.getBoolean("admin", false);
    }

    public String serverVersion() {
        return sharedPreferences.getString("version", "UNKNOWN");
    }

    public void serverVersion(String version) {
        sharedPreferences.edit().putString("version", version).apply();
    }

    private boolean validateSSL() {
        return sharedPreferences.getBoolean("validateSSL", true);
    }

    public void validateSSL(boolean validateSSL) {
        sharedPreferences.edit().putBoolean("validateSSL", validateSSL).apply();
    }

    private String cert() {
        return sharedPreferences.getString("cert", null);
    }

    public void cert(String cert) {
        sharedPreferences.edit().putString("cert", cert).apply();
    }

    public SSLSettings sslSettings() {
        return new SSLSettings(validateSSL(), cert());
    }
}
