package com.teleostnacl.phonetoolbox.weather.retrofit;

import com.teleostnacl.common.android.retrofit.BaseRetrofit;

import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * 请求天气API的retrofit
 */
public class WeatherRetrofit extends BaseRetrofit {

    private static final String BASE_URL = "https://devapi.qweather.com/v7/";

    public WeatherRetrofit() {
        super(new OkHttpClient.Builder().build(), RxJava3CallAdapterFactory.create(), GsonConverterFactory.create());
    }

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    public WeatherApi weatherApi() {
        return create(WeatherApi.class);
    }
}
