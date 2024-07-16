package com.teleostnacl.phonetoolbox;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
/**
 * 开机自启广播
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // 非开机自启广播 不处理
        if (intent == null || !Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            return;
        }

        // 常驻服务未开启, 则启动常驻服务
        NotificationUtils.runNotificationService();
    }
}
