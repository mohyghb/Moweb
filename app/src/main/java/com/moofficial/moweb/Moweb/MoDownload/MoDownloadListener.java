package com.moofficial.moweb.Moweb.MoDownload;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoContext.MoContext;
import com.moofficial.moweb.R;

import static android.content.Context.DOWNLOAD_SERVICE;

public class MoDownloadListener extends MoContext implements DownloadListener {


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

                MoDownloadManager.enqueueDownload(url, contentDisposition, finalMimeType, userAgent);
            }
        }.start();
    }


    private static boolean isSafe(String url){
        return false;
    }

}
