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

    private SharedPreferences preferences;

    @Inject
    public WeatherPreference(Context context){
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void setFirstDayDate(int dayDate){
        preferences.edit().putInt(FIRST_DAY_DATE, dayDate).apply();
    }

    public void setWeatherSaved(boolean isSaved){
        preferences.edit().putBoolean(WEATHER_SAVED, isSaved).apply();
    }

    public int getFirstDayDate(){
        int defDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return preferences.getInt(FIRST_DAY_DATE, defDay);
    }

    public boolean isWeatherSaved(){
        return preferences.getBoolean(WEATHER_SAVED, false);
    }


}
