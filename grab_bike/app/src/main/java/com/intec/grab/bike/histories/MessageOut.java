package com.intec.grab.bike.histories;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MessageOut implements Serializable {

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

    @SerializedName("Start")
    public String Start = null;

    @SerializedName("End")
    public String End = null;

    @SerializedName("Distance")
    public String Distance = null;

    @SerializedName("Cost")
    public String Cost = null;

    @SerializedName("Rating")
    public float Rating = 0;

    @SerializedName("Note")
    public String Note = null;

    @SerializedName("DriverName")
    public String DriverName = null;

    @SerializedName("DriverPhone")
    public String DriverPhone = null;

}
