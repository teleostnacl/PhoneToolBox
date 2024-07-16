package com.teleostnacl.phonetoolbox;

import static com.teleostnacl.phonetoolbox.lib.util.NotificationTransferUtils.NOTIFICATION_TRANSFER_SERVICE_CLASS;

import android.content.ComponentName;

import com.teleostnacl.common.android.context.BaseApplication;
import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.ToastUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.phonetoolbox.lib.crash.CrashHandler;
import com.teleostnacl.phonetoolbox.lib.screenaction.ScreenActionReceiver;
import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.notepad.service.NotepadService;
import com.teleostnacl.phonetoolbox.weather.service.WeatherService;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;

public class PhoneToolBoxApplication extends BaseApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        RxJavaPlugins.setErrorHandler(throwable -> {
            ToastUtils.makeToast(com.teleostnacl.common.android.R.string.unknown_error);
            Logger.e(throwable);
        });
        CrashHandler.init();

        // 禁用并重新开启通知转发服务, 使服务生效
        PmAmUtils.disableComponent(new ComponentName(this, NOTIFICATION_TRANSFER_SERVICE_CLASS));
        PmAmUtils.enableComponent(new ComponentName(this, NOTIFICATION_TRANSFER_SERVICE_CLASS));

        // 注册屏幕亮起和关闭的广播
        ScreenActionReceiver.registerReceiver();

        // 天气服务 挂靠服务
//        NotificationUtils.addService(new WeatherService());
        // 笔记服务
        NotificationUtils.addService(new NotepadService());
        // 添加完成 更新通知
        NotificationUtils.updateNotification();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

        // 解注册屏幕亮起和关闭的广播
        ScreenActionReceiver.unregisterReceiver();

        // 清空挂靠服务
        NotificationUtils.serviceSet.clear();
    }
}
