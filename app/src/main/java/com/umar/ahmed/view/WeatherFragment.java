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

    public WeatherFragment(){

    }

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



    public static WeatherFragment getInstance(WeatherDay day){
        WeatherFragment fragment = new WeatherFragment();
        Bundle args = new Bundle();
        args.putSerializable(WeatherExtra, day);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = View.inflate(getActivity(),
                R.layout.weather_fragment, container);
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

            //TODO Load background image for view;

            WeatherItem currentItem = getCurrentWeatherItem(weatherDay.getStatsList());
            updateFragmentViews(currentItem);

        }else
            Log.d("WeatherFragment", "WeatherDay is null");
        return view;
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
        weatherDescription.setText(firstWeather.getDescription());
        //max and min temperature
        currentMinTemp.setText(getString(R.string.empty_degree, weatherMain.getTempMin()));
        currentMaxTemp.setText(getString(R.string.empty_degree, weatherMain.getTempMax()));
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
