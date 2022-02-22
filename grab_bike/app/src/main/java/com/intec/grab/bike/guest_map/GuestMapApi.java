package com.intec.grab.bike.guest_map;

import com.intec.grab.bike.shared.models.jwt;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;

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
    Call<Void> BookATrip(
            @HeaderMap Map<String, String> headers,
            @Field("FromLatitude") String FromLatitude,
            @Field("FromLongtitude") String FromLongtitude,
            @Field("FromAddress") String FromAddress,

            @Field("ToLatitude") String ToLatitude,
            @Field("ToLongtitude") String ToLongtitude,
            @Field("ToAddress") String ToAddress
    );

    @FormUrlEncoded
    @POST("api/guest/push-position")
    Call<Void> PushPosition(
            @HeaderMap Map<String, String> headers,
            @Field("lat") String Lat,
            @Field("lng") String Lng
    );

    @GET("api/guest/order/driver-positions")
    Call<String> GetDriverPositions(
            @HeaderMap Map<String, String> headers,
            @Field("lat") String Lat,
            @Field("lng") String Lng
    );
}
