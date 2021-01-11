package com.moofficial.moweb.Moweb.MoWebview;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoContext.MoContext;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;

public class MoDownloadListener extends MoContext implements DownloadListener {


    public MoDownloadListener(Context c) {
        super(c);
    }

    // for downloading a link
    public static void download(Context c,String url) {
        Toast.makeText(c,"fek kon dare download mishe",Toast.LENGTH_SHORT).show();
        if(!isSafe(url)){
            return;
        }
        DownloadManager mManager = (DownloadManager) c.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request mRqRequest = new DownloadManager.Request(
                Uri.parse(url));
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
        if(url.contains("apk")){
            // it is downloading an application ask the user
            return;
        }





//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//
//        request.setMimeType(mimeType);
//        //------------------------COOKIE!!------------------------
//        String cookies = CookieManager.getInstance().getCookie(url);
//        request.addRequestHeader("cookie", cookies);
//        //------------------------COOKIE!!------------------------
//        request.addRequestHeader("User-Agent", userAgent);
//        request.setDescription("Downloading file...");
//        request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
//        request.allowScanningByMediaScanner();
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
//        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
//        dm.enqueue(request);
//        Toast.makeText(context.getApplicationContext(), "Downloading File", Toast.LENGTH_LONG).show();
    }


    private static boolean isSafe(String url){
        return false;
    }

}
