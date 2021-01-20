package com.moofficial.moweb.Moweb.MoDownload;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.webkit.URLUtil;

import androidx.core.app.NotificationCompat;

import com.moofficial.moessentials.MoEssentials.MoContext.MoContext;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult.MoOnHandleMessage;
import com.moofficial.moweb.R;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.tonyodev.fetch2.Status.COMPLETED;
import static com.tonyodev.fetch2.Status.DOWNLOADING;

public class MoDownloadItem extends MoContext implements FetchListener {

    public final static long UPDATE_NOTIFICATION_RATE = 1000;
    public final static String EXTRA_ID = "download_extra_id";
    public final static String TAG = MoDownloadItem.class.getSimpleName();



    public MoDownloadItem(Context c) {
        super(c);
    }



    /**
     * updates the notification for with the
     * id passed as param
     * @param id
     * @param n
     */
    private void notifyNotification(int id, Notification n) {
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(id, n);
    }

    /**
     * updates the notification with progress for the given id
     */
    private void updateNotification(Download download) {
        Status status = download.getStatus();
        int progress = download.getProgress();
        int max = 100;
        // todo better download speed handling, changing it to MB if it goes beyond some value
        int kBps = (int) (download.getDownloadedBytesPerSecond()/1024);
        String name = download.getFileUri().getLastPathSegment();

        int id = download.getId();

        NotificationCompat.Builder n = new NotificationCompat.Builder(context,
                MoDownloadManager.DOWNLOAD_CHANNEL_NOTIFICATION_ID)
                .setColorized(true)
                .setContentTitle(name)
                .setColor(getColor(R.color.colorAccent))
                .setSmallIcon(MoDownloadManager.DOWNLOAD_SMALL_ICON)
                .setGroup(MoDownloadManager.GROUP_KEY_DOWNLOAD)
                .setAutoCancel(false)
                .setDeleteIntent(getDeletePendingIntent(id));

        switch (status) {
            case ADDED:
                n.setProgress(0,0,true)
                        .setOngoing(true)
                        .setCategory(Notification.CATEGORY_PROGRESS);
                break;
            case DOWNLOADING:
               n.setProgress(max, progress,false)
                       .setCategory(Notification.CATEGORY_PROGRESS)
                       .setOngoing(true)
                       .addAction(R.drawable.ic_baseline_pause_24,
                        getString(R.string.download_notification_pause), null)
                       .addAction(R.drawable.ic_baseline_cancel_24,
                        getString(R.string.download_notification_cancel),null)
                       .setContentText(progress + "%")
                        .setSubText(kBps + "KB/s");
                break;
            case COMPLETED:
                n.setOngoing(false).setContentText("File was successfully downloaded!");
                break;
        }

        notifyNotification(id,n.build());
    }

    /**
     * when the user dismisses the notification
     * this is called
     * @return pending intent for broadcasting
     */
    private PendingIntent getDeletePendingIntent(int id) {
        Intent deleteIntent = new Intent(this.context, MoDownloadReceiver.class);
        deleteIntent.setAction(MoDownloadManager.ACTION_DELETE_NOTIFICATION);
        deleteIntent.putExtra(EXTRA_ID, id);
        return PendingIntent.getBroadcast(context,
                id,
                deleteIntent, PendingIntent.FLAG_ONE_SHOT);
    }






    @Override
    public void onAdded(@NotNull Download download) {
        MoDownloadManager.add(context, download.getId());
        updateNotification(download);
        MoLog.print("on added " + download.getId() );
    }

    @Override
    public void onCancelled(@NotNull Download download) {
        MoLog.print("on cancelled " + download.getId() );
    }

    @Override
    public void onCompleted(@NotNull Download download) {
        updateNotification(download);
        MoLog.print("on completed " + download.getId() );
    }

    @Override
    public void onDeleted(@NotNull Download download) {
        MoLog.print("on deleted " + download.getId() );
    }

    @Override
    public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

    }

    @Override
    public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {
        MoLog.print("on error " + download.getId() );
    }

    @Override
    public void onPaused(@NotNull Download download) {
        MoLog.print("on paused " + download.getId() );
    }

    @Override
    public void onProgress(@NotNull Download download, long etaToFinish, long averageBytePerSec) {
        updateNotification(download);
        MoLog.print(String.format("progress(eta:%s,bps:%s) => p = %s", etaToFinish,
                averageBytePerSec, download.getProgress()));
    }

    @Override
    public void onQueued(@NotNull Download download, boolean b) {

    }

    @Override
    public void onRemoved(@NotNull Download download) {

    }

    @Override
    public void onResumed(@NotNull Download download) {

    }

    @Override
    public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

    }

    @Override
    public void onWaitingNetwork(@NotNull Download download) {

    }
}
