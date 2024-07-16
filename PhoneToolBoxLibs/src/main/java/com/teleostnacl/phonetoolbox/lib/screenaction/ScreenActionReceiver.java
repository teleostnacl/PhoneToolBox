package com.teleostnacl.phonetoolbox.lib.screenaction;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.core.content.ContextCompat;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.log.Logger;

/**
 * 监听屏幕解锁锁定的广播接收器
 */
public class ScreenActionReceiver extends BroadcastReceiver {
    private static final String TAG = "ScreenActionReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null) {
            return;
        }

        String action = intent.getAction();

        Logger.v(TAG, "onReceive() action = " + action);

        if (Intent.ACTION_USER_PRESENT.equals(action)) {
            ScreenActionUtils.screenAction = true;
            ScreenActionUtils.screenActionPublishSubject.onNext(true);
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
            ScreenActionUtils.screenAction = false;
            ScreenActionUtils.screenActionPublishSubject.onNext(false);
        }
    }

    /**
     * 注册广播
     */
    public static void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        ContextUtils.registerReceiver(ScreenActionReceiver.class, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
    }

    /**
     * 解注册广播
     */
    public static void unregisterReceiver() {
        ContextUtils.unregisterReceiver(ScreenActionReceiver.class);
    }
}
