package com.umar.ahmed.data.local.db;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.umar.ahmed.data.local.model.Main;
import com.umar.ahmed.data.local.model.Weather;
import com.umar.ahmed.data.local.model.WeatherItem;

import java.util.ArrayList;
import java.util.List;

import static com.umar.ahmed.AppConstants.*;

/**
 * Created by ahmed on 11/16/17.
 */

class ItemWrapper extends CursorWrapper {
    private Cursor cursor;
    ItemWrapper(Cursor cursor) {
        super(cursor);
        this.cursor = cursor;
    }

    List<WeatherItem> getWeatherItem(){
        List<WeatherItem> itemList = new ArrayList<>();

        cursor.moveToFirst();
        while (!cursor.isAfterLast()){
            WeatherItem item = new WeatherItem();

            String dtText = getString(getColumnIndex(timeText));
            long dtLong = getLong(getColumnIndex(timeLong));
            String weatherIcon = getString(getColumnIndex(icon));
            String weatherDesc = getString(getColumnIndex(description));
            double temp = getDouble(getColumnIndex(averageTemp));
            double currentMinTemp = getDouble(getColumnIndex(minTemp));
            double currentMaxTemp = getDouble(getColumnIndex(maxTemp));

            item.setDtTxt(dtText);
            item.setDt((int)dtLong);

            Weather onlyWeather = new Weather();
            onlyWeather.setIcon(weatherIcon);
            onlyWeather.setDescription(weatherDesc);
            List<Weather> weatherList = new ArrayList<>();
            weatherList.add(onlyWeather);
            item.setWeather(weatherList);

            Main weatherMain = new Main();
            weatherMain.setTemp(temp);
            weatherMain.setTempMin(currentMinTemp);
            weatherMain.setTempMax(currentMaxTemp);
            item.setMain(weatherMain);

            itemList.add(item);
        }

        cursor.close();
        return itemList;
    }
}
