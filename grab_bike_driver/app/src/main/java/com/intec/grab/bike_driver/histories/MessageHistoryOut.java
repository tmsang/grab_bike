package com.intec.grab.bike_driver.histories;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageHistoryOut implements Serializable {
    @SerializedName("OrderId")
    public String OrderId = null;

    @SerializedName("Status")
    public String Status = null;

    @SerializedName("FromAddress")
    public String FromAddress = null;

    @SerializedName("ToLat")
    public String ToLat = null;

    @SerializedName("ToLng")
    public String ToLng = null;

    @SerializedName("ToAddress")
    public String ToAddress = null;

    @SerializedName("RequestDateTime")
    public String RequestDateTime = null;

    @SerializedName("Distance")
    public String Distance = null;

    @SerializedName("Cost")
    public String Cost = null;

    @SerializedName("Start")
    public String Start = null;

    @SerializedName("End")
    public String End = null;

    @SerializedName("Rating")
    public float Rating = 0;

    @SerializedName("GuestName")
    public String GuestName = null;

    @SerializedName("GuestPhone")
    public String GuestPhone = null;
}
