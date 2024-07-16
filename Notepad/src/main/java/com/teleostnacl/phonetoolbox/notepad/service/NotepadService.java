package com.teleostnacl.phonetoolbox.notepad.service;

import static android.app.PendingIntent.FLAG_IMMUTABLE;
import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.Intent;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.teleostnacl.common.android.context.ContextUtils;
import com.teleostnacl.common.android.context.PmAmUtils;
import com.teleostnacl.common.android.context.ResourcesUtils;
import com.teleostnacl.common.android.log.Logger;
import com.teleostnacl.common.android.utils.HtmlUtils;
import com.teleostnacl.common.android.utils.TimeUtils;
import com.teleostnacl.common.java.util.RandomUtils;
import com.teleostnacl.phonetoolbox.lib.util.NotificationUtils;
import com.teleostnacl.phonetoolbox.lib.util.PendingIntentUtils;
import com.teleostnacl.phonetoolbox.notepad.R;
import com.teleostnacl.phonetoolbox.notepad.model.Note;
import com.teleostnacl.phonetoolbox.notepad.repository.NotepadRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.rxjava3.disposables.CompositeDisposable;

/**
 * 用于创建定时任务, 在指定时间创建一句笔记的通知
 */
public class NotepadService implements NotificationUtils.IService {

    private static final String TAG = "NotepadService";

    /**
     * 通知渠道名称
     */
    private static final String NOTIFICATION_CHANNEL_NAME = ResourcesUtils.getString(R.string.notepad_service_title);

    /**
     * 通知渠道ID
     */
    public static final String NOTIFICATION_CHANNEL_ID = "com.teleostnacl.phonetoolbox.NotepadService";

    /**
     * 复制一言的ACTION
     */
    private static final String ACTION_COPY_NOTE = "com.teleostnacl.phonetoolbox.NotepadService.COPY_NOTE";

    /**
     * 传递需复制的一言的内容的KEY
     */
    private static final String KEY_COPY_NOTE = "copy_note";

    /**
     * 发送一言通知的ACTION
     */
    private static final String ACTION_SEND_NOTE = "com.teleostnacl.phonetoolbox.NotepadService.ACTION_SEND_NOTE";

    /**
     * 更新笔记本的ACTION
     */
    private static final String ACTION_UPDATE_NOTEPAD = "com.teleostnacl.phonetoolbox.NotepadService.ACTION_UPDATE_NOTEPAD";

    /**
     * 定时发送一言通知的ACTION
     */
    private static final String ACTION_SEND_INTERVAL_NOTE = "com.teleostnacl.phonetoolbox.NotepadService.ACTION_SEND_NOTE_INTERVAL";

    /**
     * repository类 用于获取数据
     */
    private NotepadRepository repository;

    /**
     * Disposable
     */
    private CompositeDisposable disposable;

    /**
     * 发送一言通知的PendingIntent
     */
    private static final PendingIntent SEND_NOTE_INTENT =
            NotificationUtils.getAttachmentServiceActionPendingIntent(ACTION_SEND_NOTE);
    /**
     * 更新笔记本的PendingIntent
     */
    private static final PendingIntent UPDATE_NOTEPAD_INTENT =
            NotificationUtils.getAttachmentServiceActionPendingIntent(ACTION_UPDATE_NOTEPAD);

    /**
     * 更新笔记本的PendingIntent
     */
    private static final PendingIntent SEND_NOTE_INTERVAL_INTENT =
            NotificationUtils.getAttachmentServiceActionPendingIntent(ACTION_SEND_INTERVAL_NOTE);

    @Override
    public void onCreate() {
        Logger.v(TAG, "onCreate()");

        disposable = new CompositeDisposable();

        repository = new NotepadRepository();

        // 首次进入 触发更新
        updateNotepad(true);

        createSendNotificationNextHourAlarm();

        // 创建定时任务 每天一点更新
        ContextUtils.getAlarmManager().cancel(UPDATE_NOTEPAD_INTENT);
        ContextUtils.getAlarmManager().setInexactRepeating(AlarmManager.RTC,
                TimeUtils.getTomorrowStartTime() + TimeUtils.HOUR, TimeUtils.DAY,
                UPDATE_NOTEPAD_INTENT);
    }

    @Override
    public void onDestroy() {
        Logger.v(TAG, "onDestroy()");

        if (disposable != null) {
            disposable.dispose();
        }
        disposable = null;
    }

    @Override
    public void onStartCommand(@NonNull Intent intent) {
        Logger.v(TAG, "onStartCommand()");

        String action = intent.getStringExtra(NotificationUtils.ACTION);
        if (action == null) {
            return;
        }

        switch (action) {
            // 复制一言
            case ACTION_COPY_NOTE:
                String note = intent.getStringExtra(KEY_COPY_NOTE);

                if (!TextUtils.isEmpty(note)) {
                    ContextUtils.getClipboardManager().setPrimaryClip(ClipData.newPlainText(ResourcesUtils.getString(R.string.notepad_service_title), note));
                }

                break;

            // 发送一言的通知
            case ACTION_SEND_NOTE:
                sendNote();
                break;

            // 更新笔记本的Action
            case ACTION_UPDATE_NOTEPAD:
                updateNotepad(false);
                break;


            // 定时发送一言通知的Action
            case ACTION_SEND_INTERVAL_NOTE:
                sendNote();
                // 创建生成下一条一言的Alarm
                createSendNotificationNextHourAlarm();
                break;
        }
    }

    @Override
    public List<NotificationCompat.Action> getActions() {
        // 更新一言的Action
        NotificationCompat.Action updateAction = new NotificationCompat.Action(null, ResourcesUtils.getString(R.string.notepad_service_new_note), SEND_NOTE_INTENT);
        List<NotificationCompat.Action> list = new ArrayList<>();
        list.add(updateAction);
        return list;
    }

    /**
     * 更新笔记本
     */
    private void updateNotepad(boolean sendNote) {
        if (repository == null || disposable == null) {
            return;
        }

        Logger.v(TAG, "updateNotepad");

        disposable.add(repository.gitClone()
                .flatMap(o -> repository.handleNotepad())
                .subscribe(o -> {
                    // 更新成功之后 生成第一条一言
                    if (sendNote) {
                        sendNote();
                    }
                }, Throwable::printStackTrace));
    }

    /**
     * 发送笔记通知
     */
    private void sendNote() {
        if (repository == null || disposable == null) {
            return;
        }

        // 生成笔记
        disposable.add(repository.getNote().subscribe(this::sendNoteNotification));
    }

    /**
     * 发送笔记通知
     *
     * @param note 笔记
     */
    private void sendNoteNotification(@NonNull Note note) {
        createNotificationChannel();

        // 获取该通知全局唯一的通知ID
        int notificationId = NotificationUtils.getNotificationId();

        // 创建通知
        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setSummaryText(ResourcesUtils.getString(R.string.notepad_service_title));
        bigTextStyle.bigText(HtmlUtils.fromHtml(note.note));

        // 复制一言的Action
        Intent copyIntent = NotificationUtils.getAttachmentServiceActionIntent(ACTION_COPY_NOTE);
        copyIntent.putExtra(KEY_COPY_NOTE, note.note);
        NotificationCompat.Action copyAction = new NotificationCompat.Action(null, ResourcesUtils.getString(R.string.notepad_service_copy),
                PendingIntent.getService(ContextUtils.getContext(), PendingIntentUtils.getRequestCode(),
                        copyIntent, FLAG_UPDATE_CURRENT | FLAG_IMMUTABLE));

        // 更新一言的Action
        NotificationCompat.Action updateAction = new NotificationCompat.Action(null, ResourcesUtils.getString(R.string.notepad_service_next_note), SEND_NOTE_INTENT);

        Notification notification = new NotificationCompat.Builder(ContextUtils.getContext(), NOTIFICATION_CHANNEL_ID)
                .setSilent(true)
                .setContentTitle(note.from)
                .addAction(copyAction)
                .addAction(updateAction)
                .setStyle(bigTextStyle)
                .setSmallIcon(PmAmUtils.getLauncherIconId())
                .build();

        // 发送通知
        ContextUtils.getNotificationManager().notify(notificationId, notification);
    }

    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        notificationChannel.setAllowBubbles(false);
        notificationChannel.enableLights(false);
        notificationChannel.enableVibration(false);
        ContextUtils.getNotificationManager().createNotificationChannel(notificationChannel);
    }

    /**
     * 创建发送下一条一言通知的Alarm
     */
    private void createSendNotificationNextHourAlarm() {
        Calendar calendar = Calendar.getInstance();
        // 增加时间为随机下3 ~ 5小时进行生成
        calendar.add(Calendar.HOUR_OF_DAY, RandomUtils.nextInt(3) + 3);
        // 8点之后
        if (calendar.get(Calendar.HOUR_OF_DAY) < 8) {
            calendar.set(Calendar.HOUR_OF_DAY, 8);
        }
        // 随机分钟
        calendar.set(Calendar.MINUTE, RandomUtils.nextInt(60));

        // 创建发送下一条一言的Alarm
        ContextUtils.getAlarmManager().cancel(SEND_NOTE_INTERVAL_INTENT);
        ContextUtils.getAlarmManager().set(AlarmManager.RTC, calendar.getTimeInMillis(), SEND_NOTE_INTERVAL_INTENT);
    }
}
