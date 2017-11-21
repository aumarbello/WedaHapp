package com.umar.ahmed;

import android.app.Application;

import com.umar.ahmed.di.DaggerWeatherComponent;
import com.umar.ahmed.di.WeatherComponent;
import com.umar.ahmed.di.WeatherModule;

/**
 * Created by ahmed on 11/20/17.
 */

public class WeatherApp extends Application {
    private WeatherComponent component;

    public WeatherComponent getComponent(){
        return component;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        component = init(this);
    }

    protected WeatherComponent init(WeatherApp app){
        return DaggerWeatherComponent.builder()
                .weatherModule(new WeatherModule(app))
                .build();
    }
}
