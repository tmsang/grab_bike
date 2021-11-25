package com.intec.grab.bike.login;

import com.google.gson.annotations.SerializedName;

public class LoginDto {

    @SerializedName("Email")
    private String Email = null;

    @SerializedName("Password")
    private String Password = null;

    public LoginDto(String email, String password) {
        this.Email = email;
        this.Password = password;
    }
}
