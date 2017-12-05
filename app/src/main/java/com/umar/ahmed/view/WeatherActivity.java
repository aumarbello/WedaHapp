package com.umar.ahmed.view;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
@SuppressWarnings("deprecation")
public class WeatherActivity extends FragmentActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private GoogleApiClient client;
    private static final int permissionReqCode = 21;
    private boolean isFirst;
    private Unbinder unbinder;
    private LocationRequest request;
    private ContainerWeather containerWeather;
    private static final String TAG = "ContainerList";
    private Location userLocation;

    @BindView(R.id.loading_weather_details)
    ContentLoadingProgressBar weather_loading;

    @BindView(R.id.weather_view_pager)
    ViewPager weather_pager;

    @Inject
    WeatherPresenter presenter;

    @Inject
    WeatherPreference preference;

    private View.OnClickListener snackListener = view -> {
        weather_loading.show();
        weather_loading.setVisibility(View.VISIBLE);
        isFirst = false;
        onLocationChanged(userLocation);
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_layout);

        ((WeatherApp)getApplication()).getComponent().inject(this);
        presenter.attachView(this);

        unbinder = ButterKnife.bind(this);
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
                return;
            }
        }

        if (client == null) {
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TAG, containerWeather);

    }

    public void gotWeather(List<WeatherDay> weatherDays) {
        weather_loading.hide();
        weather_loading.setVisibility(View.GONE);

        WeatherPagerAdapter pagerAdapter = new WeatherPagerAdapter
                (getSupportFragmentManager(), weatherDays);
        weather_pager.setAdapter(pagerAdapter);

        containerWeather = new ContainerWeather(weatherDays);
    }

    public void noWeather() {
        weather_loading.hide();
        showSnackMessage("Could not retrieve Weather Now.", true);
    }

//  location callbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, permissionReqCode);
        }else {
            Location userLocation = LocationServices
                    .FusedLocationApi.getLastLocation(client);
            retrieveLocation(userLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        showSnackMessage(getString(R.string.location_error), false);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
      showSnackMessage(getString(R.string.location_error), false);        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    @Override
    protected void onStart() {
        if (client != null){
            client.connect();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        if (client != null){
            client.disconnect();
        }

        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case permissionReqCode:
                if (grantResults.length > 0){
                    if (ActivityCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED){
                        Location userLocation = LocationServices
                                .FusedLocationApi.getLastLocation(client);
                        retrieveLocation(userLocation);
                    }
                }else {
                    showSnackMessage("Location permission denied, " +
                            "Kindly enable permission and try again", false);
                }
        }
    }

    private void retrieveLocation(Location userLocation) throws SecurityException{
        if (userLocation == null){
            request = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)
                    .setFastestInterval(1000);
            LocationServices.FusedLocationApi.requestLocationUpdates
                    (client, request , this);
        }else {
            onLocationChanged(userLocation);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        userLocation = location;
        if (!isFirst){
            int currentDay = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

            boolean loadNewWeather = !(currentDay == preference.getFirstDayDate()
                    && preference.isWeatherSaved());

            presenter.getWeather(location.getLatitude(), location.getLongitude(), loadNewWeather);

            if (request != null){
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }
        }
        isFirst = true;
    }

    private void showSnackMessage(String message, boolean addAction){
        if (addAction){
            Snackbar snack = Snackbar.make(weather_pager, message, Snackbar.LENGTH_INDEFINITE);
            snack.setAction("Try Again", snackListener);
            snack.setActionTextColor(getResources().getColor(R.color.colorPrimary));
            snack.show();
        }else {
            Snackbar.make(weather_pager, message, Snackbar.LENGTH_SHORT).show();
        }
    }
}