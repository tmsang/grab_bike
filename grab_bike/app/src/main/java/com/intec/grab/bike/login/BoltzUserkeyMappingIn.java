package com.intec.grab.bike.login;

import com.google.gson.annotations.SerializedName;

public class BoltzUserkeyMappingIn {
    @SerializedName("service")
    private String service = null;

    @SerializedName("token")
    private String token = null;

    public BoltzUserkeyMappingIn(String service, String token) {
        this.service = service;
        this.token = token;
    }
}
