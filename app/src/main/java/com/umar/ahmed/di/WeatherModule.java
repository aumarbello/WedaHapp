package com.umar.ahmed.di;

import android.app.Application;
import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.umar.ahmed.AppConstants;
import com.umar.ahmed.data.remote.WeatherService;

import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ahmed on 11/20/17.
 */
@Module
public class WeatherModule {
    private Application application;

    public WeatherModule(Application application){
        this.application = application;
    }

    @Provides
    @Singleton
    Context providesContext(){
        return application;
    }

    @Provides
    @Singleton
    OkHttpClient providesClient(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    @Provides
    @Singleton
    Gson providesGson(){
        return new GsonBuilder().setLenient().create();
    }

    @Provides
    @Singleton
    Retrofit providesRetrofit(Gson gson,OkHttpClient client){
        return new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .baseUrl(AppConstants.ENDPOINT)
                .build();
    }

    @Provides
    @Singleton
    WeatherService providesWeatherService(Retrofit retrofit){
        return retrofit.create(WeatherService.class);
    }
}
