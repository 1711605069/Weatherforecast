package com.example.weatherforecast;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weatherforecast.gson.Forecast;
import com.example.weatherforecast.gson.Weather;
import com.example.weatherforecast.util.HttpUtil;
import com.example.weatherforecast.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {
    //增加下拉刷新控件
    private SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;



    private ScrollView weatherLyout;
    private TextView titltCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    public DrawerLayout drawerLayout;
    private Button navButton;{}
    private ImageView bingPicImg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        weatherLyout = findViewById(R.id.weather_layout);
        titltCity = findViewById(R.id.title_city);
        titleUpdateTime = findViewById(R.id.title_update_time);
        degreeText = findViewById(R.id.degree_text);
        weatherInfoText = findViewById(R.id.weather_info_text);
        forecastLayout = findViewById(R.id.forecast_layout);
        aqiText = findViewById(R.id.aqi_text);
        pm25Text = findViewById(R.id.pm25_text);
        comfortText = findViewById(R.id.comfort_text);
        carWashText = findViewById(R.id.car_wash_text);
        sportText = findViewById(R.id.sport_text);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);
        bingPicImg = findViewById(R.id.background_pic);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        swipeRefresh=findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pres.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId=weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            mWeatherId=getIntent().getStringExtra("weather_id");
            weatherLyout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });

        String bingpic = pres.getString("picture", null);
        if (bingpic != null) {
            Glide.with(this).load(bingpic).into(bingPicImg);
        } else {
            loadBackground();
        }


    }



    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=9335c244496241329765b47705f13a08";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
                loadBackground();
            }


            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            mWeatherId=weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }


    private void loadBackground() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
            }
        });
    }
    private void showWeatherInfo(Weather weatherlx) {
        String cityName = weatherlx.basic.cityName;
        String updatetime = weatherlx.basic.update.updateTime.split("")[1];
        String degree = weatherlx.now.temperature + "'C";
        String weatherInfo = weatherlx.now.more.info;
        titltCity.setText(cityName);
        titleUpdateTime.setText(updatetime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for (Forecast forecastlx : weatherlx.forecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false);
            TextView dateText = view.findViewById(R.id.date_text);
            TextView infoText = view.findViewById(R.id.info_text);
            TextView maxText = view.findViewById(R.id.max_text);
            TextView minText = view.findViewById(R.id.min_text);
            dateText.setText(forecastlx.date);
            infoText.setText(forecastlx.temperature.max);
            minText.setText(forecastlx.temperature.min);
            forecastLayout.addView(view);
        }
        if (weatherlx.aqi != null) {
            aqiText.setText(weatherlx.aqi.city.aqi);
            pm25Text.setText(weatherlx.aqi.city.pm25);
        }
        String comfort = "舒适度: " + weatherlx.suggestion.comfort.info;
        String carWash = "洗车指数: " + weatherlx.suggestion.carWash.info;
        String sport = "运动建议： " + weatherlx.suggestion.sport.info;
        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLyout.setVisibility(View.VISIBLE);

    }
}
