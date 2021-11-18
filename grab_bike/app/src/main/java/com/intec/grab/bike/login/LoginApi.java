package com.intec.grab.bike.login;

import com.intec.pushnotification.shared.models.Result;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginApi {

    @Headers({"Content-Type:application/json"})
    @POST("api/login/device")
    Call<Result> getToken(
            @retrofit2.http.Body LoginMappingIn body
    );

    @Headers({"Content-Type:application/json"})
    @POST("api/user/check-account")
    Call<Result> CheckAccount(
            @retrofit2.http.Body UserMappingIn body
    );

    // API of Boltz
    @Headers({"Content-Type:application/json"})
    @POST("devices")
    Call<Result> getUserKey(
            @retrofit2.http.Body BoltzUserkeyMappingIn body
    );
}




