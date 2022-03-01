package com.intec.grab.bike_driver.messages;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface MessageApi {

    @GET("api/driver/order/requests")
    Call<List<MessageOut>> Requests(
            @HeaderMap Map<String, String> headers
    );

    @FormUrlEncoded
    @POST("api/driver/order/accept")
    Call<Void> Accept(
            @HeaderMap Map<String, String> headers,
            @Field("OrderId") String OrderId
    );

    @FormUrlEncoded
    @POST("api/driver/order/start")
    Call<Void> Start(
            @HeaderMap Map<String, String> headers,
            @Field("OrderId") String OrderId
    );

    @FormUrlEncoded
    @POST("api/driver/order/end")
    Call<Void> End(
            @HeaderMap Map<String, String> headers,
            @Field("OrderId") String OrderId
    );
}
