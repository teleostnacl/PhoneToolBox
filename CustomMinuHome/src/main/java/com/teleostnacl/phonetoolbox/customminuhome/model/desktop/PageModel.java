package com.teleostnacl.phonetoolbox.customminuhome.model.desktop;

import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.CommonItemTypeModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.FolderItemTypeModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.ShortCutItemTypeModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.SystemWidgetItemTypeModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.WidgetItemTypeModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 每一页的数据模型
 */
public class PageModel {
    // 记录普通元素的List
    public final List<CommonItemTypeModel> commonItemTypeModels = new ArrayList<>();
    // 记录文件夹元素的List
    public final List<FolderItemTypeModel> folderItemTypeModels = new ArrayList<>();
    // 记录Shortcut元素的List
    public final List<ShortCutItemTypeModel> shortCutItemTypeModels = new ArrayList<>();
    // 记录系统小部件元素的List
    public final List<SystemWidgetItemTypeModel> systemWidgetItemTypeModels = new ArrayList<>();
    // 记录小部件元素的List
    public final List<WidgetItemTypeModel> widgetItemTypeModels = new ArrayList<>();


    @Override
    public String toString() {
        return "PageModel{" +
                "commonItemTypeModels=" + commonItemTypeModels +
                ", folderItemTypeModels=" + folderItemTypeModels +
                ", shortCutItemTypeModels=" + shortCutItemTypeModels +
                ", systemWidgetItemTypeModels=" + systemWidgetItemTypeModels +
                ", widgetItemTypeModels=" + widgetItemTypeModels +
                '}';
    }
}
