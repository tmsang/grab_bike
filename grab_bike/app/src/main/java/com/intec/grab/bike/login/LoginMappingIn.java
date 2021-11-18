package com.intec.grab.bike.login;

import com.google.gson.annotations.SerializedName;

public class LoginMappingIn {

    @SerializedName("userKey")
    private String key = null;

    @SerializedName("email")
    private String name = null;

    @SerializedName("password")
    private String pass = null;

    public LoginMappingIn(String key, String name, String pass) {
        this.key = key;
        this.name = name;
        this.pass = pass;
    }
}
