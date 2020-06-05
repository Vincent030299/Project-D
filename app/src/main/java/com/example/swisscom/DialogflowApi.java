package com.example.swisscom;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface DialogflowApi {

    @POST("query?v=20150910")
    @Headers("Authorization: Bearer 59e1356ccb26466888fc8c93071bf241")
    Call<Post> createPost(@Body Post post);
}
