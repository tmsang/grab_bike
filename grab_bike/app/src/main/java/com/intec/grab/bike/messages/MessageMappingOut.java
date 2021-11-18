package com.intec.grab.bike.messages;

import com.google.gson.annotations.SerializedName;

public class MessageMappingOut {
    @SerializedName("message_id")
    public String MessageId = null;

    @SerializedName("channel_name")
    public String ChannelName = null;

    @SerializedName("published_date")
    public String PublishedDate = null;

    @SerializedName("title")
    public String Title = null;

    @SerializedName("description")
    public String Description = null;
}
