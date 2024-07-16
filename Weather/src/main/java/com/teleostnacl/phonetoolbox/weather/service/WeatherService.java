package com.teleostnacl.phonetoolbox.weather.service;

import android.content.Intent;

import androidx.annotation.NonNull;

import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.weather.viewmodel.WeatherViewModel;

/**
 * 天气常住服务
 */
public class WeatherService implements NotificationUtils.IService {

    private WeatherViewModel weatherViewModel;

    @Override
    public void onCreate() {
        weatherViewModel = new WeatherViewModel();

//        weatherViewModel.getWeather();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onStartCommand(@NonNull Intent intent) {

    }
}
