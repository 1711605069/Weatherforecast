package com.example.weatherforecast;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherforecast.gson.Forecast;
import com.example.weatherforecast.gson.Weather;
import com.example.weatherforecast.util.HttpUtil;
import com.example.weatherforecast.util.Utility;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    //增加下拉刷新控件
    private SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;

    private Button btnmore;
    private NavigationView navview;

    private CircleImageView circleimage;
    private TextView et_user_name;

    private CollapsingToolbarLayout collapsingToolbarLayout;

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
    private TextView drsgText;
    public DrawerLayout drawerLayout;
    private Button navButton;

    {
    }

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
        drsgText = findViewById(R.id.drsg_text);
        drawerLayout = findViewById(R.id.drawer_layout);
        navButton = findViewById(R.id.nav_button);



        btnmore = findViewById(R.id.btn_more);
        btnmore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.END);
                collapsingToolbarLayout =  findViewById(R.id.collapsing_tool_bar);
                collapsingToolbarLayout.setTitle("无登陆");
            }
        });

        navview = findViewById(R.id.nav_view);
        navview.setNavigationItemSelectedListener(this);

        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });



        swipeRefresh = findViewById(R.id.swipe_refresh);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);

        SharedPreferences pres = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = pres.getString("weather", null);
        if (weatherString != null) {
            Weather weather = Utility.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            mWeatherId = getIntent().getStringExtra("weather_id");
            weatherLyout.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // requestWeather(mWeatherId);
                new Handler().postAtTime(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "更新成功", Toast.LENGTH_LONG).show();
                        swipeRefresh.setRefreshing(false);
                    }
                }, 1000);
//
            }
        });
        runnable.run();
    }









public void circle_onclick(View view){
        Intent intent=new Intent(WeatherActivity.this,LoginActivity.class);
        startActivityForResult(intent, 1);
}
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && data != null) {
            String userName = data.getStringExtra("LoginUserName");
            collapsingToolbarLayout.setTitle(userName);
        }
    }


    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=9f985d6bd8124ed781dbdf678e3db383";
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
                            mWeatherId = weather.basic.weatherId;
                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败!请向右滑选择城市", Toast.LENGTH_SHORT).show();
                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
    }

    private void showWeatherInfo(Weather weatherlx) {
        String cityName = weatherlx.basic.cityName;
        String updatetime = weatherlx.basic.update.updateTime.split("")[1];
        String degree = weatherlx.now.temperature + "℃";
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
        // String fluent = "感冒指数: " +weatherlx.suggestion.fluent.info;

        comfortText.setText(comfort);
        carWashText.setText(carWash);
        sportText.setText(sport);


        weatherLyout.setVisibility(View.VISIBLE);

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            requestWeather(mWeatherId);
            handler.postDelayed(this, 120000);
        }
    };

    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
        }
        super.onDestroy();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id=menuItem.getItemId();
        if (id==R.id.circle_image){
            Intent intent2=new Intent(WeatherActivity.this, LoginActivity.class);
            startActivity(intent2);
        }

        return true;
    }

}
