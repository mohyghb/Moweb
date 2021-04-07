package com.moofficial.moweb.Moweb.MoWebview;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.app.ActivityCompat;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoMultiThread.MoThread.MoThread;
import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

public class MoWebUtils {

    /**
     * return true if we have the
     * write into external storage permission
     *
     * @param context
     * @return
     */
    public static boolean hasWritePermission(Context context) {
        int permissionCheck = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * enables or disables the desktop mode for a web view
     *
     * @param webView
     * @param enabled
     */
    public static void setDesktopMode(MoWebView webView, boolean enabled) {
        final WebSettings webSettings = webView.getSettings();
        final String newUserAgent;
        if (enabled) {
            newUserAgent = webSettings.getUserAgentString()
                    .replace("Mobile", "eliboM")
                    .replace("Android", "diordnA");
        } else {
            newUserAgent = webSettings.getUserAgentString()
                    .replace("eliboM", "Mobile")
                    .replace("diordnA", "Android");
        }
        webSettings.setUserAgentString(newUserAgent);
        webSettings.setLoadWithOverviewMode(enabled);
        webSettings.setUseWideViewPort(true);
        webView.forceReload();
    }

    /**
     * returns true if the web view is in desktop mode
     *
     * @param webView
     * @return
     */
    public static boolean isInDesktopMode(WebView webView) {
        return !webView.getSettings().getUserAgentString().contains("Mobile");
    }


    /**
     * clears all the cookies that
     * are stored inside the app
     */
    public static void clearCookies() {
        CookieManager c = CookieManager.getInstance();
        c.removeAllCookies(null);
        c.flush();
    }

    public static void clearCachedImages(Context context) {
        // we need to remove it from both mo_images and the cached images
    }

    public static void updateCookies(Context context) {
        boolean accept = MoSharedPref.get(context)
                .getBoolean(context.getString(R.string.cookies_enabled), false);
        MoLog.print("general cookie has been set to " + accept);
        CookieManager.getInstance().setAcceptCookie(accept);
    }

    public static void updateThirdPartyCookies(Context context) {
        new MoThread<String>().doBackground(() -> {
            CookieManager cookieManager = CookieManager.getInstance();
            boolean accept = MoSharedPref.get(context)
                    .getBoolean(context.getString(R.string.cookies_enabled), false);
            MoLog.print("updating all cookies to " + accept);
            MoTabsManager.getTabs().forEach((MoTab t) -> {
                if (t != null && t.getWebView() != null) {
                    WebView v = t.getWebView();
                    v.post(() -> cookieManager.setAcceptThirdPartyCookies(v, accept));
                }
            });
            return null;
        }).start();
    }

    public static void updateThirdPartyCookies(WebView v) {
        CookieManager cookieManager = CookieManager.getInstance();
        boolean accept = MoSharedPref.get(v.getContext())
                .getBoolean(v.getContext().getString(R.string.cookies_enabled), false);
        cookieManager.setAcceptThirdPartyCookies(v, accept);
    }
}
