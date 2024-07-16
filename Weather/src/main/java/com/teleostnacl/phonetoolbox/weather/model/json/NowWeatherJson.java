package com.teleostnacl.phonetoolbox.weather.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * 当前的天气情况JSON
 */
public class NowWeatherJson {
    @SerializedName("now")
    @Expose
    public Now now;

    public static class Now {
        /**
         * 数据观测时间
         */
        @SerializedName("obsTime")
        @Expose
        public String obsTime;

        /**
         * 当前温度
         */
        @SerializedName("temp")
        @Expose
        public String temp;

        /**
         * 体感温度
         */
        @SerializedName("feelsLike")
        @Expose
        public String feelsLike;

        /**
         * 天气状况的图标代码
         */
        @SerializedName("icon")
        @Expose
        public String icon;

        /**
         * 天气状态
         */
        @SerializedName("text")
        @Expose
        public String text;

        /**
         * 风向360角度
         */
        @SerializedName("wind360")
        @Expose
        public String wind360;

        /**
         * 风向
         */
        @SerializedName("windDir")
        @Expose
        public String windDir;

        /**
         * 风力等级
         */
        @SerializedName("windScale")
        @Expose
        public String windScale;

        /**
         * 风速，公里/小时
         */
        @SerializedName("windSpeed")
        @Expose
        public String windSpeed;

        /**
         * 相对湿度，百分比数值
         */
        @SerializedName("humidity")
        @Expose
        public String humidity;

        /**
         * 当前小时累计降水量，默认单位：毫米
         */
        @SerializedName("precip")
        @Expose
        public String precip;

        /**
         * 大气压强，默认单位：百帕
         */
        @SerializedName("pressure")
        @Expose
        public String pressure;

        /**
         * 能见度，默认单位：公里
         */
        @SerializedName("vis")
        @Expose
        public String vis;

        /**
         * 云量，百分比数值。可能为空
         */
        @SerializedName("cloud")
        @Expose
        public String cloud;

        /**
         * 露点温度。可能为空
         */
        @SerializedName("dew")
        @Expose
        public String dew;
    }
}
