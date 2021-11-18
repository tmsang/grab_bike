package com.intec.grab.bike.login;

import com.intec.grab.bike.shared.models.Result;

import retrofit2.Call;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface LoginApi {

    @Headers({"Content-Type:application/json"})
    @POST("api/guest/login")
    Call<Result> Login(
            @retrofit2.http.Body LoginDto body
    );
}




