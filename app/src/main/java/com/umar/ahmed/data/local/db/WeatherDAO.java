package com.umar.ahmed.data.local.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.umar.ahmed.data.local.model.Main;
import com.umar.ahmed.data.local.model.Weather;
import com.umar.ahmed.data.local.model.WeatherDay;
import com.umar.ahmed.data.local.model.WeatherItem;

import java.util.List;

import static android.content.ContentValues.TAG;
import static com.umar.ahmed.AppConstants.*;

/**
 * Created by ahmed on 11/16/17.
 */

public class WeatherDAO {
    private DatabaseHelper helper;
    private SQLiteDatabase database;


    public WeatherDAO(Context context) {
        this.helper = new DatabaseHelper(context);
    }

    public void open(){
        database = helper.getWritableDatabase();
    }

    public void close(){
        database.close();
    }

    public void saveAllWeatherDays(List<WeatherDay> weatherDayList){
        recreateTables();
        for (WeatherDay day : weatherDayList) {
            saveWeatherDay(day);
        }
    }

    public List<WeatherDay> getAllWeatherDays(){
        Cursor weatherDays = query(dayTable, null, null);

        DayWrapper wrapper = new DayWrapper(weatherDays);
        List<WeatherDay> weatherDayList = wrapper.getWeatherDay();


        for (WeatherDay day : weatherDayList) {
            List<WeatherItem> itemList = addWeatherItems(day);
            day.setDaysStats(itemList);
        }

        return weatherDayList;
    }

    private List<WeatherItem> addWeatherItems(WeatherDay day) {
        Cursor itemCursor = query(itemTable, foreignKey + " == ?",
                new String[]{day.getDayString()});
        ItemWrapper wrapper = new ItemWrapper(itemCursor);
        return wrapper.getWeatherItem();
    }

    private void saveWeatherDay(WeatherDay day) {
        database.insert(dayTable, null,
                getWeatherValues(day));
        for (WeatherItem item : day.getStatsList()) {
            database.insert(itemTable, null, getItemValues(item,
                    day.getDayString()));
        }
    }

    private ContentValues getItemValues(WeatherItem item, String dayString) {
        ContentValues itemValues = new ContentValues();
        itemValues.put(foreignKey, dayString);
        itemValues.put(timeText, item.getDtTxt());
        itemValues.put(timeLong, item.getDt());
        Weather weather = item.getWeather().get(0);
        itemValues.put(icon, weather.getIcon());
        itemValues.put(description, weather.getDescription());
        Main main  = item.getMain();
        itemValues.put(averageTemp, main.getTemp());
        itemValues.put(minTemp, main.getTempMin());
        itemValues.put(maxTemp, main.getTempMax());
        return itemValues;
    }

    private ContentValues getWeatherValues(WeatherDay day) {
        ContentValues dayValues = new ContentValues();
        dayValues.put(cityName, day.getCityName());
        dayValues.put(dayString, day.getDayString());
        return dayValues;
    }

    private Cursor query(String tableName, String whereClause, String[] args){
        return database.query(tableName, null, whereClause, args, null,
                null, null);
    }

    private void recreateTables() {
        database.execSQL(dropDayTable);
        database.execSQL(dropItemTable);

        database.execSQL(createDayTable);
        database.execSQL(createItemTable);
    }
}
