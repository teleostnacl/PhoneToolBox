package com.teleostnacl.phonetoolbox.weather.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 每日天气情况的JSON
 */
public class DailyWeatherJson {
    @SerializedName("daily")
    @Expose
    public List<Daily> daily;

    public static class Daily {

        /**
         * 预报日期
         */
        @SerializedName("fxDate")
        @Expose
        public String fxDate;

        /**
         * 日出时间，在高纬度地区可能为空
         */
        @SerializedName("sunrise")
        @Expose
        public String sunrise;

        /**
         * 日落时间，在高纬度地区可能为空
         */
        @SerializedName("sunset")
        @Expose
        public String sunset;

        /**
         * 当天月升时间，可能为空
         */
        @SerializedName("moonrise")
        @Expose
        public String moonrise;

        /**
         * 当天月落时间，可能为空
         */
        @SerializedName("moonset")
        @Expose
        public String moonset;

        /**
         * 月相名称
         */
        @SerializedName("moonPhase")
        @Expose
        public String moonPhase;

        /**
         * 月相图标代码
         */
        @SerializedName("moonPhaseIcon")
        @Expose
        public String moonPhaseIcon;

        /**
         * 预报当天最高温度
         */
        @SerializedName("tempMax")
        @Expose
        public String tempMax;

        /**
         * 预报当天最低温度
         */
        @SerializedName("tempMin")
        @Expose
        public String tempMin;

        /**
         * 预报白天天气状况的图标代码
         */
        @SerializedName("iconDay")
        @Expose
        public String iconDay;

        /**
         * 预报白天天气状况文字描述，包括阴晴雨雪等天气状态的描述
         */
        @SerializedName("textDay")
        @Expose
        public String textDay;

        /**
         * 预报夜间天气状况的图标代码
         */
        @SerializedName("iconNight")
        @Expose
        public String iconNight;

        /**
         * 预报晚间天气状况文字描述，包括阴晴雨雪等天气状态的描述
         */
        @SerializedName("textNight")
        @Expose
        public String textNight;

        /**
         * 预报白天风向360角度
         */
        @SerializedName("wind360Day")
        @Expose
        public String wind360Day;

        /**
         * 预报白天风向
         */
        @SerializedName("windDirDay")
        @Expose
        public String windDirDay;

        /**
         * 预报白天风力等级
         */
        @SerializedName("windScaleDay")
        @Expose
        public String windScaleDay;

        /**
         * 预报白天风速，公里/小时
         */
        @SerializedName("windSpeedDay")
        @Expose
        public String windSpeedDay;

        /**
         * 预报夜间风向360角度
         */
        @SerializedName("wind360Night")
        @Expose
        public String wind360Night;

        /**
         * 预报夜间当天风向
         */
        @SerializedName("windDirNight")
        @Expose
        public String windDirNight;

        /**
         * 预报夜间风力等级
         */
        @SerializedName("windScaleNight")
        @Expose
        public String windScaleNight;

        /**
         * 预报夜间风速，公里/小时
         */
        @SerializedName("windSpeedNight")
        @Expose
        public String windSpeedNight;

        /**
         * 相对湿度，百分比数值
         */
        @SerializedName("humidity")
        @Expose
        public String humidity;

        /**
         * 预报当天总降水量，默认单位：毫米
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
         * 紫外线强度指数
         */
        @SerializedName("uvIndex")
        @Expose
        public String uvIndex;
    }
}
