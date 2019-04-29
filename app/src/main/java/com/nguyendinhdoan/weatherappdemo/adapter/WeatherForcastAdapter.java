package com.nguyendinhdoan.weatherappdemo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nguyendinhdoan.weatherappdemo.R;
import com.nguyendinhdoan.weatherappdemo.common.Common;
import com.nguyendinhdoan.weatherappdemo.model.Weather;
import com.nguyendinhdoan.weatherappdemo.model.WeatherForecastResult;
import com.squareup.picasso.Picasso;

public class WeatherForcastAdapter extends RecyclerView.Adapter<WeatherForcastAdapter.MyViewHolder> {

    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForcastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast, viewGroup, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        // load image
        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(i).weather.get(0).getIcon())
                .append(".png").toString()).into(myViewHolder.img_weather);

        myViewHolder.txt_date_time.setText(Common.convertUixToDate(weatherForecastResult.list.get(i).dt));
        myViewHolder.txt_description.setText(new StringBuilder(
                weatherForecastResult.list.get(i).weather.get(0).getDescription()
        ));
        myViewHolder.txt_temperature.setText(new StringBuilder(
                String.valueOf(weatherForecastResult.list.get(i).main.getTemp()
        )).append("°C"));
    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView txt_date_time, txt_description, txt_temperature;
        ImageView img_weather;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            img_weather = itemView.findViewById(R.id.img_weather);
            txt_date_time = itemView.findViewById(R.id.txt_date_time);
            txt_description = itemView.findViewById(R.id.txt_description);
            txt_temperature = itemView.findViewById(R.id.txt_temperature);

        }
    }
}
