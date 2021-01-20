package com.moofficial.moweb.Moweb.MoDownload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import androidx.core.app.NotificationCompat;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.R;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class MoDownloadManager {

    public static final String DOWNLOAD_CHANNEL_NOTIFICATION_ID = "MoDownload_CHANNEL_ID";
    public static final String DOWNLOAD_CHANNEL_NOTIFICATION_NAME = "MoDownload";
    public static final String DOWNLOAD_CHANNEL_NOTIFICATION_DESCRIPTION = "When downloading from web," +
            " you will get notification for their progress";
    public static final int DOWNLOAD_SMALL_ICON = R.drawable.ic_baseline_arrow_downward_24;
    public static final String ACTION_DELETE_NOTIFICATION = "action_delete_notification";
    public static final String ACTION_OPEN_NOTIFICATION = "action_open_notification";

    public final static String GROUP_KEY_DOWNLOAD = "com.android.moweb.DOWNLOAD_ITEM";
    public final static int GROUP_ID = -1;

    public static final HashSet<Integer> DOWNLOADING_SET = new HashSet<>();


    /**
     * adds the item to the set of
     * currently items being downloaded
     * @param id item currently being downloaded
     */
    public static void add(Context c, int id) {
        if (DOWNLOADING_SET.isEmpty()) {
            // create grouping for download notifications
            createNotificationGroup(c);
        }
        DOWNLOADING_SET.add(id);
        MoLog.print("adding notification "+ id);
    }

    /**
     * removes the item to the set of
     * currently items being downloaded
     * @param id item currently being
     *         downloaded or finished downloading
     */
    public static void remove(Context context, int id) {
        DOWNLOADING_SET.remove(id);
        if (DOWNLOADING_SET.isEmpty()) {
            // remove the grouping notification
            getNotificationManager(context).cancel(GROUP_ID);
        }
        MoLog.print("removing notification "+ id);
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }



    // todo you need storage access to show them all
    public static void fin() {
//        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        for (File files : Objects.requireNonNull(f.listFiles())) {
//            MoLog.print(files.getName());
//        }
//        int i = 0;
    }

    /**
     * creates a notification group for downloads
     * @param context
     */
    private static void createNotificationGroup(Context context) {
        NotificationCompat.Builder b = new NotificationCompat.Builder(context,
                MoDownloadManager.DOWNLOAD_CHANNEL_NOTIFICATION_ID)
                .setGroup(GROUP_KEY_DOWNLOAD)
                .setGroupSummary(true)
                .setSmallIcon(MoDownloadManager.DOWNLOAD_SMALL_ICON)
                .setOngoing(true)
                .setAutoCancel(true);
        NotificationManager manager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        manager.notify(GROUP_ID, b.build());
    }

    /**
     * creates a notification channel for API 26+
     * so that the user can disable or modify it to their
     * liking
     * @param c context needed
     */
    public static void createNotificationChannel(Context c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(DOWNLOAD_CHANNEL_NOTIFICATION_ID,
                    DOWNLOAD_CHANNEL_NOTIFICATION_NAME, importance);
            channel.setDescription(DOWNLOAD_CHANNEL_NOTIFICATION_DESCRIPTION);
            NotificationManager notificationManager = c.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}
