package com.teleostnacl.phonetoolbox.custommiuihome.model.desktop;

import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 记录桌面数据的model
 */
public class DesktopModel {
    //记录桌面布局的行数
    public int cellX;
    //记录桌面布局的列数
    public int cellY;
    //是否为抽屉模式
    public boolean drawerMode;
    //按顺序记录每页的_id(根据顺序储存id)和每页所含有的数据元素
    public final List<Integer> screenIdList = new ArrayList<>();
    public final Map<Integer, PageModel> screenMap = new HashMap<>();

    // 记录文件夹中的元素
    public final Map<Integer, List<FavoriteModel>> itemInFolderTypeModels = new HashMap<>();

    // 记录页面总数
    public final ItemSumModel itemSumModel = new ItemSumModel();

    //数据库文件的路径
    public String databasePath;

    /**
     * 清空数据, 重新记录
     */
    public void clear() {
        screenIdList.clear();
        screenMap.clear();
        itemInFolderTypeModels.clear();
        itemSumModel.clear();
    }

    @Override
    public String toString() {
        return "DesktopModel{" +
                "cellX=" + cellX +
                ", cellY=" + cellY +
                ", drawerMode=" + drawerMode +
                ", screenIdList=" + screenIdList +
                ", screenMap=" + screenMap +
                ", itemInFolderTypeModels=" + itemInFolderTypeModels +
                ", itemSumModel=" + itemSumModel +
                ", databasePath='" + databasePath + '\'' +
                '}';
    }
}
