package com.teleostnacl.phonetoolbox.notificationtransfer;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_ONE_SHOT;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.common.java.util.ExecutorServiceUtils;
import com.teleostnacl.phonetoolbox.lib.util.NotificationTransferUtils;
import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.lib.util.PendingIntentUtils;
import com.teleostnacl.phonetoolbox.notificationtransfer.rule.NotificationTransferRule;
import com.teleostnacl.phonetoolbox.notificationtransfer.util.NotificationTransferRuleUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * 通知转发的核心服务
 */
public class NotificationTransferService extends NotificationListenerService implements NotificationUtils.IService {

    private static final String TAG = "NotificationTransferService";

    /**
     * 打开通知转发的Action
     */
    private static final String ACTION_OPEN_NOTIFICATION_TRANSFER = "com.teleostnacl.phonetoolbox.notificationtransfer.action_open_notification_transfer";

    /**
     * 关闭通知转发的Action
     */
    private static final String ACTION_CLOSE_NOTIFICATION_TRANSFER = "com.teleostnacl.phonetoolbox.notificationtransfer.action_close_notification_transfer";

    /**
     * 更新通知的ACTION
     */
    private static final String ACTION_UPDATE_NOTIFICATION = "com.teleostnacl.phonetoolbox.notificationtransfer.action_update_notification";

    /**
     * 延迟更新通知栏的时间
     */
    private static final int DELAY_UPDATE_NOTIFICATION = 10 * 60 * 1000;

    /**
     * 通知转发匹配规则
     * key - 包名
     * List<NotificationTransferRule> - 指定包名下的规则列表
     */
    private final Map<String, TreeSet<NotificationTransferRule>> rules = new HashMap<>();

    /**
     * 状态栏更新中标识符 避免频繁更新 导致anr
     */
    private boolean updatingNotification = false;

    /**
     * 更新通知的PendingIntent
     */
    private static final PendingIntent UPDATE_NOTIFICATION_PENDING_INTENT =
            NotificationUtils.getAttachmentServiceActionPendingIntent(ACTION_UPDATE_NOTIFICATION);

    public NotificationTransferService() {
        // 注册到挂靠服务中
        NotificationUtils.addService(this);
        NotificationUtils.updateNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            switch (intent.getAction()) {
                // 打开通知转发的Action
                case ACTION_OPEN_NOTIFICATION_TRANSFER:
                    // 打开通知转发
                    NotificationTransferUtils.setNotificationTransferMainSwitchFromSP(true);
                    // 更新通知栏
                    updateNotification();
                    break;

                // 关闭通知转发的Action
                case ACTION_CLOSE_NOTIFICATION_TRANSFER:
                    // 关闭通知转发
                    NotificationTransferUtils.setNotificationTransferMainSwitchFromSP(false);
                    // 更新通知栏
                    updateNotification();
                    break;
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // 添加测试规则
        rules.clear();
        for (NotificationTransferRule rule : NotificationTransferRuleUtils.generateRules()) {
            addRule(rule);
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        super.onNotificationPosted(sbn);

        // 获取发出通知的包名
        String packageName = sbn.getPackageName();

        // 自己发出的广播 不处理
        if (getPackageName().equals(packageName)) {
            return;
        }

        // 获取通知的Extra
        Notification notification = sbn.getNotification();
        Bundle extras = notification.extras;
        // 获取通知的标题
        CharSequence title = extras.getCharSequence(Notification.EXTRA_TITLE);
        // 获取通知的内容
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);

        Logger.v(TAG, "onNotificationPosted: packageName = " + packageName + ", title = " + title + ", text = " + text);

        // 通知常驻 广播进行更新(使其置顶) 已经有更新任务 不更新
        if (!updatingNotification) {
            // 启动延迟任务功能更新通知栏
            updatingNotification = true;
            ContextUtils.getAlarmManager().cancel(UPDATE_NOTIFICATION_PENDING_INTENT);
            ContextUtils.getAlarmManager().set(AlarmManager.RTC,
                    System.currentTimeMillis() + DELAY_UPDATE_NOTIFICATION,
                    UPDATE_NOTIFICATION_PENDING_INTENT);
        }

        // 获取转发规则
        TreeSet<NotificationTransferRule> packageRules = rules.get(packageName);
        // 指定包名没有转发规则 则忽略
        if (packageRules == null) {
            return;
        }

        // 遍历规则
        for (NotificationTransferRule rule : packageRules) {
            // 通知转发未打开 同时忽略主开关为false 不进行判断
            if (!NotificationTransferUtils.getNotificationTransferMainSwitch() && !rule.ignoreMainSwitch) {
                continue;
            }

            // 匹配规则
            if (matchNotificationRule(title, text, rule) && rule.action != null) {
                ExecutorServiceUtils.executeByCached(() -> {
                    // 防止出错导致程序崩溃 进行 try - catch
                    try {
                        rule.action.accept(this, sbn);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                });

                // 规则消费后 不再遍历规则
                if (rule.consuming) {
                    break;
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        rules.clear();

        NotificationUtils.removeService(this);
    }

    @Override
    public void onStartCommand(@NonNull Intent intent) {
        // 更新通知的ACTION
        String action = intent.getStringExtra(NotificationUtils.ACTION);
        if (action != null) {
            switch (action) {
                // 更新通知
                case ACTION_UPDATE_NOTIFICATION:
                    updateNotification();
                    break;
            }
        }
    }

    /**
     * 向常驻通知添加按钮
     */
    @Override
    public List<NotificationCompat.Action> getActions() {
//        // 创建通知转发相关的按钮
//        NotificationCompat.Action notificationTransferAction;
//        // 当前通知转发开关为开, 则在常住通知添加关闭的Action
//        if (NotificationTransferUtils.getNotificationTransferMainSwitch()) {
//            notificationTransferAction = new NotificationCompat.Action(null, getString(R.string.notification_transfer_service_notification_transfer_close),
//                    PendingIntent.getService(this, PendingIntentUtils.getRequestCode(),
//                            new Intent(ACTION_CLOSE_NOTIFICATION_TRANSFER), FLAG_ONE_SHOT | FLAG_IMMUTABLE));
//        } else {
//            // 通知转发为关, 则在常驻通知添加打开的Action
//            notificationTransferAction = new NotificationCompat.Action(null, getString(R.string.notification_transfer_service_notification_transfer_open),
//                    PendingIntent.getService(this, PendingIntentUtils.getRequestCode(),
//                            new Intent(ACTION_OPEN_NOTIFICATION_TRANSFER), FLAG_ONE_SHOT | FLAG_IMMUTABLE));
//        }
//        List<NotificationCompat.Action> list = new ArrayList<>();
//        list.add(notificationTransferAction);
//        return list;
        return null;
    }

    private void addRule(@NonNull NotificationTransferRule rule) {
        rules.compute(rule.packageName, (s, notificationTransferRules) -> {
            if (notificationTransferRules == null) {
                notificationTransferRules = new TreeSet<>();
            }
            notificationTransferRules.add(rule);
            return notificationTransferRules;
        });
    }

    /**
     * 判断指定规则是否符合当前的通知消息
     *
     * @param title 通知的标题
     * @param text  通知的内容
     * @param rule  通知匹配规则
     * @return 是否匹配
     */
    private boolean matchNotificationRule(@Nullable CharSequence title, @Nullable CharSequence text, @NonNull NotificationTransferRule rule) {
        boolean titleMatches = true;
        boolean contentMatches = true;

        // 标题规则不为空时, 需要匹配标题
        if (!TextUtils.isEmpty(rule.title)) {
            switch (rule.titleMatchRule) {
                // 包含匹配
                case NotificationTransferRule.CONTAINS_MATCH: {
                    if (title != null) {
                        titleMatches = title.toString().contains(rule.title);
                    } else {
                        titleMatches = false;
                    }
                    break;
                }
                // 正则表达式匹配
                case NotificationTransferRule.REGEX_MATCH:
                    if (title != null) {
                        titleMatches = title.toString().matches(rule.title);
                    } else {
                        titleMatches = false;
                    }
                    break;
                // 全匹配
                default: {
                    if (title != null) {
                        titleMatches = rule.title.contentEquals(title);
                    } else {
                        titleMatches = false;
                    }
                    break;
                }
            }
        }

        // 内容规则不为空时, 需要匹配内容
        if (!TextUtils.isEmpty(rule.content)) {
            switch (rule.contentMatchRule) {
                // 包含匹配
                case NotificationTransferRule.CONTAINS_MATCH: {
                    if (text != null) {
                        contentMatches = text.toString().contains(rule.content);
                    } else {
                        contentMatches = false;
                    }
                    break;
                }
                // 正则表达式匹配
                case NotificationTransferRule.REGEX_MATCH:
                    if (text != null) {
                        contentMatches = text.toString().matches(rule.content);
                    } else {
                        contentMatches = false;
                    }
                    break;
                // 全匹配
                default: {
                    if (text != null) {
                        contentMatches = rule.content.contentEquals(text);
                    } else {
                        contentMatches = false;
                    }
                    break;
                }
            }
        }

        // 规则前置条件是否为true
        boolean ruleCondition = rule.ruleCondition == null || rule.ruleCondition.get();

        Logger.v(TAG, "title = " + rule.title + ", titleMatchRule = " + rule.titleMatchRule +
                ", content = " + rule.content + ", contentMatchRule = " + rule.contentMatchRule +
                ", titleMatches = " + titleMatches + ", contentMatches = " + contentMatches +
                ", ruleCondition = " + ruleCondition);

        return titleMatches && contentMatches && ruleCondition;
    }

    /**
     * 更新通知
     */
    private void updateNotification() {
        ContextUtils.getAlarmManager().cancel(UPDATE_NOTIFICATION_PENDING_INTENT);
        updatingNotification = false;
        NotificationUtils.updateNotification();
    }
}