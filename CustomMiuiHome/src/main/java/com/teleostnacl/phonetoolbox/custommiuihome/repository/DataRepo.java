package com.teleostnacl.phonetoolbox.custommiuihome.repository;

import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.COMMON_CONTAINER;
import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.COMMON_ITEM_TYPE;
import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.FOLDER_ITEM_TYPE;
import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.SHORTCUT_ITEM_TYPE;
import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.SYSTEM_WIDGET_ITEM_TYPE;
import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.TOGGLE_SHURTCUT_ITEM_TYPE;
import static com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel.WIDGET_ITEM_TYPE;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.ResourcesUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.phonetoolbox.custommiuihome.R;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.DesktopModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.PageModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.CommonItemTypeModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FolderItemTypeModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.ShortCutItemTypeModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.SystemWidgetItemTypeModel;
import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.WidgetItemTypeModel;
import com.teleostnacl.phonetoolbox.custommiuihome.util.Const;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Observable;

/**
 * 读取桌面数据的Repo
 */
public class DataRepo {

    private static final String TAG = "DataRepo";

    /**
     * MIUI桌面 SP的文件路径 在备份文件夹中
     */
    private static final String SHARED_PREFERENCE = "apps" + File.separator + Const.MIUI_LAUNCHER_PACKAGE
            + File.separator + "d_sp" + File.separator + "launcher_sharedpreference.xml";

    /**
     * MIUI桌面 数据库文件夹在备份文件夹中的路径
     */
    private static final String DATABASE_FOLDER = "apps" + File.separator + Const.MIUI_LAUNCHER_PACKAGE
            + File.separator + "d_db" + File.separator;

    /**
     * 在SP中记录是否为桌面模式的KEY
     */
    private static final String KEY_LAUNCHER_MODE = "is_all_apps_drawer_mode_enable";

    /**
     * 在SP中记录多少行的KEY
     */
    private static final String KEY_CELL_X = "pref_key_cell_x";

    /**
     * 在SP中记录多少列的KEY
     */
    private static final String KEY_CELL_Y = "pref_key_cell_y";

    /**
     * 读取桌面数据
     *
     * @param filePath     存放所需读取桌面数据的文件路径
     * @param desktopModel 记录桌面数据的模型
     * @return 含有是否读取成功的Observable
     */
    public boolean readLauncherData(String filePath, DesktopModel desktopModel) {
        Logger.v(TAG, "readLauncherData() filePath = " + filePath);
        try ( //读取配置文件
              BufferedReader preferenceReader = new BufferedReader(
                      new FileReader(new File(filePath, SHARED_PREFERENCE)))) {
            StringBuilder preferenceStringBuilder = new StringBuilder();
            String lines;
            while ((lines = preferenceReader.readLine()) != null) {
                preferenceStringBuilder.append(lines);
            }
            //获取配置
            if (!readPreference(preferenceStringBuilder.toString(), desktopModel)) {
                Logger.v(TAG, "readLauncherData() readPreference failed!");
                return false;
            }

            // 设置需要读取的数据文件
            desktopModel.databasePath = filePath + File.separator + DATABASE_FOLDER + File.separator +
                    // 根据是否为抽屉模式, 存储数据库的文件是否需要添加drawer路径
                    (desktopModel.drawerMode ? "drawer/" : "")
                    // 根据行列 选择数据库文件
                    + "launcher" + desktopModel.cellX + "x" + desktopModel.cellY + ".db";

            //打开数据库文件
            SQLiteDatabase database = SQLiteDatabase.openDatabase(desktopModel.databasePath,
                    null, SQLiteDatabase.OPEN_READONLY);

            if (!readDatabase(database, desktopModel)) {
                Logger.v(TAG, "readLauncherData() readDatabase failed!");
                return false;
            }

            //未发生错误,则完成读取
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 读取配置文件
     *
     * @param filePath     放置文件的路径
     * @param preference   读取配置文件之后的字符串
     * @param desktopModel 记录桌面数据模型
     */
    private boolean readPreference(String preference, @NonNull DesktopModel desktopModel) {
        Document document = Jsoup.parse(preference);

        //获取是否为抽屉模式
        Element drawerMode = document.getElementsByAttributeValue("name", KEY_LAUNCHER_MODE).first();
        if (drawerMode == null) {
            return false;
        }
        desktopModel.drawerMode = Boolean.parseBoolean(drawerMode.val());

        //获取桌面的行数
        Element keyCellX = document.getElementsByAttributeValue("name", KEY_CELL_X).first();
        if (keyCellX == null) {
            return false;
        }
        desktopModel.cellX = Integer.parseInt(keyCellX.val());

        //获取桌面的列数
        Element keyCellY = document.getElementsByAttributeValue("name", KEY_CELL_Y).first();
        if (keyCellY == null) {
            return false;
        }
        desktopModel.cellY = Integer.parseInt(keyCellY.val());

        return true;
    }

    /**
     * 读取数据库
     *
     * @param database     所读取的数据库
     * @param desktopModel 记录桌面数据模型
     * @return 如果数据库不符合预期, 则会返回false, 否则正常返回true
     */
    private boolean readDatabase(@NonNull SQLiteDatabase database, DesktopModel desktopModel) {
        if (!checkTable(database)) {
            database.close();
            return false;
        }

        //获取页数的信息
        getScreen(database, desktopModel);

        //获取各页数所含有的元素
        getItem(database, desktopModel);

        database.close();

        return true;
    }

    /**
     * 检查数据库的结构是否符合预期
     *
     * @param database 所读取的数据库
     * @return 数据库结构是否符合预期
     */
    private boolean checkTable(@NonNull SQLiteDatabase database) {
        boolean flag = false;

        //检查是否有数据库中的所有表中,是否含有favorites表和screens表
        Cursor tableCursor = database.query(true, "sqlite_master", null,
                null, null, null, null, null, null);
        int nameIndex = tableCursor.getColumnIndex("tbl_name");
        int sqlIndex = tableCursor.getColumnIndex("sql");
        List<Boolean> nameFlag = new ArrayList<>();
        if (nameIndex >= 0 && sqlIndex >= 0) {
            tableCursor.moveToNext();
            do {

                if (tableCursor.getString(nameIndex).equals("screens")) {

                    String sql = tableCursor.getString(sqlIndex);
                    if (sql.contains("_id") && sql.contains("screenOrder")) {

                        nameFlag.add(true);

                        if (nameFlag.size() == 2) {
                            flag = true;
                            break;
                        }
                    }
                } else if (tableCursor.getString(nameIndex).equals("favorites")) {
                    String sql = tableCursor.getString(sqlIndex);
                    if (sql.contains("_id") && sql.contains("title") && sql.contains("screen") &&
                            sql.contains("cellX") && sql.contains("cellY") && sql.contains("spanX") &&
                            sql.contains("spanY") && sql.contains("iconPackage") && sql.contains("icon") &&
                            sql.contains("profileId") && sql.contains("itemType") &&
                            sql.contains("appWidgetProvider") && sql.contains("container")) {
                        nameFlag.add(true);
                        if (nameFlag.size() == 2) {
                            flag = true;
                            break;
                        }
                    }
                }

            } while (tableCursor.moveToNext());
        }

        tableCursor.close();

        return flag;
    }

    /**
     * 获取屏幕页数与_id间的关系
     *
     * @param database     所读取的数据库
     * @param desktopModel 记录桌面数据的模型
     */
    private void getScreen(@NonNull SQLiteDatabase database, @NonNull DesktopModel desktopModel) {

        Cursor screensCursor = database.query(true, "screens", null,
                null, null, null, null, "screenOrder", null);

        int _idIndex = screensCursor.getColumnIndex("_id");

        screensCursor.moveToNext();
        do {
            desktopModel.screenIdList.add(screensCursor.getInt(_idIndex));
        } while (screensCursor.moveToNext());

        screensCursor.close();
    }

    /**
     * 获取各页数所含有的元素
     *
     * @param database     所读取的数据库
     * @param desktopModel 记录桌面数据的模型
     */
    private void getItem(@NonNull SQLiteDatabase database, @NonNull DesktopModel desktopModel) {
        Cursor itemCursor = database.query(true, "favorites", null,
                null, null, null, null, null, null);

        // 获取数据库的各值索引
        int _idIndex = itemCursor.getColumnIndex("_id");
        int titleIndex = itemCursor.getColumnIndex("title");
        int screenIndex = itemCursor.getColumnIndex("screen");
        int cellXIndex = itemCursor.getColumnIndex("cellX");
        int cellYIndex = itemCursor.getColumnIndex("cellY");
        int spanXIndex = itemCursor.getColumnIndex("spanX");
        int spanYIndex = itemCursor.getColumnIndex("spanY");
        int containerIndex = itemCursor.getColumnIndex("container");
        int iconPackageIndex = itemCursor.getColumnIndex("iconPackage");
        int iconIndex = itemCursor.getColumnIndex("icon");
        int profileIdIndex = itemCursor.getColumnIndex("profileId");
        int itemTypeIndex = itemCursor.getColumnIndex("itemType");
        int appWidgetProviderIndex = itemCursor.getColumnIndex("appWidgetProvider");

        itemCursor.moveToNext();

        do {
            // 获取元素所在页的_id
            int screen = itemCursor.getInt(screenIndex);

            // 获取_id所对应的页model
            PageModel pageModel = desktopModel.screenMap.get(screen);
            //为空时先创建
            if (pageModel == null) desktopModel.screenMap.put(screen, pageModel = new PageModel());

            // 根据类型, 创建不同的对象, 并放置不同的位置
            FavoriteModel item = null;
            switch (itemCursor.getInt(itemTypeIndex)) {
                // 普通元素类型
                case COMMON_ITEM_TYPE: {
                    item = new CommonItemTypeModel();

                    //跳过名称为空的普通元素
                    if (itemCursor.getString(titleIndex) == null) continue;

                    pageModel.commonItemTypeModels.add((CommonItemTypeModel) item);

                    // 统计+1
                    desktopModel.itemSumModel.commonItemSum++;
                }
                break;

                // 文件夹类型
                case FOLDER_ITEM_TYPE: {
                    item = new FolderItemTypeModel();

                    pageModel.folderItemTypeModels.add((FolderItemTypeModel) item);

                    // 统计+1
                    desktopModel.itemSumModel.folderItemSum++;
                }
                break;

                // 小部件类型
                case WIDGET_ITEM_TYPE: {
                    item = new WidgetItemTypeModel();

                    // 获取组建所对应的包名
                    ((WidgetItemTypeModel) item).appWidgetProvider =
                            itemCursor.getString(appWidgetProviderIndex);

                    pageModel.widgetItemTypeModels.add((WidgetItemTypeModel) item);

                    // 统计+1
                    desktopModel.itemSumModel.widgetItemSum++;
                }
                break;

                // 系统小部件类型
                case SYSTEM_WIDGET_ITEM_TYPE:
                case TOGGLE_SHURTCUT_ITEM_TYPE: {
                    item = new SystemWidgetItemTypeModel();

                    pageModel.systemWidgetItemTypeModels.add((SystemWidgetItemTypeModel) item);

                    // 统计+1
                    desktopModel.itemSumModel.systemWidgetSum++;
                }
                break;

                // Shortcut类型
                case SHORTCUT_ITEM_TYPE: {
                    item = new ShortCutItemTypeModel();

                    pageModel.shortCutItemTypeModels.add((ShortCutItemTypeModel) item);

                    // 统计+1
                    desktopModel.itemSumModel.shortcutItemSum++;
                }
                break;
            }

            if (item != null) {
                item._id = itemCursor.getInt(_idIndex);
                item.title = itemCursor.getString(titleIndex);
                item.screen = screen;
                item.cellX = itemCursor.getInt(cellXIndex);
                item.cellY = itemCursor.getInt(cellYIndex);
                item.spanX = itemCursor.getInt(spanXIndex);
                item.spanY = itemCursor.getInt(spanYIndex);
                item.iconPackage = itemCursor.getString(iconPackageIndex);
                item.container = itemCursor.getInt(containerIndex);

                //双开判断的标准为该元素是否非本地用户(0)所有
                item.isDouble = itemCursor.getInt(profileIdIndex) != 0;

                // 获取元素自己提供的图标
                item.iconBytes = itemCursor.getBlob(iconIndex);

                //统计自增1
                desktopModel.itemSumModel.itemSum++;

                // 在文件夹中, 则放进map中
                if (item.container != COMMON_CONTAINER) {
                    List<FavoriteModel> favoriteModels = desktopModel.itemInFolderTypeModels
                            .computeIfAbsent(item.container, k -> new ArrayList<>());

                    favoriteModels.add(item);
                }
            }

        } while (itemCursor.moveToNext());

        itemCursor.close();
    }


    /**
     * 获取所有应用的图标
     *
     * @param desktopModel 记录桌面数据的model
     * @return 含有完成状态的Observable
     */
    public static Observable<Integer> getIcons(@NonNull DesktopModel desktopModel) {
        return Observable.create(emitter -> {
            int i = 0;
            //遍历所有元素
            for (PageModel pageModel : desktopModel.screenMap.values()) {
                List<FavoriteModel> tmp = new ArrayList<>();
                tmp.addAll(pageModel.commonItemTypeModels);
                tmp.addAll(pageModel.folderItemTypeModels);
                tmp.addAll(pageModel.shortCutItemTypeModels);
                tmp.addAll(pageModel.systemWidgetItemTypeModels);
                tmp.addAll(pageModel.widgetItemTypeModels);
                for (FavoriteModel item : tmp) {
                    //该元素有自己的图标
                    if (item.iconBytes != null && item.iconBytes.length != 0) {
                        item.icon = new BitmapDrawable(
                                ResourcesUtils.getResources(),
                                BitmapFactory.decodeByteArray(item.iconBytes, 0, item.iconBytes.length));
                    }

                    //获取应用图标
                    else if (!TextUtils.isEmpty(item.iconPackage)) {
                        try {
                            PackageInfo packageInfo = PmAmUtils.getPm().
                                    getPackageInfo(item.iconPackage, PackageManager.MATCH_UNINSTALLED_PACKAGES);

                            item.icon = packageInfo.applicationInfo.loadIcon(PmAmUtils.getPm());
                        } catch (PackageManager.NameNotFoundException e) {
                            e.printStackTrace();

                            item.icon = getDefaultApplicationIcon();
                        }
                    }

                    //根据小组件的package获取应用图标
                    else if (item instanceof WidgetItemTypeModel &&
                            !TextUtils.isEmpty(((WidgetItemTypeModel) item).appWidgetProvider)) {
                        try {
                            PackageInfo packageInfo = PmAmUtils.getPm().getPackageInfo(
                                    ((WidgetItemTypeModel) item).appWidgetProvider
                                            .split("/")[0], 0);
                            item.icon = packageInfo.applicationInfo.loadIcon(PmAmUtils.getPm());
                        } catch (PackageManager.NameNotFoundException e) {
                            item.icon = getDefaultApplicationIcon();
                        }
                    } else item.icon = getDefaultApplicationIcon();

                    Log.i("TAG", "item.title = " + item.title);
                    Log.i("TAG", "item.icon == null : " + (item.icon == null));

                    //发送事件, 用于统计已获取图标的元素个数
                    emitter.onNext(++i);
                }
            }

            emitter.onComplete();
        });
    }

    /**
     * @return 默认的应用图标
     */
    private static Drawable getDefaultApplicationIcon() {
        return ContextCompat.getDrawable(ContextUtils.getContext(), R.drawable.custom_miui_home_default_application_icon);
    }

}
