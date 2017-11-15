package com.umar.ahmed.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ahmed on 11/15/17.
 */

public class WeatherDay implements Serializable{
    private List<WeatherItem> daysStats;
    private String cityName;
    private String dayString;

    public WeatherDay(List<WeatherItem> daysStats, String cityName, String dayString){
        this.daysStats = daysStats;
        this.cityName = cityName;
        this.dayString = dayString;
    }

    public String getCityName(){
        return cityName;
    }

    public String getDayString(){
        return dayString;
    }

    public List<WeatherItem> getStatsList() {
        return daysStats;
    }
}
