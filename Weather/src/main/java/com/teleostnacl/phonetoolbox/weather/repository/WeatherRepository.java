package com.teleostnacl.phonetoolbox.weather.repository;

import androidx.annotation.NonNull;

import com.teleostnacl.phonetoolbox.lib.util.WeatherUtils;
import com.teleostnacl.phonetoolbox.weather.model.json.DailyWeatherJson;
import com.teleostnacl.phonetoolbox.weather.retrofit.WeatherApi;
import com.teleostnacl.phonetoolbox.weather.retrofit.WeatherRetrofit;

import io.reactivex.rxjava3.core.Single;

public class WeatherRepository {
    private final WeatherApi weatherApi = new WeatherRetrofit().weatherApi();

    /**
     * 获取当前的天气状态
     */
    public Single<Boolean> getNowWeather(@NonNull WeatherUtils.Weather weather) {
        return weatherApi.getNowWeather(WeatherApi.KEY, weather.location)
                .map(nowWeatherJson -> {
                    weather.currentTemperature = nowWeatherJson.now.temp;
                    weather.currentFeelsLike = nowWeatherJson.now.feelsLike;
                    weather.currentWeatherText = nowWeatherJson.now.text;
                    return true;
                });
    }

    /**
     * 获取每日的天气状态
     */
    public Single<Boolean> getDailyWeather(@NonNull WeatherUtils.Weather weather) {
        return weatherApi.getDailyWeather(WeatherApi.KEY, weather.location)
                .map(json -> {
                    for (int i = 0; i < 3; i++) {
                        DailyWeatherJson.Daily daily = json.daily.get(i);
                        switch (i) {
                            case 0:
                                weather.day1Date = daily.fxDate;
                                weather.day1MaxTempe = daily.tempMax;
                                weather.day1MinTempe = daily.tempMin;
                                weather.day1WeatherText = daily.textDay;
                                break;
                            case 1:
                                weather.day2Date = daily.fxDate;
                                weather.day2MaxTempe = daily.tempMax;
                                weather.day2MinTempe = daily.tempMin;
                                weather.day2WeatherText = daily.textDay;
                                break;
                            case 2:
                                weather.day3Date = daily.fxDate;
                                weather.day3MaxTempe = daily.tempMax;
                                weather.day3MinTempe = daily.tempMin;
                                weather.day3WeatherText = daily.textDay;
                                break;
                        }
                    }
                    return true;
                });
    }

    /**
     * 获取每日的穿衣指数
     */
    public Single<Boolean> geyDailyDress(@NonNull WeatherUtils.Weather weather) {
        return weatherApi.getDailyIndices(WeatherApi.KEY, weather.location, "3")
                .map(json -> {
                    weather.dressText = json.daily.get(0).text;
                    return true;
                });
    }
}
