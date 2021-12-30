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
}
