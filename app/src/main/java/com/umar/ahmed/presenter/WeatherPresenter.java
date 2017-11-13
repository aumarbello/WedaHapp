package com.umar.ahmed.presenter;

import com.google.gson.Gson;
import com.umar.ahmed.AppConstants;
import com.umar.ahmed.data.WeatherService;
import com.umar.ahmed.view.WeatherActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ahmed on 11/6/17.
 */

public class WeatherPresenter {
    private WeatherActivity activity;

    public WeatherPresenter(WeatherActivity activity){
        this.activity =  activity;
    }

    public void getWeather(double lat, double lon){
        provideService().getWeather(lat, lon, "imperial", AppConstants.API_KEY).
                observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(responseBody -> {
                    activity.gotWeather();
                }, throwable -> {
                    activity.noWeather();
                });

    }

    private WeatherService provideService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();

        return retrofit.create(WeatherService.class);
    }
}
