package com.nguyendinhdoan.weatherappdemo.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.nguyendinhdoan.weatherappdemo.R;
import com.nguyendinhdoan.weatherappdemo.common.Common;
import com.nguyendinhdoan.weatherappdemo.model.WeatherResult;
import com.nguyendinhdoan.weatherappdemo.retrofit.IOpenWeatherMap;
import com.nguyendinhdoan.weatherappdemo.retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class CityFragment extends Fragment {

    private List<String> listCity;
    private MaterialSearchBar searchBar;

    private ImageView img_weather;
    private TextView txt_city_name, txt_humidity, txt_sunrise, txt_sunset, txt_pressure, txt_temperature, txt_description, txt_date_time, txt_wind, txt_geo_coord;
    LinearLayout weather_panel;
    ProgressBar loading;

    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mServices;

    private static CityFragment instance;

    public static CityFragment getInstance() {
        if (instance == null) {
            instance = new CityFragment();
        }
        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mServices = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_city, container, false);


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

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setEnabled(false);

        new LoadCities().execute(); // asyntask class load cities list

        return view;
    }

    private class LoadCities extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {
            listCity = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);


                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while ((readed = in.readLine()) != null) {
                    builder.append(readed);
                }

                listCity = new Gson().fromJson(builder.toString(), new TypeToken<String>() {
                }.getType());

            } catch (IOException e) {
                e.printStackTrace();
            }
            return listCity;
        }

        @Override
        protected void onSuccess(final List<String> listCity) {
            super.onSuccess(listCity);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : listCity) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                            suggest.add(search);
                        }
                    }
                    searchBar.setLastSuggestions(suggest);
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());

                    searchBar.setLastSuggestions(listCity);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });

            searchBar.setLastSuggestions(listCity);
            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInformation(String cityName) {
        compositeDisposable.add(mServices.getWeatherByCityName(
                cityName,
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

    @Override
    public void onDestroyView() {
        compositeDisposable.clear();
        super.onDestroyView();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
