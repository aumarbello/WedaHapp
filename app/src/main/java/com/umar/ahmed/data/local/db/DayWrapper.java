package com.umar.ahmed.data.local.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.umar.ahmed.data.local.model.WeatherDay;

import java.util.ArrayList;
import java.util.List;

import static com.umar.ahmed.AppConstants.*;

/**
 * Created by ahmed on 11/16/17.
 */

class DayWrapper extends CursorWrapper {
    private Cursor cursor;

    DayWrapper(Cursor cursor) {
        super(cursor);
        this.cursor = cursor;
    }

    List<WeatherDay> getWeatherDay(){
        List<WeatherDay> weatherDayList = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            String weatherCityName = getString(getColumnIndex(cityName));
            String weatherDay = getString(getColumnIndex(dayString));

            WeatherDay day = new WeatherDay(null, weatherCityName, weatherDay);
            weatherDayList.add(day);
            cursor.moveToNext();
        }
        cursor.close();
        return weatherDayList;
    }
}
