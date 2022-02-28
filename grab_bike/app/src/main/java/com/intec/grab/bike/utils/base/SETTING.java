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

    public String url() {
        return sharedPreferences.getString("url", null);
    }
    public void url(String url) {
        sharedPreferences.edit().putString("url", url).apply();
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

    public String fullName() {
        return sharedPreferences.getString("fullName", null);
    }
    public void fullName(String name) {
        sharedPreferences.edit().putString("fullName", name).apply();
    }

    public String phone() {
        return sharedPreferences.getString("phone", null);
    }
    public void phone(String phone) {
        sharedPreferences.edit().putString("phone", phone).apply();
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
    public void currentLng(String lng) {
        sharedPreferences.edit().putString("currentLng", lng).apply();
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
        fullName(null);
        phone(null);
        email(null);
        currentLat(null);
        currentLng(null);
        currentAddress(null);

        url(null);
        validateSSL(true);
        cert(null);
    }

    public boolean admin() {
        return sharedPreferences.getBoolean("admin", false);
    }
    public void admin(boolean admin) {
        sharedPreferences.edit().putBoolean("admin", admin).apply();
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
