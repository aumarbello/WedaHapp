package com.umar.ahmed.data;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by ahmed on 11/6/17.
 */

public interface WeatherService {
    @GET("weather")
    Single<ResponseBody> getWeather(@Query("lat") double latitude,
                                    @Query("lon") double lon,
                                    @Query("units") String units,
                                    @Query("APPID") String appId);
}
