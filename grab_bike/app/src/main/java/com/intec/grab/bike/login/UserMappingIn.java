package com.intec.grab.bike.login;

import com.google.gson.annotations.SerializedName;

public class UserMappingIn
{
    @SerializedName("email")
    private String name = null;

    @SerializedName("password")
    private String pass = null;

    public UserMappingIn(String name, String pass) {
        this.name = name;
        this.pass = pass;
    }
}
