package com.teleostnacl.phonetoolbox.custommiuihome.model;

import static com.teleostnacl.phonetoolbox.custommiuihome.util.Const.MIUI_LAUNCHER_PACKAGE;

import android.content.pm.PackageInfo;
import android.os.Build;

import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.ResourcesUtils;
import com.teleostnacl.common.android.utils.SystemPropertiesUtils;
import com.teleostnacl.phonetoolbox.custommiuihome.R;

import java.util.Locale;

/**
 * 设备相关的信息
 */
public class SystemModel {

    /**
     * 设备型号
     */
    public final String device = String.format(Locale.CHINA, "%s %s", Build.BRAND, Build.MODEL);

    /**
     * Android版本信息
     */
    public final String androidVersion = String.format(Locale.CHINA, "%s (API %s)",
            Build.VERSION.RELEASE, Build.VERSION.SDK_INT);

    /**
     * ROM版本信息
     */
    public final String systemVersion = SystemPropertiesUtils.getProp(SystemPropertiesUtils.VERSION_INCREMENTAL);

    /**
     * miui桌面版本信息
     */
    public final String miuiLauncherVersion;

    public SystemModel() {
        // 获取miui桌面版本号
        String tmp;
        try {
            PackageInfo packageInfo = PmAmUtils.getPm().getPackageInfo(MIUI_LAUNCHER_PACKAGE, 0);
            tmp = String.format(Locale.CHINA, "%s\n(%s)",
                    packageInfo.versionName, packageInfo.getLongVersionCode());
        } catch (Exception e) {
            tmp = ResourcesUtils.getString(R.string.main_fragment_system_and_software_info_miui_home_none);
        }

        miuiLauncherVersion = tmp;
    }
}
