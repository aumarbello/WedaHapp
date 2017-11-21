package com.umar.ahmed.view;

import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.umar.ahmed.data.local.model.Weather;
import com.umar.ahmed.data.local.model.WeatherItem;
import com.umar.ahmed.weatherapp.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by ahmed on 11/16/17.
 */

class WeatherAdapter  extends RecyclerView.Adapter<WeatherAdapter.WeatherHolder>{
    private List<WeatherItem> itemList;
    private WeatherFragment fragment;
    private Resources weatherResources;

    WeatherAdapter(List<WeatherItem> itemList, WeatherFragment fragment) {
        this.itemList = itemList;
        this.fragment = fragment;
        weatherResources = fragment.getResources();
    }

    @Override
    public WeatherHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getActivity());
        View weatherView = inflater.inflate(R.layout.weather_list_item, parent,
                false);
        return new WeatherHolder(weatherView);
    }

    @Override
    public void onBindViewHolder(WeatherHolder holder, int position) {
        WeatherItem item = itemList.get(position);
        holder.bindWeather(item);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class WeatherHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener{
        @BindView(R.id.list_item_avg_temp)
        TextView averageWeather;

        @BindView(R.id.list_item_icon)
        ImageView weatherIcon;

        @BindView(R.id.list_item_time)
        TextView weatherTime;

        private WeatherItem item;
        WeatherHolder(View itemView) {
            super(itemView);

            itemView.setOnClickListener(this);
            ButterKnife.bind(this, itemView);
        }

        void bindWeather(WeatherItem item){
            this.item = item;
            //weather String
            String[] weatherStrings = item.getDtTxt().split(" ");

            String hourString = weatherStrings[1].substring(0, 5);
            weatherTime.setText(hourString);

            //averageWeather
            averageWeather.setText(weatherResources.getString(R.string.empty_degree,
                    item.getMain().getTemp()));

            //weather_icon
            Weather firstWeather = item.getWeather().get(0);
            String firstIcon = firstWeather.getIcon();

            switch (WeatherFragment.getWeatherInt(firstIcon)){
                case 0:
                    weatherIcon.setImageDrawable(weatherResources.getDrawable(R.drawable.partly_cloudy));
                    break;
                case 1:
                    weatherIcon.setImageDrawable(weatherResources.getDrawable(R.drawable.clouds));
                    break;
                case 2:
                    weatherIcon.setImageDrawable(weatherResources.getDrawable(R.drawable.rain));
                    break;
                case 3:
                    weatherIcon.setImageDrawable(weatherResources.getDrawable(R.drawable.sunny));
                    break;
            }
        }

        @Override
        public void onClick(View view) {
            fragment.updateFragmentViews(item);
        }
    }
}
