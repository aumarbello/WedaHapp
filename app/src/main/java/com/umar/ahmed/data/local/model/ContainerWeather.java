package com.umar.ahmed.data.local.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ahmed on 12/5/17.
 */

public class ContainerWeather implements Serializable {
    private List<WeatherDay> weatherDays;

    public ContainerWeather(List<WeatherDay> weatherDays) {
        this.weatherDays = weatherDays;
    }

    public List<WeatherDay> getWeatherDays() {
        return weatherDays;
    }
}
