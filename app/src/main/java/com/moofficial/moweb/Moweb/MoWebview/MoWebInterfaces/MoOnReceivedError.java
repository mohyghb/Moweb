package com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

public interface MoOnReceivedError {

    public static final MoOnReceivedError EMPTY = new MoOnReceivedError() {
        @Override
        public void onSSLErrorReceived(WebView view, SslErrorHandler handler, SslError error) {

        }

        @Override
        public void hideError() {

        }
    };

    void onSSLErrorReceived(WebView view, SslErrorHandler handler, SslError error);

    void hideError();
}
