package com.intec.grab.bike.guest_map;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GuestMapApi
{
    //===================================================
    // With https (cannot receive parameter LoginDto ... why???)
    // With https: use @Field - we can get parameter ... why
    // About FieldMap - not try yet!!!!
    //===================================================
    //@Headers({"Content-Type:application/json"})
    @GET("api/guest/order/price")
    Call<String> GetPrice(
            @HeaderMap Map<String, String> headers
    );

    @FormUrlEncoded
    @POST("api/guest/order/book")
    Call<BookResultDto> BookATrip(
            @HeaderMap Map<String, String> headers,
            @Field("FromLatitude") String FromLatitude,
            @Field("FromLongtitude") String FromLongtitude,
            @Field("FromAddress") String FromAddress,

            @Field("ToLatitude") String ToLatitude,
            @Field("ToLongtitude") String ToLongtitude,
            @Field("ToAddress") String ToAddress,

            @Field("Distance") Double Distance,
            @Field("Amount") Double Amount
    );

    @FormUrlEncoded                                 // use for method: "POST"
    @POST("api/guest/push-position")
    Call<Void> PushPosition(
            @HeaderMap Map<String, String> headers,
            @Field("lat") String Lat,               // @Field: is for POST
            @Field("lng") String Lng
    );

    @FormUrlEncoded                                 // use for method: "POST"
    @POST("api/guest/order/cancel-by-client")
    Call<Void> CancelBooking(
            @HeaderMap Map<String, String> headers,
            @Field("orderId") String OrderId
    );

    @GET("api/guest/order/interval-gets")
    Call<IntervalResultDto> IntervalGets(
            @HeaderMap Map<String, String> headers,
            @Query("lat") String Lat,
            @Query("lng") String Lng,
            @Query("orderId") String OrderId
    );
}
