package com.intec.grab.bike_driver.messages;

import com.intec.grab.bike_driver.histories.MessageHistoryOut;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

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

    @GET("api/driver/order/request-histories")
    Call<List<MessageHistoryOut>> RequestHistories(
            @HeaderMap Map<String, String> headers
    );

    @GET("api/driver/order/statistic")
    Call<StatisticOut> Statistic(
            @HeaderMap Map<String, String> headers
    );



    @GET("api/driver/order/interval-gets")
    Call<IntervalResultOut> IntervalGets(
            @HeaderMap Map<String, String> headers,
            @Query("lat") String Lat,
            @Query("lng") String Lng
    );

    // upload file by retrofit 2
    @Multipart
    @POST("api/driver/order/log")
    Call<ResponseBody> Upload(
            @HeaderMap Map<String, String> headers,
            @Part("description") RequestBody description,
            @Part MultipartBody.Part file
    );
}
