package com.intec.grab.bike_driver.messages;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.LocalDateTime;

public class MessageOut implements Serializable {

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

    @SerializedName("GuestName")
    public String GuestName = null;

    @SerializedName("GuestPhone")
    public String GuestPhone = null;

    @SerializedName("GuestLat")
    public String GuestLat = null;

    @SerializedName("GuestLng")
    public String GuestLng = null;

    public long AcceptDateTime;
}
