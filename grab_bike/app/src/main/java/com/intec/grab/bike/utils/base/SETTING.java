package com.intec.grab.bike.utils.base;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;

import com.google.gson.Gson;
import com.intec.grab.bike.guest_map.SessionMapDto;
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

    public SessionMapDto sessionMap() {
        Gson gson = new Gson();
        String value = sharedPreferences.getString("sessionMap", null);
        SessionMapDto result = gson.fromJson(value, SessionMapDto.class);
        return result;
    }
    public void sessionMap(SessionMapDto session) {
        Gson gson = new Gson();
        String result = gson.toJson(session);
        sharedPreferences.edit().putString("sessionMap", result).apply();
    }
    public void sessionMap_SetToLat(String lat) {
        SessionMapDto result = this.sessionMap();
        if (result == null) result = new SessionMapDto();
        result.ToLat = lat;
        sessionMap(result);
    }
    public void sessionMap_SetToLng(String lng) {
        SessionMapDto result = this.sessionMap();
        if (result == null) result = new SessionMapDto();
        result.ToLng = lng;
        sessionMap(result);
    }
    public void sessionMap_SetToAddress(String address) {
        SessionMapDto result = this.sessionMap();
        if (result == null) result = new SessionMapDto();
        result.ToAddress = address;
        sessionMap(result);
    }
    public void sessionMap_SetOrderId(String orderId) {
        SessionMapDto result = this.sessionMap();
        if (result == null) result = new SessionMapDto();
        result.OrderId = orderId;
        sessionMap(result);
    }
    public void sessionMap_SetStatus(String status) {
        SessionMapDto result = this.sessionMap();
        if (result == null) result = new SessionMapDto();
        result.Status = status;
        sessionMap(result);
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
        sessionMap(null);

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
