package com.intec.grab.bike.guest_map;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IntervalResultDto {
    @SerializedName("OrderId")
    public String OrderId = null;

    @SerializedName("Status")
    public String Status = null;

    @SerializedName("Positions")
    public List<DriverPositionDto> Positions = null;

}
