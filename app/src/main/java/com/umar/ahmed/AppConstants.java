package com.umar.ahmed;

/**
 * Created by ahmed on 11/6/17.
 */

public final class AppConstants {
    public static final String API_KEY = "bbeb34ebf60ad50f7893e7440a1e2b0b";
    public static final String ENDPOINT = "http://api.openweathermap.org/data/2.5/";
    public static final String WEATHER_UNIT = "metric";


    //database Strings
    public static final String databaseName = "weather.db";
    public static final int databaseVersion = 1;


    //database tables
    public static final String dayTable = "weatherDayTable";
    public static final String itemTable = "weatherItemTable";

    //dayTable columns
    public static final String cityName = "name";
    public static final String dayString = "day";

    //itemTable columns
    public static final String foreignKey = "weatherCity";
    public static final String timeText = "timeInText";
    public static final String timeLong = "timeInLong";
    public static final String icon  = "weatherIcon";
    public static final String description = "weatherDesc";
    public static final String averageTemp = "temp";
    public static final String minTemp = "minimumTemp";
    public static final String maxTemp = "maximumTemp";


    //sqLite commands
    public static final String createDayTable = "CREATE TABLE IF NOT EXISTS " + dayTable + "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            cityName + " TEXT, " +
            dayString + " TEXT" + ")";


    public static final String createItemTable = "CREATE TABLE IF NOT EXISTS " + itemTable + "(" +
            " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            foreignKey + " TEXT, " +
            timeText + " TEXT, " +
            timeLong + " LONG, " +
            icon + " TEXT, " +
            description + " TEXT, " +
            averageTemp + " DOUBLE, " +
            minTemp + " DOUBLE, " +
            maxTemp + " DOUBLE" + ")";

    public static final String dropDayTable = "DROP TABLE " + dayTable;
    public static final String dropItemTable = "DROP TABLE " + itemTable;
}
