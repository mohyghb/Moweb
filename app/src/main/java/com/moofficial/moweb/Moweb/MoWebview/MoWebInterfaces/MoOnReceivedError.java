package com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces;

import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;

public interface MoOnReceivedError {

    public static final MoOnReceivedError EMPTY = new MoOnReceivedError() {
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {

        }

        @Override
        public void onSSLErrorReceived(WebView view, SslErrorHandler handler, SslError error) {

        }

        @Override
        public void hideError() {

        }
    };

    void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);
    void onSSLErrorReceived(WebView view, SslErrorHandler handler, SslError error);
    void hideError();
}
