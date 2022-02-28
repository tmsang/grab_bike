package com.intec.grab.bike_driver.messages;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;

public interface MessageApi {

    @GET("api/driver/order/requests")
    Call<List<MessageOut>> Requests(
            @HeaderMap Map<String, String> headers
    );
}
