package com.teleostnacl.phonetoolbox.lib.util.activity;

/**
 * 启动通知转发的配置Activity的工具类
 */
public class NotificationTransferActivityUtil {
    // 广播的ACTION值
    public static final String ACTION_NOTIFICATION_TRANSFER = "com.teleostnacl.phonetoolbox.notificationtransfer.activity.NotificationTransferActivity";

    public static void startNotificationTransfer() {
        ActivityUtil.sendBroadcast(ACTION_NOTIFICATION_TRANSFER);
    }
}
