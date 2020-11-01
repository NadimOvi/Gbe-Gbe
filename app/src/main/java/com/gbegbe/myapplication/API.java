package com.gbegbe.myapplication;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface API {
    @POST("CustomerLoginTrackAPI")
    Call<Information> postURLInfo(@Body JsonObject ussdObject);
}
