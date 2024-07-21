package com.teleostnacl.phonetoolbox.customminuhome.repository;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.DesktopModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.PageModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.FavoriteModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.desktop.favourite.FolderItemTypeModel;
import com.teleostnacl.phonetoolbox.customminuhome.model.modify.ModifyModel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.IntStream;

public final class ModifyRepo {

    private static final String TAG = "ModifyRepo";

    public ModifyRepo() {
    }

    /**
     * 对指定页数的所有普通元素和Shortcut元素按照元素名进行排序, 并移除所有其他元素
     */
    @NonNull
    public static ModifyModel sortByNameOnlyCommonAndShortcut(@NonNull List<Integer> page_id, DesktopModel desktopModel,
                                                              boolean tmp) {
        ModifyModel modifyModel = new ModifyModel();

        // 记录所有的元素 并用于排序
        TreeSet<FavoriteModel> treeSet = new TreeSet<>();

        // 普通元素 ShortCut元素
        for (int i : page_id) {
            PageModel pageModel = desktopModel.screenMap.get(i);
            if (pageModel != null) {
                treeSet.addAll(pageModel.commonItemTypeModels);
                treeSet.addAll(pageModel.shortCutItemTypeModels);
                treeSet.addAll(pageModel.widgetItemTypeModels);
                treeSet.addAll(pageModel.systemWidgetItemTypeModels);
            }
        }

        // 获取所有页面的所有文件夹
        List<FolderItemTypeModel> models = new ArrayList<>();
        for (int _id : page_id) {
            PageModel pageModel = desktopModel.screenMap.get(_id);
            if (pageModel != null) {
                List<FolderItemTypeModel> folderItemTypeModels =
                        pageModel.folderItemTypeModels;
                // 移除所有文件夹
                modifyModel.deleteFavoriteModelList.addAll(folderItemTypeModels);

                models.addAll(folderItemTypeModels);
            }
        }

        // 遍历所有文件夹, 拆掉文件夹 将元素添加进待处理的treeSet中
        for (FolderItemTypeModel model : models) {
            List<FavoriteModel> list1 = desktopModel.itemInFolderTypeModels.get(model._id);

            if (list1 != null) {
                treeSet.addAll(list1);
            }
        }

        List<FavoriteModel> list = new ArrayList<>(treeSet);

        return generateModifyModel(page_id, desktopModel, list,
                tmp ? generateForbiddenCoordinates(desktopModel.cellX, new int[]{0, 1}) : null,
                modifyModel);
    }

    /**
     * 生成一个连续的不可占用的范围
     *
     * @param xs 不可占用的范围的横坐标
     * @param ys 不可占用的范围的纵坐标
     * @return 含有连续不可占用范围坐标的List
     */
    @NonNull
    public static List<String> generateForbiddenCoordinates(@NonNull int[] xs, @NonNull int[] ys) {
        List<String> list = new ArrayList<>();

        for (int x : xs) {
            for (int y : ys) {
                list.add(x + " " + y);
            }
        }

        Log.i("ModifyRepo", "generateForbiddenCoordinates: list = " + list);

        return list;
    }

    /**
     * 生成一个给定列不可占用的范围
     *
     * @param xs 不可占用的列
     * @param y  总行数
     * @return 给定列不可占用的范围
     */
    @NonNull
    public static List<String> generateForbiddenCoordinates(@NonNull int[] xs, int y) {
        return generateForbiddenCoordinates(xs, IntStream.rangeClosed(0, y).toArray());
    }


    /**
     * 生成一个给定行不可占用的范围
     *
     * @param x  总列数
     * @param ys 不可占用的行
     * @return 给定行不可占用的范围
     */
    @NonNull
    public static List<String> generateForbiddenCoordinates(int x, @NonNull int[] ys) {
        return generateForbiddenCoordinates(IntStream.range(0, x).toArray(), ys);
    }

    /**
     * 生成对数据库修改的信息
     *
     * @param page_id              需要修改页面的_id所组成的list
     * @param desktopModel         桌面的model
     * @param sortedList           已经排好序的list
     * @param forbiddenCoordinates 禁止放置的坐标信息
     * @param modifyModel          记录修改信息的model
     * @return 修改信息的model
     */
    private static ModifyModel generateModifyModel(@NonNull List<Integer> page_id,
                                                   DesktopModel desktopModel,
                                                   List<FavoriteModel> sortedList,
                                                   @Nullable List<String> forbiddenCoordinates,
                                                   ModifyModel modifyModel) {
        // 用于记录当前已经放置的FavoriteModel的记录
        int i = 0;

        // 当前放置的FavoriteModel的记录的坐标
        int x = 0, y = 0;
        for (int _id : page_id) {
            // 当前页坐标信息, 记录是否已有元素 true为已有
            boolean[][] coordinate = new boolean[desktopModel.cellX][desktopModel.cellY];
            while (true) {
                FavoriteModel favoriteModel = sortedList.get(i);
                if (checkAvailable(_id, coordinate, forbiddenCoordinates, x, y, favoriteModel)) {
                    i++;

                    modifyModel.updateFavoriteModelList.add(favoriteModel);

                    // 已经结束了
                    if (i == sortedList.size()) {
                        break;
                    }
                }

                // 移到下一个各自 检查一行是否已满
                if (++x == desktopModel.cellX) {
                    x = 0;

                    // 移到下一行, 检查一页是否已满
                    if (++y == desktopModel.cellY) {
                        y = 0;
                        // 移到下一页
                        break;
                    }
                }

            }

            // 已经结束了
            if (i == sortedList.size()) {
                // 处理需要移除的页面
                int tmp = page_id.indexOf(_id);

                if (tmp != page_id.size() - 1) {
                    modifyModel.removeIdList.addAll(page_id.subList(tmp, page_id.size()));
                }
                break;
            }
        }

        // 还没有全部放进去, 则创建新页面 进行放置
        if (i != sortedList.size()) {
            int lastId = page_id.get(page_id.size() - 1);
            for (int id = 1; ; id++) {
                // 放入新增页面列表
                modifyModel.addIdList.add(lastId + id);
                // 定义坐标
                boolean[][] coordinate = new boolean[desktopModel.cellX][desktopModel.cellY];
                while (true) {
                    FavoriteModel favoriteModel = sortedList.get(i);

                    if (checkAvailable(-id, coordinate, forbiddenCoordinates, x, y, favoriteModel)) {
                        // 下一个元素
                        i++;

                        favoriteModel.screen = lastId + id;

                        modifyModel.updateFavoriteModelList.add(favoriteModel);

                        // 已经结束了
                        if (i == sortedList.size()) {
                            break;
                        }
                    }

                    // 移到下一个格子 检查一行是否已满
                    if (++x == desktopModel.cellX) {
                        x = 0;

                        // 移到下一行, 检查一页是否已满
                        if (++y == desktopModel.cellY) {
                            y = 0;
                            // 移到下一页
                            break;
                        }
                    }
                }

                // 已经结束了
                if (i == sortedList.size()) {
                    break;
                }
            }
        }

        modifyModel.updateFavoriteModelList.sort(new Comparator<FavoriteModel>() {
            @Override
            public int compare(FavoriteModel o1, FavoriteModel o2) {
                int ret = Integer.compare(o1.screen, o2.screen);

                if (ret != 0) {
                    return ret;
                }

                ret = Integer.compare(o1.cellY, o2.cellY);

                if (ret != 0) {
                    return ret;
                }

                return Integer.compare(o1.cellX, o2.cellX);
            }
        });

        return modifyModel;
    }

    /**
     * 检查坐标是否可用
     *
     * @param page_id              页面的id
     * @param coordinate           页面的坐标信息
     * @param forbiddenCoordinates 禁止放置的坐标信息
     * @param x                    所处的横坐标
     * @param y                    所处的纵坐标
     * @param favoriteModel        需要放置的FavoriteModel
     * @return 该处是否可用并更新了model
     */
    private static boolean checkAvailable(
            int page_id, boolean[][] coordinate, @Nullable List<String> forbiddenCoordinates,
            int x, int y, FavoriteModel favoriteModel) {
        // 当前位置未被标记为不可占用, 且未被占用, 则尝试将favoriteModel置于该位置
        Log.i("ModifyRepo", "checkAvailable: x = " + x + ", y = " + y);
        if ((forbiddenCoordinates == null || (
                // 具体页面的具体坐标
                !forbiddenCoordinates.contains(page_id + " " + x + " " + y) &&
                        // 只有坐标
                        !forbiddenCoordinates.contains(x + " " + y)))
                && !coordinate[x][y]) {
            boolean flag = false;

            // 遍历检查是否被占用
            for (int j = 0; j < favoriteModel.spanX; j++) {
                // 循环检查高度
                for (int k = 0; k < favoriteModel.spanY; k++) {
                    flag = coordinate[x + j][y + k];
                    // 被占用时, 跳出循环,
                    if (flag) {
                        break;
                    }
                }
                // 被占用时, 跳出循环,
                if (flag) {
                    break;
                }
            }

            // 未被占用, 则进行赋值并占用
            if (!flag) {
                // 循环赋值
                for (int j = 0; j < favoriteModel.spanX; j++) {
                    for (int k = 0; k < favoriteModel.spanY; k++) {
                        coordinate[x + j][y + k] = true;
                    }
                }

                favoriteModel.cellX = x;
                favoriteModel.cellY = y;

                favoriteModel.screen = page_id;

                // 可用并已更新
                return true;
            }
        }

        return false;
    }

    public static void removeOrAddPage(@NonNull ModifyModel modifyModel, @NonNull DesktopModel desktopModel) {
        // 打开数据库
        try (SQLiteDatabase database = SQLiteDatabase.openDatabase(desktopModel.databasePath,
                null, SQLiteDatabase.OPEN_READWRITE)) {

            database.beginTransaction();

            // 移除无效的页
            for (int i : modifyModel.removeIdList)
                database.execSQL("DELETE FROM screens WHERE _id=?", new Object[]{i});

            // 获取当前最大的页数
            Cursor cursor = database.rawQuery("SELECT MAX(screenOrder) FROM screens",
                    null);
            cursor.moveToNext();
            int lastScreenOrder = cursor.getInt(0) + 1;
            cursor.close();

            // 插入新页
            for (int i = 0; i < modifyModel.addIdList.size(); i++) {
                database.execSQL("INSERT INTO screens VALUES(?, ?, ?, ?)", new Object[]{
                        modifyModel.addIdList.get(i), null, lastScreenOrder + i, 0});
            }

            // 更新FavoriteModel
            for (FavoriteModel model : modifyModel.updateFavoriteModelList) {
                Logger.v(TAG, "removeOrAddPage() updateFavoriteModel = " + model.toString());
                database.execSQL("UPDATE favorites SET container = ?, screen = ?, cellX = ?, cellY = ?" +
                        "WHERE _id = ?", new Object[]{-100, model.screen, model.cellX, model.cellY, model._id});
            }

            // 移除FavoriteModel
            for (FavoriteModel model : modifyModel.deleteFavoriteModelList) {
                database.execSQL("DELETE FROM favorites WHERE _id = ?", new Object[]{model._id});
            }

            database.setTransactionSuccessful();
            database.endTransaction();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
