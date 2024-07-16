package com.teleostnacl.phonetoolbox.weather.viewmodel;

import android.util.Log;

import com.teleostnacl.phonetoolbox.lib.util.WeatherUtils;
import com.teleostnacl.phonetoolbox.weather.repository.WeatherRepository;

import io.reactivex.rxjava3.core.Single;

/**
 * 管理天气数据的ViewModel层
 */
public class WeatherViewModel {
    private final WeatherRepository weatherRepository = new WeatherRepository();

    public void getWeather() {
        Single.zip(weatherRepository.getNowWeather(WeatherUtils.weatherGuangzhou),
                        weatherRepository.getDailyWeather(WeatherUtils.weatherGuangzhou),
                        weatherRepository.geyDailyDress(WeatherUtils.weatherGuangzhou),
                        (aBoolean, aBoolean2, aBoolean3) -> {
                            Log.i("Weather", WeatherUtils.weatherGuangzhou.toString());
                            return true;
                        })
                .onErrorReturnItem(false)
                .subscribe();
    }
}
