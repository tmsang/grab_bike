package com.intec.grab.bike.register;

import com.intec.grab.bike.shared.models.Result;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RegisterApi {

    @Headers({"Content-Type:application/json"})
    @POST("api/login/device")
    Call<Result> Register(
            @retrofit2.http.Body RegisterDto body
    );
}
