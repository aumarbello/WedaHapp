package com.umar.ahmed.data.remote;

import com.umar.ahmed.data.local.model.WeatherResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ahmed on 11/6/17.
 */

public interface WeatherService {
    @GET("forecast?")
    Single<WeatherResponse> getWeather(@Query("units") String units,
                                       @Query("APPID") String appId,
                                       @Query("q") String city);
}
