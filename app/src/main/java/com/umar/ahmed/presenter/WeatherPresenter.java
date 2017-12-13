package com.umar.ahmed.presenter;

import android.util.Log;

import com.umar.ahmed.AppConstants;
import com.umar.ahmed.data.local.WeatherPreference;
import com.umar.ahmed.data.local.db.WeatherDAO;
import com.umar.ahmed.data.local.model.WeatherDay;
import com.umar.ahmed.data.local.model.WeatherItem;
import com.umar.ahmed.data.local.model.WeatherResponse;
import com.umar.ahmed.data.remote.WeatherService;
import com.umar.ahmed.view.WeatherActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;

/**
 * Created by ahmed on 11/6/17.
 */

public class WeatherPresenter {
    private WeatherService service;
    private WeatherDAO weatherDAO;
    private WeatherActivity activity;
    private WeatherPreference preference;

    @Inject
    WeatherPresenter(WeatherService service, WeatherDAO weatherDAO,
                            WeatherPreference preference){
        this.service =  service;
        this.weatherDAO = weatherDAO;
        this.preference = preference;
    }

    public void attachView(WeatherActivity activity){
        this.activity = activity;
    }

    public void getWeather(boolean loadNewWeather, String city){
        if (loadNewWeather){
            loadFreshWeather(city);
        }else {
            Log.d("WP", "Reading from database");
            weatherDAO.open();
            activity.gotWeather(weatherDAO.getAllWeatherDays());
            weatherDAO.close();
        }

    }

    private void loadFreshWeather(String city) {
        service.getWeather(AppConstants.WEATHER_UNIT,
                AppConstants.API_KEY, city)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .map(this::mapWeatherResponse)
                .subscribe(weatherDays -> {
                    int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

                    weatherDAO.open();
                    weatherDAO.saveAllWeatherDays(weatherDays);
                    weatherDAO.close();

                    preference.setCurrentCity(city);
                    preference.setWeatherSaved(true);
                    preference.setFirstDayDate(currentDay);
                    activity.gotWeather(weatherDays);
                }, throwable -> {
                    if(throwable instanceof HttpException){
                        HttpException exception = (HttpException) throwable;
                        if (exception.code() == 404){
                            activity.cityNotFound();
                            return;
                        }
                    }
                    Log.d("WP", "Error", throwable);

                    if (preference.isWeatherSaved()){
                        activity.gotWeather(weatherDAO.getAllWeatherDays());
                    }else
                        activity.noWeather();
                });
    }

    private String getDayString(int currentDay) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, currentDay);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        String[] dayString = new String[]
                {"Sunday", "Monday", "Tuesday", "Wednesday",
                        "Thursday", "Friday", "Saturday"};

        return dayString[dayOfWeek - 1];
    }

    private List<WeatherDay> mapWeatherResponse(WeatherResponse weatherResponse) {
        List<WeatherItem> originalItemList = weatherResponse.getList();
        List<WeatherItem> currentDayList = new ArrayList<>();
        List<WeatherItem> firstDayList = new ArrayList<>();
        List<WeatherItem> secondDayList = new ArrayList<>();
        List<WeatherItem> thirdDayList = new ArrayList<>();
        List<WeatherItem> fourthDayList = new ArrayList<>();

        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

        for (WeatherItem item : originalItemList) {
            Calendar weatherCalender = Calendar.getInstance();
            String stringTime = item.getDt() + "000";
            weatherCalender.setTimeInMillis(Long.valueOf(stringTime));

            int weatherDay =  weatherCalender.get(Calendar.DAY_OF_YEAR);

            if (weatherDay == currentDay){
                currentDayList.add(item);
            }else if (weatherDay == currentDay + 1){
                firstDayList.add(item);
            }else if(weatherDay == currentDay + 2){
                secondDayList.add(item);
            }else if (weatherDay == currentDay + 3){
                thirdDayList.add(item);
            }else if (weatherDay == currentDay + 4){
                fourthDayList.add(item);
            }
        }

        String cityName = weatherResponse.getCity().getName();

        WeatherDay todayWeather = new WeatherDay(currentDayList,
                cityName, getDayString(currentDay));
        WeatherDay plusOne = new WeatherDay(firstDayList, cityName,
                getDayString(currentDay + 1));
        WeatherDay plusTwo = new WeatherDay(secondDayList, cityName,
                getDayString(currentDay + 2));
        WeatherDay plusThree = new WeatherDay(thirdDayList, cityName,
                getDayString(currentDay + 3));
        WeatherDay plusFour = new WeatherDay(fourthDayList, cityName,
                getDayString(currentDay + 4));

        List<WeatherDay> weatherDays = new ArrayList<>();
        weatherDays.add(todayWeather);
        weatherDays.add(plusOne);
        weatherDays.add(plusTwo);
        weatherDays.add(plusThree);
        weatherDays.add(plusFour);
        return weatherDays;
    }
}