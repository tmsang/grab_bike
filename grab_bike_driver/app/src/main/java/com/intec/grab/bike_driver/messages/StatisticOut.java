package com.intec.grab.bike_driver.messages;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class StatisticOut implements Serializable {
    @SerializedName("Price")
    public String Price = null;

    @SerializedName("CancelCounter")
    public String CancelCounter = null;

    @SerializedName("DoneCounter")
    public String DoneCounter = null;

    @SerializedName("TotalAmount")
    public String TotalAmount = null;
}
