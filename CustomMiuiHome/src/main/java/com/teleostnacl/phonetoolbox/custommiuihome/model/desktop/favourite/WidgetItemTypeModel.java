package com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite;

/**
 * 小部件元素
 */
public class WidgetItemTypeModel extends FavoriteModel{

    //当元素类型为小组件时, 该组件所对应的应用包命
    public String appWidgetProvider;

    public WidgetItemTypeModel() {
        itemType = WIDGET_ITEM_TYPE;
    }
}
