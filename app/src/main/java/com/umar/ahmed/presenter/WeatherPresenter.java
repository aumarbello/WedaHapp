package com.umar.ahmed.presenter;

import android.util.Log;

import com.google.gson.Gson;
import com.umar.ahmed.AppConstants;
import com.umar.ahmed.data.local.db.WeatherDAO;
import com.umar.ahmed.data.local.model.WeatherDay;
import com.umar.ahmed.data.local.model.WeatherItem;
import com.umar.ahmed.data.remote.WeatherService;
import com.umar.ahmed.view.WeatherActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by ahmed on 11/6/17.
 */

public class WeatherPresenter {
    private WeatherActivity activity;
    private WeatherDAO weatherDAO;

    public WeatherPresenter(WeatherActivity activity){
        this.activity =  activity;
        weatherDAO = new WeatherDAO(activity);
    }

    public void getWeather(double lat, double lon, boolean freshOut){
        if (freshOut){
            loadFreshWeather(lat, lon);
        }else {
            Log.d("WP", "Reading from database");
            weatherDAO.open();
            activity.gotWeather(weatherDAO.getAllWeatherDays());
            weatherDAO.close();
        }

    }

    private void loadFreshWeather(double lat, double lon) {
        provideService().getWeather(lat, lon, "metric",
                AppConstants.API_KEY).
                observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(weatherResponse -> {
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

                    Log.d("WeatherPresenter", "Size of current day list - " + currentDayList.size());
                    Log.d("WeatherPresenter", "Size of first day list - " + firstDayList.size());
                    Log.d("WeatherPresenter", "Size of second day list - " + secondDayList.size());
                    Log.d("WeatherPresenter", "Size of third day list - " + thirdDayList.size());
                    Log.d("WeatherPresenter", "Size of fourth day list - " + fourthDayList.size());

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

//                    weatherDAO.open();
//                    weatherDAO.saveAllWeatherDays(weatherDays);
//                    weatherDAO.close();
                    activity.gotWeather(weatherDays);
                }, throwable -> {
                    Log.d("WP", "Error", throwable);
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

    private WeatherService provideService(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.ENDPOINT)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(new Gson()))
                .client(client)
                .build();

        return retrofit.create(WeatherService.class);
    }
}
