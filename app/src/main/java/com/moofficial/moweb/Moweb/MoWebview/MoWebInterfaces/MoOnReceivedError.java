package com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces;

import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

public interface MoOnReceivedError {

    void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);

}
