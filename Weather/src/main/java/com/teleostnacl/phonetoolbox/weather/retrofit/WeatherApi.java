package com.teleostnacl.phonetoolbox.weather.retrofit;

import com.teleostnacl.phonetoolbox.weather.model.json.DailyWeatherJson;
import com.teleostnacl.phonetoolbox.weather.model.json.IndicesJson;
import com.teleostnacl.phonetoolbox.weather.model.json.NowWeatherJson;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApi {
    String KEY = "2e2e1922964c41b3bc77639cfe6b566c";

    /**
     * 获取实时天气
     *
     * @param key      用户认证key 恒为 {@link #KEY}
     * @param location 经度,纬度（经度在前纬度在后，英文逗号分隔，十进制格式，北纬东经为正，南纬西经为负）
     */
    @GET("weather/now")
    Single<NowWeatherJson> getNowWeather(@Query("key") String key, @Query("location") String location);

    /**
     * 获取每日天气情况
     *
     * @param key      用户认证key 恒为 {@link #KEY}
     * @param location 经度,纬度（经度在前纬度在后，英文逗号分隔，十进制格式，北纬东经为正，南纬西经为负）
     */
    @GET("weather/3d")
    Single<DailyWeatherJson> getDailyWeather(@Query("key") String key, @Query("location") String location);


    /**
     * 获取每日的生活指数
     *
     * @param key      用户认证key 恒为 {@link #KEY}
     * @param location 经度,纬度（经度在前纬度在后，英文逗号分隔，十进制格式，北纬东经为正，南纬西经为负）
     * @param type     生活指数的类型ID，包括洗车指数、穿衣指数、钓鱼指数等。可以一次性获取多个类型的生活指数，多个类型用英文,分割。
     */
    @GET("indices/1d")
    Single<IndicesJson> getDailyIndices(@Query("key") String key, @Query("location") String location, @Query("type") String type);
}
