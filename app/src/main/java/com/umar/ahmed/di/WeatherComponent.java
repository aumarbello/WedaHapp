package com.umar.ahmed.di;

import com.umar.ahmed.view.WeatherActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by ahmed on 11/20/17.
 */

@Singleton
@Component(modules = WeatherModule.class)
public interface WeatherComponent {
    void inject(WeatherActivity activity);
}
