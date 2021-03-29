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

    private static final String desktop_mode = "Mozilla/5.0 (X11; Linux x86_64)" +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36";

    private static final String mobile_mode = "Mozilla/5.0 (Linux; U; Android 4.4; en-" +
            "us; Nexus 4 Build/JOP24G) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile" +
            "Safari/534.30";

    public static final int WRITE_EXTERNAL_PERMISSION_CODE = 1;
    public static final String SOME_FEATURES_DISABLED = "Some features are disabled";

    /**
     * request write to external storage permission
     *
     * @param activity
     */
    public static void requestWritePermission(Activity activity) {
        // if we dont already have the permission
        if (!hasWritePermission(activity)) {
            //requesting permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_CODE);
        }
    }


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


    private static String run() {
        return null;
    }
}
