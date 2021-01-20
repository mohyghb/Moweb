package com.moofficial.moweb.Moweb.MoDownload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.R;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

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
    public static final String ACTION_PAUSE_DOWNLOAD = "action_pause_notification";
    public static final String ACTION_RESUME_DOWNLOAD = "action_resume_notification";
    public static final String ACTION_CANCEL_DOWNLOAD = "action_cancel_notification";

    public final static String GROUP_KEY_DOWNLOAD = "com.android.moweb.DOWNLOAD_ITEM";
    public final static int GROUP_ID = -1;

    public static final HashSet<Integer> DOWNLOADING_SET = new HashSet<>();
    public static Fetch fetch;


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

    /**
     * pauses the download with the id passed in
     * @param id to pause download for
     */
    public static void pause(int id) {
        if (fetch != null) {
            fetch.pause(id);
        }
    }

    /**
     * resumes the download for the id passed in
     * @param id to resume download for
     */
    public static void resume(int id) {
        if (fetch != null) {
            fetch.resume(id);
        }
    }

    /**
     * cancels the download for the id passed in
     * @param id to cancel download for
     */
    public static void cancel(Context c, int id) {
        if (fetch != null) {
            fetch.cancel(id);
            // todo we can give them the option to either delete on cancel or not
            //  normally we automatically delete if they cancel
            fetch.delete(id);
            getNotificationManager(c).cancel(id);
            remove(c,id);
        }
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

    /**
     * creates a download request based on the information passed in
     * @param url
     * @param contentDisposition
     * @param finalMimeType
     * @param userAgent
     */
    public static void enqueueDownload(String url, String contentDisposition, String finalMimeType, String userAgent) {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

        String path = dir.getPath() + "/" + URLUtil.guessFileName(url,
                contentDisposition, finalMimeType).replace(" ","");

        Request request = new Request(url, path);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        request.addHeader("User-Agent", userAgent);


        fetch.enqueue(request, request1 -> {
            //Request enqueued for download
            MoLog.print("enqeue for download " + request1.toString());
        }, error -> {
            //Error while enqueuing download
            MoLog.print("error while enquing download "+ error.toString());
        });
    }

    /**
     * sets up the listener for downloads
     * @param context
     */
    public static void setUp(Context context) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(10)
                .setProgressReportingInterval(MoRequestListener.UPDATE_NOTIFICATION_RATE)
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.addListener(new MoRequestListener(context));
    }
}
