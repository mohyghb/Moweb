package com.moofficial.moweb.Moweb.MoWebview.MoClient;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;


import java.net.URISyntaxException;

public class MoWebClient extends WebViewClient {


    private static final String INTENT_URL = "intent:";
    private static final String HTTP = "http";
    private static final String FALL_BACK_URL = "browser_fallback_url";




   //private MoRunnable r;

    public MoWebClient() {
        super();
    }





    @Override
    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {


        String url = request.getUrl().toString();
        if (url.startsWith(HTTP)) return false;//open web links as usual
        //try to find browse activity to handle uri
        Uri parsedUri = Uri.parse(url);
        Context context = view.getContext();
        PackageManager packageManager = context.getPackageManager();
        Intent browseIntent = new Intent(Intent.ACTION_VIEW).setData(parsedUri);
        if (browseIntent.resolveActivity(packageManager) != null) {
            context.startActivity(browseIntent);
            return true;
        }
        //if not activity found, try to parse intent://
        if (url.startsWith(INTENT_URL)) {
            try {
                Intent intent = Intent.parseUri(url, Intent.URI_INTENT_SCHEME);
                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                    return true;
                }
                //try to find fallback url
                String fallbackUrl = intent.getStringExtra(FALL_BACK_URL);
                if (fallbackUrl != null) {
                    view.loadUrl(fallbackUrl);
                    return true;
                }
                //invite to install
                Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(
                        Uri.parse("market://details?id=" + intent.getPackage()));
                if (marketIntent.resolveActivity(packageManager) != null) {
                    context.startActivity(marketIntent);
                    return true;
                }
            } catch (URISyntaxException e) {
                //not an intent uri
            }
        }
        return true;//do nothing in other cases
    }





}
