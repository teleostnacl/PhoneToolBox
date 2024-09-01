package com.teleostnacl.phonetoolbox;

import android.os.Bundle;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.phonetoolbox.databinding.ActivityMainBinding;
import com.teleostnacl.phonetoolbox.lib.activity.BaseActivity;
import com.teleostnacl.phonetoolbox.lib.adapter.NeumorphCardViewTextViewIconListAdapter;
import com.teleostnacl.phonetoolbox.lib.model.NeumorphCardViewTextWithIconModel;
import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.lib.util.activity.ActivityUtil;
import com.teleostnacl.phonetoolbox.lib.util.activity.CustomMiuiHomeActivityUtil;
import com.teleostnacl.phonetoolbox.lib.util.activity.NotificationTransferActivityUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 主页面的Activity 用于展示所有功能
 */
public class MainActivity extends BaseActivity {

    /**
     * 记录主页选项的Model
     */
    private final List<NeumorphCardViewTextWithIconModel> models = new ArrayList<>();

    /**
     * 展示主页选项的Adapter
     */
    private final NeumorphCardViewTextViewIconListAdapter listAdapter = new NeumorphCardViewTextViewIconListAdapter();

    static {
        // 静态注册广播
        ContextUtils.registerReceiver(ActivityBroadcastReceiver.class,
                ActivityUtil.getActivityIntentFilter(), ContextCompat.RECEIVER_NOT_EXPORTED);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(PmAmUtils.getApplicationLabel() + " ver." + PmAmUtils.getVersionCode());

        // 配置显示的model
        configModels();
        // 提交list并设置Adapter
        listAdapter.submitList(models);
        binding.recyclerView.setAdapter(listAdapter);

        // 常驻服务未开启, 则启动常驻服务
        NotificationUtils.runNotificationService();
    }

    /**
     * 配置显示的model
     */
    private void configModels() {
        models.clear();

        // 通知转发
        models.add(new NeumorphCardViewTextWithIconModel(getString(com.teleostnacl.phonetoolbox.notificationtransfer.R.string.item_notification_transfer), Objects.requireNonNull(AppCompatResources.getDrawable(this, com.teleostnacl.phonetoolbox.notificationtransfer.R.drawable.ic_notification_transfer)), NotificationTransferActivityUtil::startNotificationTransfer));

    }
}