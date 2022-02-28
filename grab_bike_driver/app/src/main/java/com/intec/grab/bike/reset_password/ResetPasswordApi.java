package com.intec.grab.bike.reset_password;

import com.intec.grab.bike.shared.models.jwt;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ResetPasswordApi {
    @FormUrlEncoded
    @POST("api/guest/reset/")
    Call<jwt> ResetPassword(
            @Field("Email") String Email,
            @Field("OldPassword") String OldPassword,
            @Field("NewPassword") String NewPassword,
            @Field("SmsCode") String SmsCode
    );
}
