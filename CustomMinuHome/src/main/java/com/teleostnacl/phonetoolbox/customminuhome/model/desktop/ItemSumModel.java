package com.teleostnacl.phonetoolbox.customminuhome.model.desktop;

import android.text.Html;
import android.text.Spanned;

import com.teleostnacl.common.android.context.ResourcesUtils;
import com.teleostnacl.phonetoolbox.customminuhome.R;

/**
 * 记录桌面元素总数的Model
 */
public class ItemSumModel {
    //元素总数
    public int itemSum = 0;
    //普通元素的个数
    public int commonItemSum = 0;
    //文件夹元素的个数
    public int folderItemSum = 0;
    //用户小部件的个数
    public int widgetItemSum = 0;
    //系统小部件的个数
    public int systemWidgetSum = 0;
    //shortcut的个数
    public int shortcutItemSum = 0;

    /**
     * 清空数据 重新记录
     */
    public void clear() {
        itemSum = commonItemSum = folderItemSum =
                widgetItemSum = systemWidgetSum = shortcutItemSum = 0;
    }

    //region DataBinding
    public Spanned getItemSum() {
        return Html.fromHtml(ResourcesUtils.getString(R.string.main_fragment_desktop_item, itemSum), Html.FROM_HTML_MODE_COMPACT);
    }

    public Spanned getCommonItemSum() {
        return Html.fromHtml(ResourcesUtils.getString(R.string.main_fragment_application_item, commonItemSum), Html.FROM_HTML_MODE_COMPACT);
    }

    public Spanned getFolderItemSum() {
        return Html.fromHtml(ResourcesUtils.getString(R.string.main_fragment_folder_item, folderItemSum), Html.FROM_HTML_MODE_COMPACT);
    }

    public Spanned getWidgetItemSum() {
        return Html.fromHtml(ResourcesUtils.getString(R.string.main_fragment_widget_item, widgetItemSum), Html.FROM_HTML_MODE_COMPACT);
    }

    public Spanned getSystemWidgetSum() {
        return Html.fromHtml(ResourcesUtils.getString(R.string.main_fragment_system_widget_item, systemWidgetSum), Html.FROM_HTML_MODE_COMPACT);
    }

    public Spanned getShortcutItemSum() {
        return Html.fromHtml(ResourcesUtils.getString(R.string.main_fragment_shortcut_item, shortcutItemSum), Html.FROM_HTML_MODE_COMPACT);
    }
    //endregion


    @Override
    public String toString() {
        return "ItemSumModel{" +
                "itemSum=" + itemSum +
                ", commonItemSum=" + commonItemSum +
                ", folderItemSum=" + folderItemSum +
                ", widgetItemSum=" + widgetItemSum +
                ", systemWidgetSum=" + systemWidgetSum +
                ", shortcutItemSum=" + shortcutItemSum +
                '}';
    }
}
