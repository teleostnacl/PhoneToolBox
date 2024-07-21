package com.teleostnacl.phonetoolbox.custommiuihome.model.modify;

import com.teleostnacl.phonetoolbox.custommiuihome.model.desktop.favourite.FavoriteModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 修改数据库的Model
 */
public class ModifyModel {
    // 需删除的screen的idList
    public List<Integer> removeIdList = new ArrayList<>();

    // 需增加的screen的idList
    public List<Integer> addIdList = new ArrayList<>();

    // 需要更新的FavoriteModel
    public List<FavoriteModel> updateFavoriteModelList = new ArrayList<>();

    // 需要删除的FavoriteModel
    public List<FavoriteModel> deleteFavoriteModelList = new ArrayList<>();

}
