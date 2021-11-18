package com.intec.grab.bike.messages;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;

public interface MessageApi {

    // API of Boltz
    @Headers({
        "Content-Type: application/json"
    })
    @GET("messages")
    Call<List<MessageMappingOut>> getMessages(
        @HeaderMap Map<String, String> headers
    );
}
