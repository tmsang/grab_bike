package com.intec.grab.bike.login;

import com.google.gson.annotations.SerializedName;

public class LoginDto {

    @SerializedName("email")
    private String email = null;

    @SerializedName("password")
    private String password = null;

    public LoginDto(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
