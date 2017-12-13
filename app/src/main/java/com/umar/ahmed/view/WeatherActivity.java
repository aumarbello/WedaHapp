package com.umar.ahmed.view;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.EditText;

import com.umar.ahmed.WeatherApp;
import com.umar.ahmed.data.local.WeatherPreference;
import com.umar.ahmed.data.local.model.ContainerWeather;
import com.umar.ahmed.data.local.model.WeatherDay;
import com.umar.ahmed.presenter.WeatherPresenter;
import com.umar.ahmed.weatherapp.R;

import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by ahmed on 11/6/17.
 */
public class WeatherActivity extends FragmentActivity
        implements WeatherFragment.WeatherBack{

    private Unbinder unbinder;
    private ContainerWeather containerWeather;
    private static final String TAG = "ContainerList";
    private WeatherPagerAdapter adapter;

    @BindView(R.id.loading_weather_details)
    ContentLoadingProgressBar weather_loading;

    @BindView(R.id.weather_view_pager)
    ViewPager weather_pager;

    @BindView(R.id.reload_weather)
    SwipeRefreshLayout refreshWeather;

    @Inject
    WeatherPresenter presenter;

    @Inject
    WeatherPreference preference;

    private View.OnClickListener snackListener = view -> {
        weather_loading.show();
        weather_loading.setVisibility(View.VISIBLE);
        getCityAndLoadWeather();
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
        preference.setWeatherSaved(false);
        getCityAndLoadWeather();
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        ((WeatherApp)getApplication()).getComponent().inject(this);
        presenter.attachView(this);

        unbinder = ButterKnife.bind(this);
        refreshWeather.setOnRefreshListener(refreshListener);
        weather_loading.show();

        if (savedInstanceState != null){
            containerWeather = (ContainerWeather) savedInstanceState
                    .getSerializable(TAG);
            if (containerWeather != null){
                weather_loading.hide();

                List<WeatherDay> dayList =containerWeather.getWeatherDays();
                WeatherPagerAdapter pagerAdapter = new
                        WeatherPagerAdapter(getSupportFragmentManager() ,
                        dayList);
                weather_pager.setAdapter(pagerAdapter);
            }
        }else
            getCityAndLoadWeather();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TAG, containerWeather);

    }

    public void gotWeather(List<WeatherDay> weatherDays) {
        refreshWeather.setRefreshing(false);
        weather_loading.hide();
        weather_loading.setVisibility(View.GONE);


        if (adapter == null){
            adapter = new WeatherPagerAdapter
                    (getSupportFragmentManager(), weatherDays);
        }else{
            ((WeatherPagerAdapter)weather_pager.getAdapter()).setListItems(weatherDays);
            weather_pager.getAdapter().notifyDataSetChanged();
        }

        weather_pager.setAdapter(adapter);

        containerWeather = new ContainerWeather(weatherDays);
    }

    public void noWeather() {
        weather_loading.hide();
        showSnackMessage(getString(R.string.weather_loading_error_msg), true);
    }

    public void cityNotFound(){
        refreshWeather.setRefreshing(false);
        showSnackMessage(getString(R.string.invalid_city), false);
    }

    private void getCityAndLoadWeather() {
        String city = preference.getCurrentCity();

        presenter.getWeather(shouldLoadNewWeather(), city);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
            unbinder.unbind();
        }

        if (presenter != null){
            presenter.closeOnDestroy();
        }
    }

    private boolean shouldLoadNewWeather() {
        int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        return !(currentDay == preference.getFirstDayDate()
                && preference.isWeatherSaved());
    }

    private void showSnackMessage(String message, boolean addAction){
        if (addAction){
            Snackbar.make(weather_pager, message, Snackbar.LENGTH_INDEFINITE)
            .setAction("Try Again", snackListener)
            .setActionTextColor(getResources().getColor(R.color.colorPrimary))
            .show();
        }else {
            Snackbar.make(weather_pager, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    @Override
    public void loadNewWeather() {
        View editView = View.inflate(this, R.layout.dialog_view, null);

        EditText city = editView.findViewById(R.id.enter_city);

        AlertDialog cityDialog = new AlertDialog.Builder(this)
                .setTitle("Enter Valid City")
                .setView(editView)
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    showSnackMessage(getString(R.string.loading_new), false);
                    presenter.getWeather(true, city.getText().toString().trim());
                })
                .create();

        cityDialog.show();
    }
}