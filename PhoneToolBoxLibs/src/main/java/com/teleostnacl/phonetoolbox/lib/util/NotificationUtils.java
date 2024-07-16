package com.teleostnacl.phonetoolbox.lib.util;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.SPUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 处理常住通知的公共类
 */
public class NotificationUtils {

    // region ACTION
    /**
     * 更新常驻通知的Action
     */
    public static final String ACTION_UPDATE_NOTIFICATION = "com.teleostnacl.phonetoolbox.NotificationService.UPDATE_NOTIFICATION";

    /**
     * 启动通知服务的Action
     */
    public static final String NOTIFICATION_SERVICE_ACTION = "com.teleostnacl.phonetoolbox.NotificationService";

    /**
     * 挂靠通知服务的ACTION, 向挂靠服务传递INTENT
     */
    public static final String ACTION_ATTACHMENT_SERVICE = "com.teleostnacl.phonetoolbox.NotificationService.ATTACHMENT_SERVICE";

    /**
     * 挂靠通知服务的服务取出原始Action的key
     * <p>
     * 使用getStringExtra方法获取原始Action
     */
    public static final String ACTION = "action";

    // endregion

    /**
     * 全局的通知ID
     */
    private static final AtomicInteger NOTIFICATION_ID = new AtomicInteger(0x0911);

    /**
     * 通知服务是否正在运行
     */
    public static boolean notificationServiceRunning = false;

    // region SP相关
    /**
     * 通知SP的文件名
     */
    private static final String SP_FILE_NAME = "notification";

    /**
     * 读写SP的SharedPreferences
     */
    private static final SharedPreferences sharedPreferences = SPUtils.getSP(SP_FILE_NAME);
    // endregion
    /**
     * 挂靠在通知服务上的Service
     */
    public static final Set<IService> serviceSet = new HashSet<>();

    /**
     * 添加挂靠的服务
     */
    public static void addService(IService service) {
        serviceSet.add(service);
        // 通知服务已经运行 则调用onCreate()
        if (notificationServiceRunning) {
            service.onCreate();
        }
    }

    /**
     * 移除挂靠的服务
     */
    public static void removeService(IService service) {
        serviceSet.remove(service);
    }

    /**
     * 运行通知服务
     */
    public static void runNotificationService() {
        if (!notificationServiceRunning) {
            Intent intent = new Intent(NOTIFICATION_SERVICE_ACTION);
            intent.setPackage(PmAmUtils.getPackageName());
            ContextUtils.getContext().startForegroundService(intent);
        }
    }

    /**
     * 更新常驻通知
     */
    public static void updateNotification() {
        // 创建更新通知的Intent
        Intent intent = new Intent(ACTION_UPDATE_NOTIFICATION);
        intent.setPackage(PmAmUtils.getPackageName());
        // 启动服务
        ContextUtils.getContext().startForegroundService(intent);
    }

    /**
     * 生成传递挂靠服务的Intent, 携带Action
     * <p>
     * 此Intent发送给通知服务之后, 将传递给挂靠服务
     *
     * @param action 原始的Action, 将会被包装, 可使用getStringExtra方法获取原始Action
     */
    @NonNull
    public static Intent getAttachmentServiceActionIntent(String action) {
        Intent intent = new Intent(ACTION_ATTACHMENT_SERVICE);
        intent.setPackage(PmAmUtils.getPackageName());
        intent.putExtra(ACTION, action);
        return intent;
    }

    /**
     * 生成传递挂靠服务的Pending Intent, 携带Action
     * <p>
     * 此Intent发送给通知服务之后, 将传递给挂靠服务
     *
     * @param action 原始的Action, 将会被包装, 可使用getStringExtra方法获取原始Action
     */
    @NonNull
    public static PendingIntent getAttachmentServiceActionPendingIntent(String action) {
        return PendingIntent.getService(ContextUtils.getContext(), PendingIntentUtils.getRequestCode(),
                NotificationUtils.getAttachmentServiceActionIntent(action),
                FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);
    }

    /**
     * 获取一个新的通知ID
     */
    public static synchronized int getNotificationId() {
        return NOTIFICATION_ID.incrementAndGet();
    }

    /**
     * 挂靠在通知服务上的的接口
     * <p>
     * 实现由NotificationService统一管理其它Service的生命周期
     * <p>
     * 减少内存的占用
     */
    public interface IService {
        void onCreate();

        void onDestroy();

        void onStartCommand(@NonNull Intent intent);

        /**
         * 需要在通知栏添加按钮
         */
        default List<NotificationCompat.Action> getActions() {
            return null;
        }
    }
}
