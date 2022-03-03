package com.intec.grab.bike.histories;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface MessageApi {

    @GET("api/guest/order/requests")
    Call<List<MessageOut>> Requests(
            @HeaderMap Map<String, String> headers
    );

    @FormUrlEncoded
    @POST("api/guest/order/evaluate")
    Call<Void> Evaluate(
            @HeaderMap Map<String, String> headers,
            @Field("OrderId") String OrderId,
            @Field("Rating") Float Rating,
            @Field("Remark") String Remark
    );

}
