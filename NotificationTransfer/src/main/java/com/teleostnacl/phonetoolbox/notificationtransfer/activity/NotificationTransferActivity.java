package com.teleostnacl.phonetoolbox.notificationtransfer.activity;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationManagerCompat;
import androidx.databinding.DataBindingUtil;

import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.ToastUtils;
import com.teleostnacl.phonetoolbox.lib.activity.BaseActivity;
import com.teleostnacl.phonetoolbox.lib.util.NotificationTransferUtils;
import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.notificationtransfer.R;
import com.teleostnacl.phonetoolbox.notificationtransfer.databinding.ActivityNotificationTransferBinding;

/**
 * 通知转发的配置Activity
 */
public class NotificationTransferActivity extends BaseActivity {
    private static final String TAG = "NotificationTransferActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityNotificationTransferBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_notification_transfer);

        setSupportActionBar(binding.toolbar);

        // 配置通知转发总开关切换事件
        binding.notificationTransferMainSwitch.setChecked(NotificationTransferUtils.getNotificationTransferMainSwitch());
        binding.notificationTransferMainSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NotificationTransferUtils.setNotificationTransferMainSwitchFromSP(isChecked);
            NotificationUtils.updateNotification();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        // 未授予通知使用权, 启动设置进行授予使用权
        if (!notificationListenerEnable()) {
            ToastUtils.makeLongToast(R.string.notification_transfer_need_notification_permission, PmAmUtils.getApplicationLabel());
            gotoNotificationAccessSetting();
            // 结束Activity
            finish();
        }
    }

    /**
     * 判断是否有通知使用权的权限
     *
     * @return 是否有通知使用权的权限
     */
    private boolean notificationListenerEnable() {
        return NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName());
    }

    /**
     * 启动设置界面 授予通知使用权
     */
    private void gotoNotificationAccessSetting() {
        try {
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            try {
                Intent intent = new Intent();
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.Settings$NotificationAccessSettingsActivity");
                intent.setComponent(cn);
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings");
                startActivity(intent);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}