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
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.FetchFileServerDownloader;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MoDownloadListener extends MoContext implements DownloadListener {


    public static Fetch fetch;


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

        //todo do this at the beggining of the app
        MoDownloadManager.createNotificationChannel(context);


        // how you get mime type from a url
        String s = MimeTypeMap.getFileExtensionFromUrl(url);
        mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s);




        String finalMimeType = mimeType;
        new Thread() {
            @Override
            public void run() {
                // todo get the storage read and write access here or somewhere when you are downloading


                // todo if a file is already downloaded, basically if the path is the same, it won't donwload
                //  it again

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
        }.start();
    }






    private static boolean isSafe(String url){
        return false;
    }

    /**
     * sets up the listener for downloads
     * @param context
     */
    public static void setUp(Context context) {
        FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(context)
                .setDownloadConcurrentLimit(10)
                .setProgressReportingInterval(MoDownloadItem.UPDATE_NOTIFICATION_RATE)
                .build();
        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch.addListener(new MoDownloadItem(context));
    }

}
