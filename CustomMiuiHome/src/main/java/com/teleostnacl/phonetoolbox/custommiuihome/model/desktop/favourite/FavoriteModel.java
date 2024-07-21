package com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite;

import android.graphics.drawable.Drawable;
import android.icu.text.Collator;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Locale;

public abstract class FavoriteModel implements Comparable<FavoriteModel> {

    //元素类型: 普通元素
    public final static int COMMON_ITEM_TYPE = 0;
    //元素类型: 文件夹元素
    public final static int FOLDER_ITEM_TYPE = 2;
    //元素类型: 小部件
    public final static int WIDGET_ITEM_TYPE = 4;
    //元素类型: 系统的小部件
    public final static int SYSTEM_WIDGET_ITEM_TYPE = 5;
    //元素类型: 手机管家的快捷开关TOGGLE_SHURTCUT, 归为系统小部件
    public final static int TOGGLE_SHURTCUT_ITEM_TYPE = 1;
    //元素类型: Shortcut
    public final static int SHORTCUT_ITEM_TYPE = 14;

    //container类型 普通元素
    public final static int COMMON_CONTAINER = -100;

    //在数据中的id值
    public int _id;
    //显示的名称
    public String title;

    /**
     * 当该值为-100时, 其表示一个普通元素.
     * 当该值为另一个元素的_id值时, 表示该元素在_id=该值的文件夹里面
     */
    public int container;

    //所在的page
    public int screen;
    //所在的行(从上往下)
    public int cellX;
    //所在的列(从左往右)
    public int cellY;
    //所占的行数
    public int spanX;
    //所占的列数
    public int spanY;

    //元素所使用的图标的应用的包名
    public String iconPackage;
    //元素自己提供的图标
    public byte [] iconBytes;

    //元素应显示的图标
    public Drawable icon;

    //是否为双开应用
    public boolean isDouble;

    //元素的类型(普通元素, 文件夹, 小部件, 系统小部件, 其他未知元素)
    public int itemType;

    @Override
    public int compareTo(@NonNull FavoriteModel o) {
        Collator instance = Collator.getInstance(Locale.CHINA);
        int i = instance.compare(title, o.title);

        // 名称相同
        if(i == 0) {
            // 为双开应用, 比较对象不是双开应用, 双开排后面
            if(isDouble && !o.isDouble) return 1;
            else if(!isDouble && o.isDouble) return -1;
            // 都为双开, 或者都不为双开, 则按原来的顺序, 即后来者在后面
            else {
                return 1;
            }
        }
        else return i;
    }

    @Override
    public String toString() {
        return "FavoriteModel{" +
                "_id=" + _id +
                ", title='" + title + '\'' +
                ", container=" + container +
                ", screen=" + screen +
                ", cellX=" + cellX +
                ", cellY=" + cellY +
                ", spanX=" + spanX +
                ", spanY=" + spanY +
                ", iconPackage='" + iconPackage + '\'' +
                ", iconBytes=" + Arrays.toString(iconBytes) +
                ", icon=" + icon +
                ", isDouble=" + isDouble +
                ", itemType=" + itemType +
                '}';
    }
}
