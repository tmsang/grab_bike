package com.intec.grab.bike_driver.messages;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class IntervalResultOut implements Serializable
{
    @SerializedName("Requests")
    public List<MessageOut> Requests = null;



}
