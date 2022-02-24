package com.intec.grab.bike.guest_map;

import com.google.gson.annotations.SerializedName;

public class DriverPositionDto {

    @SerializedName("Phone")
    public String Phone = null;

    @SerializedName("Lat")
    public double Lat = 0.0;

    @SerializedName("Lng")
    public double Lng = 0.0;

    @SerializedName("Distance")
    public double Distance = 0.0;
}
