package com.intec.grab.bike.login;

import com.intec.grab.bike.shared.models.jwt;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface LoginApi
{
    //===================================================
    // With https (cannot receive parameter LoginDto ... why???)
    // With https: use @Field - we can get parameter ... why
    // About FieldMap - not try yet!!!!
    //===================================================
    //@Headers({"Content-Type:application/json"})
    @FormUrlEncoded
    @POST("api/guest/login/")
    Call<LoginDto> Login(
            //@retrofit2.http.Body LoginDto body
            @Field("Email") String Email,
            @Field("Password") String Password
    );
}




