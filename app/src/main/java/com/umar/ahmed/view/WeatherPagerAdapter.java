package com.umar.ahmed.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.umar.ahmed.data.local.model.WeatherDay;

import java.util.List;

/**
 * Created by ahmed on 11/15/17.
 */

class WeatherPagerAdapter extends FragmentStatePagerAdapter {
    private List<WeatherDay> weatherDays;
    WeatherPagerAdapter(FragmentManager fm, List<WeatherDay> weatherDays) {
        super(fm);
        this.weatherDays = weatherDays;
    }

    @Override
    public Fragment getItem(int position) {
        return WeatherFragment.getInstance(weatherDays.get(position));
    }

    @Override
    public int getCount() {
        return weatherDays.size();
    }
}
