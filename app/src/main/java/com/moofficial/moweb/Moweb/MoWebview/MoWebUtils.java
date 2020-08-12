package com.moofficial.moweb.Moweb.MoWebview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

public class MoWebUtils {

    public static final int WRITE_EXTERNAL_PERMISSION_CODE = 1;
    public static final String SOME_FEATURES_DISABLED = "Some features are disabled";

    public static void requestWritePermission(Activity activity){
        // if we dont already have the permission
        if (!hasWritePermission(activity)) {
            //requesting permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_CODE);
        }
    }


    public static boolean hasWritePermission(Context context) {
        int permissionCheck = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * enables or disables the desktop mode for a web view
     * @param webView
     * @param enabled
     */
    public static void setDesktopMode(WebView webView,boolean enabled) {
        final WebSettings webSettings = webView.getSettings();

        final String newUserAgent;
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString().replace("Mobile", "eliboM").replace("Android", "diordnA");
        }
        else {
            newUserAgent = webSettings.getUserAgentString().replace("eliboM", "Mobile").replace("diordnA", "Android");
        }

        webSettings.setUserAgentString(newUserAgent);
        webSettings.setLoadWithOverviewMode(enabled);
        webSettings.setUseWideViewPort(enabled);
        webView.reload();

    }

    /**
     * returns true if the web view is in desktop mode
     * @param webView
     * @return
     */
    public static boolean isInDesktopMode(WebView webView){
        return !webView.getSettings().getUserAgentString().contains("Mobile");
    }


}
