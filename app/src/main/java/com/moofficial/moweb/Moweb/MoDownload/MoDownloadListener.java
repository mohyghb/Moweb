package com.moofficial.moweb.Moweb.MoDownload;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.moofficial.moessentials.MoEssentials.MoContext.MoContext;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShareUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moweb.R;

import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MoDownloadListener extends MoContext implements DownloadListener {


    private final static long UPDATE_NOTIFICATION_RATE = 1000;

    public MoDownloadListener(Context c) {
        super(c);
    }

    // for downloading a link
    public static void download(Context c,String url) {
        Toast.makeText(c,"fek kon dare download mishe",Toast.LENGTH_SHORT).show();
//        if(!isSafe(url)){
//            return;
//        }
        DownloadManager mManager = (DownloadManager) c.getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request mRqRequest = new DownloadManager.Request(
                Uri.parse(url))
                .setTitle(c.getString(R.string.app_name));
        if (mManager != null) {
            long idDownLoad = mManager.enqueue(mRqRequest);
        }
    }


    // for downloads from web view
    @Override
    public void onDownloadStart(String url, String userAgent,
                                String contentDisposition, String mimeType,
                                long contentLength) {

//        if(!isSafe(url)){
//            return;
//        }
        if(url.contains("apk")) {
            // it is downloading an application ask the user
            return;
        }

        MoDownloadManager.createNotificationChannel(context);


        // how you get mime type from a url
        String s = MimeTypeMap.getFileExtensionFromUrl(url);
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s);

//
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

        request.setMimeType(mimeType);
        //------------------------COOKIE!!------------------------
        String cookies = CookieManager.getInstance().getCookie(url);
        request.addRequestHeader("cookie", cookies);
        //------------------------COOKIE!!------------------------
        request.addRequestHeader("User-Agent", userAgent);
        request.setDescription("Downloading file...");
        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
        request.allowScanningByMediaScanner();

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,
                URLUtil.guessFileName(url, contentDisposition, mimeType));


        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);


        long id = dm.enqueue(request);
        String name = URLUtil.guessFileName(url, contentDisposition, mimeType);


        new Thread() {
            @Override
            public void run() {
                Timer t = new Timer();
                t.scheduleAtFixedRate(new TimerTask() {
                    @Override
                    public void run() {
                        updateNotification(id, dm, t, name);
                        MoLog.print("Downloading " + id);
                    }
                },0, UPDATE_NOTIFICATION_RATE);
            }
        }.start();
    }

    /**
     * updates the notification based on the id of the notification
     * that is passed as param
     * @param id
     * @param dm
     * @param t
     * @param name
     */
    private void updateNotification(long id, DownloadManager dm, Timer t, String name) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);
        Cursor c = dm.query(q);
        c.moveToFirst();
        int downloaded = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        int fileSize = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        //int dl_progress = (int) ((downloaded * 100l) / fileSize);

        int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
        // todo better status handling
        if ( status == DownloadManager.STATUS_SUCCESSFUL ||
                status == DownloadManager.STATUS_FAILED) {
            t.cancel();
        }

        updateNotification((int)id, fileSize, downloaded, name);
        c.close();
    }


    /**
     * updates the notification with progress for the given id
     * @param id of notification to be updated
     * @param progress of the ongoing download
     */
    private void updateNotification(int id, int max ,int progress, String name) {
        Notification n = new NotificationCompat.Builder(context,
                MoDownloadManager.DOWNLOAD_CHANNEL_NOTIFICATION_ID)
                .setProgress(max, progress,false)
                .setColorized(true)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setContentTitle(name)
                .setColor(getColor(R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_baseline_arrow_downward_24)
                .addAction(R.drawable.ic_baseline_pause_24, getString(R.string.download_notification_pause), null)
                .addAction(R.drawable.ic_baseline_cancel_24,getString(R.string.download_notification_cancel),null)
                .build();
        NotificationManager m = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        m.notify(id, n);
    }


    private static boolean isSafe(String url){
        return false;
    }

}
