package com.intec.grab.bike.guest_map;

import com.intec.grab.bike.shared.models.jwt;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
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
    Call<Void> BookATrip(
            @HeaderMap Map<String, String> headers,
            @Field("FromLatitude") String FromLatitude,
            @Field("FromLongtitude") String FromLongtitude,
            @Field("FromAddress") String FromAddress,

            @Field("ToLatitude") String ToLatitude,
            @Field("ToLongtitude") String ToLongtitude,
            @Field("ToAddress") String ToAddress
    );

    @FormUrlEncoded                                 // use for method: "POST"
    @POST("api/guest/push-position")
    Call<Void> PushPosition(
            @HeaderMap Map<String, String> headers,
            @Field("lat") String Lat,               // @Field: is for POST
            @Field("lng") String Lng
    );

    @GET("api/guest/order/driver-positions")
    Call<List<DriverPositionDto>> GetDriverPositions(
            @HeaderMap Map<String, String> headers,
            @Query("lat") String Lat,
            @Query("lng") String Lng
    );
}
