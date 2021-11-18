package com.intec.grab.bike.register;

import com.google.gson.annotations.SerializedName;

public class RegisterDto {

    @SerializedName("fullName")
    private String fullName = null;

    @SerializedName("email")
    private String email = null;

    @SerializedName("phone")
    private String phone = null;

    @SerializedName("password")
    private String password = null;

    @SerializedName("smsCode")
    private String smsCode = null;

    public RegisterDto(String fullName, String email, String phone, String password, String smsCode) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.smsCode = smsCode;
    }
}
