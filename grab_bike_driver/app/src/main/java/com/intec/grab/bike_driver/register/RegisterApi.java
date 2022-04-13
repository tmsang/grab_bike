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
    @POST("api/driver/register/")
    Call<ResponseBody> Register(
            // fullName, email, phone, password, code,
            @Field("FullName") String fullName,
            @Field("Email") String email,
            @Field("Phone") String phone,
            @Field("Password") String password,
            @Field("SmsCode") String code,

            //avatar, birthday, male, personalId, address,
            @Field("Avatar") String avatar,
            @Field("Birthday") String birthDay,
            @Field("Male") Boolean male,
            @Field("PersonalId") String personalId,
            @Field("Address") String address,

            //plateNo, bikeOwner, engineNo, chassisNo, bikeType, brand
            @Field("PlateNo") String plateNo,
            @Field("BikeOwner") String bikeOwner,
            @Field("EngineNo") String engineNo,
            @Field("ChassisNo") String chassisNo,
            @Field("BikeType") String bikeType,
            @Field("Brand") String brand
    );

    @FormUrlEncoded
    @POST("api/driver/smscode/")
    Call<ResponseBody> GetSmsCode(
            @Field("phone") String phone
    );
}
