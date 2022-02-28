package com.intec.grab.bike_driver.register;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface RegisterApi {

    /*
    @Headers({"Content-Type:application/json"})
    @POST("api/login/device")
    Call<Result> Register(
            @retrofit2.http.Body RegisterDto body
    );
    */

    @FormUrlEncoded
    @POST("api/guest/register/")
    Call<ResponseBody> Register(
            @Field("FullName") String fullName,
            @Field("Email") String email,
            @Field("Phone") String phone,
            @Field("Password") String password,
            @Field("SmsCode") String code
    );

    @FormUrlEncoded
    @POST("api/guest/smscode/")
    Call<ResponseBody> GetSmsCode(
            @Field("phone") String phone
    );
}
