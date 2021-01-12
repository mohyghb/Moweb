package com.moofficial.moweb.Moweb.MoDownload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.R;

import java.io.File;
import java.util.Objects;

public class MoDownloadManager {

    public static final String DOWNLOAD_CHANNEL_NOTIFICATION_ID = "MoDownload_CHANNEL_ID";
    public static final String DOWNLOAD_CHANNEL_NOTIFICATION_NAME = "MoDownload";
    public static final String DOWNLOAD_CHANNEL_NOTIFICATION_DESCRIPTION = "When downloading from web," +
            " you will get notification for their progress";

    // todo you need storage access to show them all
    public static void fin() {
//        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        for (File files : Objects.requireNonNull(f.listFiles())) {
//            MoLog.print(files.getName());
//        }
//        int i = 0;
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
