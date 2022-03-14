package com.intec.grab.bike_driver.shared.models;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("messageText")               // "result" from NETCORE
    public String MessageText = null;

    @SerializedName("messageError")
    public String MessageError = null;
}
