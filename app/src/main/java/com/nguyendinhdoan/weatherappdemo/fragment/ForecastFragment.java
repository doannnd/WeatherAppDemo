package com.nguyendinhdoan.weatherappdemo.fragment;


import android.os.Bundle;
import android.support.annotation.MainThread;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nguyendinhdoan.weatherappdemo.R;
import com.nguyendinhdoan.weatherappdemo.adapter.WeatherForcastAdapter;
import com.nguyendinhdoan.weatherappdemo.common.Common;
import com.nguyendinhdoan.weatherappdemo.model.WeatherForecastResult;
import com.nguyendinhdoan.weatherappdemo.retrofit.IOpenWeatherMap;
import com.nguyendinhdoan.weatherappdemo.retrofit.RetrofitClient;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

/**
 * A simple {@link Fragment} subclass.
 */
public class ForecastFragment extends Fragment {

    private static final String TAG = "forecast fragment";
    static ForecastFragment instance;
    IOpenWeatherMap mServices;

    TextView txt_city_name, txt_geo_coord;
    RecyclerView recycler_forecast;

    CompositeDisposable compositeDisposable;

    public ForecastFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServices = retrofit.create(IOpenWeatherMap.class);
    }

    public static ForecastFragment getInstance() {
        if (instance == null) {
            instance = new ForecastFragment();
        }
        return instance;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_forecast, container, false);

        txt_city_name = view.findViewById(R.id.txt_city_name);
        txt_geo_coord = view.findViewById(R.id.txt_geo_coord);
        recycler_forecast = view.findViewById(R.id.recycler_forecast);

        recycler_forecast.setHasFixedSize(true);
        recycler_forecast.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        getForecastWeatherInformation();

        return view;
    }

    private void getForecastWeatherInformation() {
        compositeDisposable.add(
                mServices.getForecastWeatherByLatLng(
                        String.valueOf(Common.current_location.getLatitude()),
                        String.valueOf(Common.current_location.getLongitude()),
                        Common.APP_ID,
                        "metric"
                ).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<WeatherForecastResult>() {
                            @Override
                            public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                                displayForecastWeather(weatherForecastResult);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.d(TAG, "error: " + throwable.getMessage());
                            }
                        })
        );
    }

    private void displayForecastWeather(WeatherForecastResult weatherForecastResult) {
        txt_city_name.setText(new StringBuilder(
                weatherForecastResult.city.name
        ));

        txt_geo_coord.setText(new StringBuilder("[")
        .append(weatherForecastResult.city.coord.toString()));

        WeatherForcastAdapter weatherForcastAdapter = new WeatherForcastAdapter(getContext(), weatherForecastResult);
        recycler_forecast.setAdapter(weatherForcastAdapter);
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
