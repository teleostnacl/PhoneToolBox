package com.teleostnacl.phonetoolbox.notification.service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;
import static com.teleostnacl.common.android.utils.TimeUtils.DAY;
import static com.teleostnacl.common.android.utils.TimeUtils.getTomorrowStartTime;
import static com.teleostnacl.phonetoolbox.lib.util.NotificationUtils.ACTION_UPDATE_NOTIFICATION;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.ResourcesUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.common.android.service.BaseLogService;
import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.lib.util.PendingIntentUtils;
import com.teleostnacl.phonetoolbox.notification.R;

import java.util.List;

/**
 * 通知常驻服务
 */
public class NotificationService extends BaseLogService {

    private static final String TAG = "NotificationService";

    /**
     * 通知的id
     */
    public static final int NOTIFICATION_ID = 0x0911;

    /**
     * 通知渠道ID
     */
    public static final String NOTIFICATION_CHANNEL_ID = "com.teleostnacl.phonetoolbox.NotificationService";

    /**
     * 通知渠道名称
     */
    private static final String NOTIFICATION_CHANNEL_NAME = ResourcesUtils.getString(R.string.notification_service_title);

    /**
     * 通知管理器
     */
    private NotificationManager notificationManager;

    /**
     * 定时更新的PendingIntent
     */
    private static final PendingIntent UPDATE_NOTIFICATION_PENDING_INTENT =
            PendingIntent.getService(ContextUtils.getContext(), PendingIntentUtils.getRequestCode(),
                    new Intent(ACTION_UPDATE_NOTIFICATION).setPackage(PmAmUtils.getPackageName()),
                    FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE);

    @Override
    public void onCreate() {
        super.onCreate();

        startForeground();

        // 通知展示中
        NotificationUtils.notificationServiceRunning = true;

        // 调用挂靠的服务
        for (NotificationUtils.IService service : NotificationUtils.serviceSet) {
            // 调用onCreate
            try {
                service.onCreate();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        // 每天凌晨定时更新
        ContextUtils.getAlarmManager().cancel(UPDATE_NOTIFICATION_PENDING_INTENT);
        ContextUtils.getAlarmManager().setInexactRepeating(AlarmManager.RTC, getTomorrowStartTime(), DAY, UPDATE_NOTIFICATION_PENDING_INTENT);
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent == null) {
            return super.onStartCommand(null, flags, startId);
        }

        String action = intent.getAction();

        if (action == null) {
            return START_STICKY;
        }

        Logger.v(TAG, "onStartCommand: action = " + action);

        switch (action) {
            // 更新通知
            case ACTION_UPDATE_NOTIFICATION:
                updateNotification();
                break;

            case NotificationUtils.ACTION_ATTACHMENT_SERVICE:
                // 调用挂靠的服务
                for (NotificationUtils.IService service : NotificationUtils.serviceSet) {
                    // 调用onDestroy
                    try {
                        service.onStartCommand(intent);
                    } catch (Throwable th) {
                        th.printStackTrace();
                    }
                }
                break;
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // 调用挂靠的服务
        for (NotificationUtils.IService service : NotificationUtils.serviceSet) {
            // 调用onDestroy
            try {
                service.onDestroy();
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        // 不展示通知
        NotificationUtils.notificationServiceRunning = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onBind(intent);
        return null;
    }

    /**
     * 启动前台服务
     */
    private void startForeground() {
        // 创建通知
        startForeground(NOTIFICATION_ID, createNotification());
    }

    /**
     * 更新通知
     */
    private void updateNotification() {
        Logger.v(TAG, "updateNotification()");
        Notification notification = createNotification();
        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    /**
     * 创建通知
     *
     * @return 新的通知
     */
    @NonNull
    private Notification createNotification() {
        // 创建通知渠道
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setAllowBubbles(false);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        if (notificationManager == null) {
            notificationManager = ContextUtils.getNotificationManager();
        }
        notificationManager.createNotificationChannel(notificationChannel);

        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setSilent(true)
                .setStyle(bigTextStyle)
                .setOnlyAlertOnce(true)
                .setSmallIcon(PmAmUtils.getLauncherIconId())
                .setAutoCancel(false)
                .setOngoing(true);


        builder.setContentTitle(ResourcesUtils.getString(R.string.notification_service_secret_mode_tips));

        // 调用挂靠的服务
        for (NotificationUtils.IService service : NotificationUtils.serviceSet) {
            // 调用getAction()
            try {
                List<NotificationCompat.Action> actions = service.getActions();
                if (actions != null) {
                    for (NotificationCompat.Action action : actions) {
                        builder.addAction(action);
                    }
                }
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }

        return builder.build();
    }
}
