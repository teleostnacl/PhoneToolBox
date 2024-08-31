package com.teleostnacl.phonetoolbox.lib.util.activity;

public class CustomMiuiHomeActivityUtil {
    // 广播的ACTION值
    public static final String ACTION_CUSTOM_MIUI_HOME = "com.teleostnacl.phonetoolbox.custommiuihome.CustomMiuiHomeActivity";

    public static void startCustomMiuiHome() {
        ActivityUtil.sendBroadcast(ACTION_CUSTOM_MIUI_HOME);
    }
}
