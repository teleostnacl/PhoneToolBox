package com.teleostnacl.phonetoolbox.notificationtransfer.rule;

import android.service.notification.StatusBarNotification;

import androidx.annotation.NonNull;
import androidx.core.util.Supplier;

import com.teleostnacl.phonetoolbox.notificationtransfer.NotificationTransferService;

/**
 * 通知转发的规则
 */
public class NotificationTransferRule implements Comparable<NotificationTransferRule> {

    // region 字符串匹配规则
    /**
     * 字符串匹配规则 完全匹配
     */
    public final static int ENTIRE_MATCH = 0;

    /**
     * 字符串匹配规则 包含匹配
     */
    public final static int CONTAINS_MATCH = 1;

    /**
     * 字符串匹配规则 正则表达式匹配
     */
    public final static int REGEX_MATCH = 2;

    // endregion

    /**
     * 规则排序
     */
    public int no;

    /**
     * 通知转发规则 - 包名
     */
    public String packageName;

    /**
     * 通知转发规则 - 标题
     */
    public String title;

    /**
     * 标题匹配规则
     */
    public int titleMatchRule = ENTIRE_MATCH;

    /**
     * 通知转发规则 - 内容
     */
    public String content;

    /**
     * 内容匹配规则
     */
    public int contentMatchRule = ENTIRE_MATCH;

    /**
     * 是否无视通知转发总开关(可在Runnable制定自定义规则)
     */
    public boolean ignoreMainSwitch = false;

    /**
     * 是否完全消费通知, 不再传递给其他规则
     */
    public boolean consuming = true;

    /**
     * 匹配上时的 触发的行为规则
     * 非主线程, 允许做耗时任务
     */
    public Consumer action;

    /**
     * 规则启用条件
     */
    public Supplier<Boolean> ruleCondition;

    @Override
    public int compareTo(@NonNull NotificationTransferRule o) {
        // 匹配排序规则
        return Integer.compare(no, o.no);
    }

    /**
     * 匹配上时的 触发的行为规则
     */
    public interface Consumer {
        void accept(NotificationTransferService service, StatusBarNotification sbn);
    }
}
