package com.moofficial.moweb.Moweb.MoDownload;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoMultiThread.MoThread.MoOnThreadRun;
import com.moofficial.moessentials.MoEssentials.MoMultiThread.MoThread.MoThread;
import com.moofficial.moweb.R;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.Func;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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

    public static final HashMap<String, Download> DOWNLOADING_MAP = new HashMap<>();
    public static Fetch fetch;

    /**
     * updates the progress for download
     * @param d
     */
    public static void update(Download d) {
        if (d == null)
            return;
        DOWNLOADING_MAP.put(d.getFile(), d);
    }


    /**
     * adds the item to the set of
     * currently items being downloaded
     * @param d item currently being downloaded
     */
    public static void add(Context c, Download d) {
        if (DOWNLOADING_MAP.isEmpty()) {
            // create grouping for download notifications
            createNotificationGroup(c);
        }
        DOWNLOADING_MAP.put(d.getFile(), d);
        MoLog.print("adding notification "+ d.getId());
    }

    /**
     * removes the item to the set of
     * currently items being downloaded
     * @param d item currently being
     *         downloaded or finished downloading
     */
    public static void remove(Context context, Download d) {
        DOWNLOADING_MAP.remove(d.getFile());
        if (DOWNLOADING_MAP.isEmpty()) {
            // remove the grouping notification
            getNotificationManager(context).cancel(GROUP_ID);
        }
        getNotificationManager(context).cancel(d.getId());
        MoLog.print("removing notification "+ d.getId());
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
     * @param d to cancel download for
     */
    public static void cancel(Context c, Download d) {
        if (fetch != null && d != null) {
            int id = d.getId();
            fetch.cancel(id);
            // todo we can give them the option to either delete on cancel or not
            //  normally we automatically delete if they cancel
            fetch.delete(id);
            getNotificationManager(c).cancel(id);
            remove(c, d);
        }
    }

    /**
     * registers a listener for the download manager so you
     * can observe incoming updates for download
     * @param listener to be added
     */
    public static void registerListener(FetchListener listener) {
        if (fetch.getListenerSet().contains(listener)) {
            return;
        }
        fetch.addListener(listener);
    }

    /**
     * removes the listener
     * @param listener to be removed
     */
    public static void unregisterListener(FetchListener listener) {
        fetch.removeListener(listener);
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }


    /**
     * returns the downloads inside the
     * specified download dir of the user
     * @return list of files
     */
    public static List<MoDownload> getDownloads() {
        ArrayList<MoDownload> downloads = new ArrayList<>();
        File dir = getDir();
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            if (file != null && file.isFile()) {
                MoDownload d = new MoDownload(file);
                // to know whether this is currently being downloaded
                if (DOWNLOADING_MAP.containsKey(file.getPath())) {
                    d.link(DOWNLOADING_MAP.get(file.getPath()));
                }
                downloads.add(d);
            }
        }
        return downloads;
    }

    /**
     * deletes the files that are
     * @param downloadsToDelete
     */
    public static void delete(Iterable<MoDownload> downloadsToDelete, Runnable onDone) {
        new MoThread<Void>().doBackground(() -> {
            for (MoDownload download: downloadsToDelete) {
                download.delete();
            }
            onDone.run();
            return null;
        }).start();

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

    public static void enqueueDownload(String url) {
        enqueueDownload(url, "","","");
    }

    /**
     * creates a download request based on the information passed in
     * @param url
     * @param contentDisposition
     * @param finalMimeType
     * @param userAgent
     */
    public static void enqueueDownload(String url, String contentDisposition, String finalMimeType, String userAgent) {
        String path = getDir().getPath() + "/" + URLUtil.guessFileName(url,
                contentDisposition, finalMimeType).replace(" ","");
        File f = new File(path);
        if (f.exists()) {
            return;
        }
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
     * todo add option so that user can change this
     * @return the download directory
     */
    public static File getDir() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
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

    /**
     * when the app is going to be destroyed we
     * need to run this function
     */
    public static void onDestroy() {
        fetch.close();
    }

    public static boolean isSafe(String url) {
        return !url.contains("apk");
    }
}
