package com.intec.grab.bike_driver.forgot_password;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ForgotPasswordApi {
    @FormUrlEncoded
    @POST("api/guest/forgot/")
    Call<ResponseBody> ForgotPassword(
            @Field("Email") String Email
    );
}
