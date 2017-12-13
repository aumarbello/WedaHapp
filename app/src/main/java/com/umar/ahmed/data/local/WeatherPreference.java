package com.umar.ahmed.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.Calendar;

import javax.inject.Inject;

/**
 * Created by ahmed on 11/20/17.
 */

public class WeatherPreference {
    private static final String FIRST_DAY_DATE = "day_of_year";
    private static final String WEATHER_SAVED = "isWeatherSaved";
    private static final String CURRENT_CITY = "savedCity";

    private SharedPreferences preferences;

    @Inject
    WeatherPreference(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setFirstDayDate(int dayDate){
        preferences.edit().putInt(FIRST_DAY_DATE, dayDate).apply();
    }

    public void setWeatherSaved(boolean isWeatherSaved){
        preferences.edit().putBoolean(WEATHER_SAVED, isWeatherSaved).apply();
    }

    public int getFirstDayDate(){
        int defDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return preferences.getInt(FIRST_DAY_DATE, defDay);
    }

    public boolean isWeatherSaved(){
        return preferences.getBoolean(WEATHER_SAVED, false);
    }

    public String getCurrentCity() {
        return preferences.getString(CURRENT_CITY, "Lagos");
    }

    public void setCurrentCity(String city){
        preferences
                .edit()
                .putString(CURRENT_CITY, city)
                .apply();
    }
}
