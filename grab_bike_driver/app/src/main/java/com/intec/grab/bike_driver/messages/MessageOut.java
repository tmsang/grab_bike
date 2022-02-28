package com.intec.grab.bike_driver.messages;

import com.google.gson.annotations.SerializedName;

public class MessageOut {

    @SerializedName("OrderId")
    public String OrderId = null;

    @SerializedName("Status")
    public String Status = null;

    @SerializedName("FromAddress")
    public String FromAddress = null;

    @SerializedName("ToAddress")
    public String ToAddress = null;

    @SerializedName("RequestDateTime")
    public String RequestDateTime = null;

    @SerializedName("Distance")
    public String Distance = null;

    @SerializedName("Cost")
    public String Cost = null;

    @SerializedName("GuestName")
    public String GuestName = null;

    @SerializedName("GuestPhone")
    public String GuestPhone = null;
}
