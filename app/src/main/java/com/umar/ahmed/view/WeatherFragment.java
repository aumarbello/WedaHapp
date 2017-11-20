package com.umar.ahmed.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.umar.ahmed.data.local.model.Main;
import com.umar.ahmed.data.local.model.Weather;
import com.umar.ahmed.data.local.model.WeatherDay;
import com.umar.ahmed.data.local.model.WeatherItem;
import com.umar.ahmed.weatherapp.R;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ahmed on 11/15/17.
 */

public class WeatherFragment extends Fragment {
    private static final String WeatherExtra = "Day_Extra";
    private Unbinder unbinder;
    private String cityName;
    private String dayString;

    @BindView(R.id.weather_city)
    TextView weatherCity;

    @BindView(R.id.weather_degree)
    TextView weatherDegree;

    @BindView(R.id.weather_description)
    TextView weatherDescription;

    @BindView(R.id.weather_day)
    TextView weatherDayString;

    @BindView(R.id.weather_current_min_temp)
    TextView currentMinTemp;

    @BindView(R.id.weather_current_max_temp)
    TextView currentMaxTemp;

    @BindView(R.id.weather_day_list)
    HorizontalGridView dayWeatherList;

    @BindView(R.id.weather_back)
    RelativeLayout weatherBack;

    public static WeatherFragment getInstance(WeatherDay day){
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putSerializable(WeatherExtra, day);
        fragment.setArguments(args);

        Log.d("WF", "Received day " + day.getDayString());

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.weather_fragment, container,
                false);

        unbinder = ButterKnife.bind(this, view);
        WeatherDay weatherDay = (WeatherDay) getArguments()
                .getSerializable(WeatherExtra);

        if (weatherDay != null){
            cityName = weatherDay.getCityName();
            dayString = weatherDay.getDayString();

            WeatherAdapter adapter = new WeatherAdapter(weatherDay.getStatsList(),
                    this);
            dayWeatherList.setAdapter(adapter);
            dayWeatherList.setLayoutManager(new LinearLayoutManager(getActivity(),
                    LinearLayoutManager.HORIZONTAL, false));
            dayWeatherList.setHasFixedSize(true);

            WeatherItem currentItem = getCurrentWeatherItem(weatherDay.getStatsList());
            updateFragmentViews(currentItem);
            Log.d("WF", "WD is not null");
        }else
            Log.d("WeatherFragment", "WeatherDay is null");
        return view;
    }

    public static int getWeatherInt(String icon){
        if (icon.startsWith("02")) {
            return 0;
        }else if (icon.startsWith("03") ||  icon.startsWith("04")){
            return 1;
        }else if (icon.startsWith("09") || icon.startsWith("10")
                ||  icon.startsWith("11")){
            return 2;
        }else {
            return 3;
        }
    }

    void updateFragmentViews(WeatherItem item) {
        //weatherCity
        weatherCity.setText(cityName);
        //dayString
        weatherDayString.setText(dayString);
        //weatherDegree
        Main weatherMain = item.getMain();
        weatherDegree.setText(getString(R.string.empty_degree, weatherMain.getTemp()));
        //weatherDescription
        Weather firstWeather = item.getWeather().get(0);

        String[] weatherDescriptions = firstWeather.getDescription().split(" ");

        String finalWeatherString = "";

        for (String string : weatherDescriptions) {
            char first = string.trim().charAt(0);
            String lower = first + "";
            String upper = lower.toUpperCase();
            string = string.replaceFirst(lower, upper);

            finalWeatherString = finalWeatherString.concat(string).concat(" ");
        }

        weatherDescription.setText(finalWeatherString);
        //max and min temperature
        currentMinTemp.setText(getString(R.string.empty_degree, weatherMain.getTempMin()));
        currentMaxTemp.setText(getString(R.string.empty_degree, weatherMain.getTempMax()));

        //background image
        String icon =  item.getWeather().get(0).getIcon();
        loadBackgroundImage(icon);
    }

    private void loadBackgroundImage(String icon) {
        switch (getWeatherInt(icon)){
            case 0:
                weatherBack.setBackground(
                        getResources().getDrawable(R.drawable.partly_cloudy_back));
                break;
            case 1:
                weatherBack.setBackground(getResources().getDrawable(R.drawable.cloud_back));
                break;
            case 2:
                weatherBack.setBackground(getResources().getDrawable(R.drawable.rain_back));
                break;
            case 3:
                weatherBack.setBackground(getResources().getDrawable(R.drawable.sun_back));
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    public WeatherItem getCurrentWeatherItem(List<WeatherItem> itemList) {
        WeatherItem currentItem = itemList.get(0);
        int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        for (WeatherItem item : itemList){
            Calendar weatherCalender = Calendar.getInstance();
            weatherCalender.setTimeInMillis(item.getDt());
            int weatherHour = weatherCalender.get(Calendar.HOUR_OF_DAY);
            if (weatherHour ==  currentHour){
                return item;
            }
        }
        return currentItem;
    }
}
