package com.example.appagromanager;

import com.example.appagromanager.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("forecast.json")
    Call<WeatherResponse> getForecastByCity(
            @Query("key") String apiKey,
            @Query("q") String city,
            @Query("days") int days,
            @Query("aqi") String aqi,
            @Query("alerts") String alerts
    );
}