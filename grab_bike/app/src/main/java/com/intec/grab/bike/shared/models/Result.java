package com.intec.pushnotification.shared.models;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("result")               // "result" from PHP
    public String MessageText = null;

    @SerializedName("user_key")             // "user_key" from Boltz Engine
    public String UserkeyResult = null;

    @SerializedName("error")
    public String MessageError = null;
}
