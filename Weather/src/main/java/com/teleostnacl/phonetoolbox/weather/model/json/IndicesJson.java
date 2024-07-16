package com.teleostnacl.phonetoolbox.weather.model.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 生活指数的json
 */
public class IndicesJson {
    @SerializedName("daily")
    @Expose
    public List<Daily> daily;

    public static class Daily {

        /**
         * 预报日期
         */
        @SerializedName("date")
        @Expose
        public String date;

        /**
         * 生活指数类型ID
         */
        @SerializedName("type")
        @Expose
        public String type;

        /**
         * 生活指数类型的名称
         */
        @SerializedName("name")
        @Expose
        public String name;

        /**
         * 生活指数预报等级
         */
        @SerializedName("level")
        @Expose
        public String level;

        /**
         * 生活指数预报级别名称
         */
        @SerializedName("category")
        @Expose
        public String category;

        /**
         * 生活指数预报的详细描述，可能为空
         */
        @SerializedName("text")
        @Expose
        public String text;

    }
}
