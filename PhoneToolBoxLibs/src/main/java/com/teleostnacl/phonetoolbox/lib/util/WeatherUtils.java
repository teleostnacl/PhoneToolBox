package com.teleostnacl.phonetoolbox.lib.util;

/**
 * 记录天气信息的工具类
 */
public class WeatherUtils {
    /**
     * 当前位置的天气
     */
    public static Weather localWeather;

    /**
     * 广州的天气
     */
    public static Weather weatherGuangzhou = new Weather("广州番禺", "101280102");

    /**
     * 深圳的天气
     */
    public static Weather weatherShenzhen = new Weather("深圳南山", "101280604");

    /**
     * 记录天气数据
     */
    public static class Weather {

        public Weather(String city, String location) {
            this.city = city;
            this.location = location;
        }

        /**
         * 城市
         */
        public String city;

        /**
         * 坐标
         */
        public String location;

        /**
         * 当前温度
         */
        public String currentTemperature;

        /**
         * 当前体感温度
         */
        public String currentFeelsLike;

        /**
         * 当前天气状态
         */
        public String currentWeatherText;

        /**
         * 穿衣指数
         */
        public String dressText;

        /**
         * 日期1的日期
         */
        public String day1Date;

        /**
         * 日期1最高温度
         */
        public String day1MaxTempe;

        /**
         * 日期1最低温度
         */
        public String day1MinTempe;

        /**
         * 日期1的天气情况
         */
        public String day1WeatherText;

        /**
         * 日期2的日期
         */
        public String day2Date;

        /**
         * 日期2最高温度
         */
        public String day2MaxTempe;

        /**
         * 日期2最低温度
         */
        public String day2MinTempe;

        /**
         * 日期2的天气情况
         */
        public String day2WeatherText;

        /**
         * 日期3的日期
         */
        public String day3Date;

        /**
         * 日期3最高温度
         */
        public String day3MaxTempe;

        /**
         * 日期3最低温度
         */
        public String day3MinTempe;

        /**
         * 日期3的天气情况
         */
        public String day3WeatherText;

        @Override
        public String toString() {
            return "Weather{" +
                    "city='" + city + '\'' +
                    ", location='" + location + '\'' +
                    ", currentTemperature='" + currentTemperature + '\'' +
                    ", currentFeelsLike='" + currentFeelsLike + '\'' +
                    ", currentWeatherText='" + currentWeatherText + '\'' +
                    ", dressText='" + dressText + '\'' +
                    ", day1Date='" + day1Date + '\'' +
                    ", day1MaxTempe='" + day1MaxTempe + '\'' +
                    ", day1MinTempe='" + day1MinTempe + '\'' +
                    ", day1WeatherText='" + day1WeatherText + '\'' +
                    ", day2Date='" + day2Date + '\'' +
                    ", day2MaxTempe='" + day2MaxTempe + '\'' +
                    ", day2MinTempe='" + day2MinTempe + '\'' +
                    ", day2WeatherText='" + day2WeatherText + '\'' +
                    ", day3Date='" + day3Date + '\'' +
                    ", day3MaxTempe='" + day3MaxTempe + '\'' +
                    ", day3MinTempe='" + day3MinTempe + '\'' +
                    ", day3WeatherText='" + day3WeatherText + '\'' +
                    '}';
        }
    }
}
