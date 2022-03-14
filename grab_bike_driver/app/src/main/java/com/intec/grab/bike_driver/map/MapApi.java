package com.intec.grab.bike_driver.map;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface MapApi
{
    //===================================================
    // With https (cannot receive parameter LoginDto ... why???)
    // With https: use @Field - we can get parameter ... why
    // About FieldMap - not try yet!!!!
    //===================================================
    //@Headers({"Content-Type:application/json"})

    @FormUrlEncoded                                 // use for method: "POST"
    @POST("api/driver/push-position")
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
