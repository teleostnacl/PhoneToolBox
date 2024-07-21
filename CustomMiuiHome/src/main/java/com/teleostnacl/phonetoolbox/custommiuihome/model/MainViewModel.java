package com.teleostnacl.phonetoolbox.custommiuihome.model;

import android.app.Application;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.phonetoolbox.custommiuihome.model.modify.ModifyModel;
import com.teleostnacl.phonetoolbox.custommiuihome.repository.BackupRepo;
import com.teleostnacl.phonetoolbox.custommiuihome.repository.DataRepo;
import com.teleostnacl.phonetoolbox.custommiuihome.repository.ModifyRepo;

import java.io.File;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    /**
     * 备份app的包名
     */
    private static final String BACKUP_APP_PACKAGE = "com.miui.backup";

    /**
     * 备份app的Activity
     */
    private static final String BACKUP_APP_ACTIVITY = "com.miui.backup.local.LocalHomeActivity";

    public final SummaryModel summaryModel = new SummaryModel();

    /**
     * 处理备份文件的Repo类
     */
    private final BackupRepo backupRepo = new BackupRepo();

    /**
     * 整理的Repo类
     */
    private final ModifyRepo modifyRepo = new ModifyRepo();

    /**
     * 读取桌面数据得Repo类
     */
    private final DataRepo dataRepo = new DataRepo();

    /**
     * 记录正在处理的文件夹路径
     */
    private String handlerPath;

    public MainViewModel(@NonNull Application application) {
        super(application);
    }

    /**
     * 打开备份的App
     */
    public void openBackupApp() {
        ContextUtils.startActivity(new Intent().setClassName(BACKUP_APP_PACKAGE, BACKUP_APP_ACTIVITY));
    }

    /**
     * 检查选择的备份文件是否正确 并解压
     *
     * @param uri 打开文件所呈现的uri
     * @return 是否成功
     */
    public Single<Boolean> checkBackupFileCorrect(Uri uri) {
        return Single.just(new Object()).observeOn(Schedulers.io()).map(o -> {
                    handlerPath = backupRepo.checkBackupFileCorrect(uri);
                    return handlerPath != null;
                })
                .onErrorReturnItem(false)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 整理
     */
    public Single<Boolean> modify() {
        return Single.just(true).observeOn(Schedulers.io())
                // 读取当前桌面的数据
                .map(aBoolean -> dataRepo.readLauncherData(handlerPath, summaryModel.desktopModel))
                // 整理
                .map(aBoolean -> {
                    if (!aBoolean) {
                        return false;
                    }

                    Logger.v(TAG, "modify() summaryModel.desktopModel = " + summaryModel.desktopModel.toString());

                    ModifyModel modifyModel = ModifyRepo.sortByNameOnlyCommonAndShortcut(
                            summaryModel.desktopModel.screenIdList.subList(
                                    1, summaryModel.desktopModel.screenIdList.size()),
                            summaryModel.desktopModel, true);

                    ModifyRepo.removeOrAddPage(modifyModel, summaryModel.desktopModel);

                    return true;
                })
                .onErrorReturnItem(false)
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 保存
     */
    public Single<Boolean> save() {
        return Single.just(true).observeOn(Schedulers.io())
                .map(aBoolean -> backupRepo.recompressBackFile(new File(handlerPath, "apps")));
    }
}
