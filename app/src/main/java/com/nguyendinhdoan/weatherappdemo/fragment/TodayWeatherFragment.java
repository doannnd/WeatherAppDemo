package com.nguyendinhdoan.weatherappdemo.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nguyendinhdoan.weatherappdemo.R;
import com.nguyendinhdoan.weatherappdemo.common.Common;
import com.nguyendinhdoan.weatherappdemo.model.WeatherResult;
import com.nguyendinhdoan.weatherappdemo.retrofit.IOpenWeatherMap;
import com.nguyendinhdoan.weatherappdemo.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    private ImageView img_weather;
    private TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mServices;

    private static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }
        return instance;
    }

    public TodayWeatherFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServices = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);

        img_weather = view.findViewById(R.id.img_weather);
        txt_city_name = view.findViewById(R.id.txt_city_name);
        txt_humidity = view.findViewById(R.id.txt_humidity);
        txt_sunrise = view.findViewById(R.id.txt_sunrise);
        txt_sunset = view.findViewById(R.id.txt_sunset);
        txt_pressure = view.findViewById(R.id.txt_pressure);
        txt_temperature = view.findViewById(R.id.txt_temperature);
        txt_description = view.findViewById(R.id.txt_description);
        txt_date_time = view.findViewById(R.id.txt_date_time);
        txt_wind = view.findViewById(R.id.txt_wind);
        txt_geo_coord = view.findViewById(R.id.txt_geo_coord);
        loading = view.findViewById(R.id.loading);

        weather_panel = view.findViewById(R.id.weather_panel);

        getWeatherInformation();

        return view;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mServices.getWeatherByLatLng(
                String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {

                        Log.d("data", "lat" + weatherResult.getCoord().getLat());

                        // load image
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                        .append(weatherResult.getWeather().get(0).getIcon())
                        .append(".png").toString()).into(img_weather);

                        Log.d("icon", "icon: " + weatherResult.getWeather().get(0).getIcon());

                        // load information
                        txt_city_name.setText(weatherResult.getName());
                        txt_description.setText(new StringBuilder("Weather in ")
                        .append(weatherResult.getName()).toString());

                        txt_temperature.setText(new StringBuilder(String.valueOf(
                                weatherResult.getMain().getTemp()
                        )).append("°C").toString());

                        txt_date_time.setText(Common.convertUixToDate(weatherResult.getDt()));

                        txt_pressure.setText(
                                new StringBuilder(
                                        String.valueOf(weatherResult.getMain().getPressure())
                                ).append(" hpa").toString()
                        );

                        txt_humidity.setText(
                                new StringBuilder(
                                        String.valueOf(weatherResult.getMain().getHumidity())
                                ).append(" %").toString()
                        );

                        txt_sunrise.setText(
                                Common.convertUnixToHour(
                                        weatherResult.getSys().getSunrise()
                                )
                        );

                        txt_sunset.setText(
                                Common.convertUnixToHour(
                                        weatherResult.getSys().getSunset()
                                )
                        );

                        txt_geo_coord.setText(
                               new StringBuilder(
                                       String.valueOf(weatherResult.getCoord().getLat())
                               )
                                .append(" ").append(weatherResult.getCoord().getLon())
                        );

                       // display panel
                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);

                    }
                })
        );
    }

}
