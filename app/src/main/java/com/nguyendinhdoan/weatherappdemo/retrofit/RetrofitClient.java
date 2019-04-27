package com.nguyendinhdoan.weatherappdemo.retrofit;

import retrofit2.Retrofit;
import retrofit2.Retrofit.Builder;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static Retrofit instance = null;

    public static Retrofit getInstance(String baseURL) {
        if (instance == null) {
            instance = new Retrofit
                    .Builder()
                    .baseUrl(baseURL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return instance;
    }
}
