package com.teleostnacl.phonetoolbox.lib.util;

import android.content.SharedPreferences;

import com.teleostnacl.common.android.context.SPUtils;

/**
 * 通知转发所使用的工具类
 */
public class NotificationTransferUtils {

    // region SP
    /**
     * 通知转发SP的文件名
     */
    private static final String SP_FILE_NAME = "notification_transfer";

    /**
     * 读写SP的SharedPreferences
     */
    private static final SharedPreferences sharedPreferences = SPUtils.getSP(SP_FILE_NAME);

    /**
     * SP key 通知转发总开关的Key
     */
    private static final String KEY_NOTIFICATION_TRANSFER_MAIN_SWITCH = "notification_transfer_main_switch";
    // endregion

    /**
     * 通知转发的包名
     */
    public static final String NOTIFICATION_TRANSFER_SERVICE_CLASS = "com.teleostnacl.phonetoolbox.notificationtransfer.NotificationTransferService";

    /**
     * 记录通知转发总开关
     */
    private static boolean notificationTransferMainSwitch = getNotificationTransferMainSwitchFromSP();

    /**
     * 用于记录通知转发总开关改变次数. 用于对称操作, 比如关闭了在打开, 保证原子性
     */
    private static int notificationTransferMainSwitchChangeCount = 0;

    /**
     * @return 从SP中获取通知转发总开关的状态
     */
    public static boolean getNotificationTransferMainSwitchFromSP() {
        return SPUtils.getBoolean(sharedPreferences, KEY_NOTIFICATION_TRANSFER_MAIN_SWITCH, true);
    }

    /**
     * 向SP设置通知转发总开关的状态
     */
    public static void setNotificationTransferMainSwitchFromSP(boolean open) {
        setNotificationTransferMainSwitch(open);
        SPUtils.putBoolean(sharedPreferences.edit(), KEY_NOTIFICATION_TRANSFER_MAIN_SWITCH, open).apply();
    }

    /**
     * 设置 通知转发总开关 的变量
     */
    public static void setNotificationTransferMainSwitch(boolean open) {
        notificationTransferMainSwitchChangeCount++;
        notificationTransferMainSwitch = open;
    }

    /**
     * 获取 通知转发总开关 的变量
     */
    public static boolean getNotificationTransferMainSwitch() {
        return notificationTransferMainSwitch;
    }

    /**
     * 获取通知转发总开关改变次数的记录
     */
    public static int getNotificationTransferMainSwitchChangeCnt() {
        return notificationTransferMainSwitchChangeCount;
    }

}