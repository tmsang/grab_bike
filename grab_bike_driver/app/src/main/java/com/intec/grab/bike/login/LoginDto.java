package com.intec.grab.bike.login;

import com.google.gson.annotations.SerializedName;

public class LoginDto {

    @SerializedName("jwt")
    public String jwt = null;

    @SerializedName("FullName")
    public String FullName = null;

    @SerializedName("Phone")
    public String Phone = null;

    @SerializedName("Email")
    public String Email = null;

}
