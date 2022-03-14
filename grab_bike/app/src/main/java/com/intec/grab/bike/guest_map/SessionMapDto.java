package com.intec.grab.bike.guest_map;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class SessionMapDto implements Serializable {
    @SerializedName("ToAddress")
    public String ToAddress = null;

    @SerializedName("ToLat")
    public String ToLat = null;

    @SerializedName("ToLng")
    public String ToLng = null;

    @SerializedName("OrderId")
    public String OrderId = null;

    @SerializedName("Status")
    public String Status = null;

    public SessionMapDto() {
    }
}
