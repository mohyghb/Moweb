package com.moofficial.moweb.Moweb.MoWebview.MoWebError;

import android.webkit.WebResourceError;
import android.webkit.WebViewClient;

public class MoWebResourceErrorUtils {

    public static String getTitle(WebResourceError error) {
        switch (error.getErrorCode()) {
            case WebViewClient.ERROR_BAD_URL:
                return "Bad Url Error!";
            case WebViewClient.ERROR_AUTHENTICATION:
                return "Authentication Error";
            case WebViewClient.ERROR_IO:
                return "IO Error";
            case WebViewClient.ERROR_FILE_NOT_FOUND:
                return "File not found error";
            case WebViewClient.ERROR_TIMEOUT:
                return "Time out error";
            case WebViewClient.ERROR_CONNECT:
                return "Connection error";
            case WebViewClient.ERROR_FAILED_SSL_HANDSHAKE:
                return "SSL handshake error";
            case WebViewClient.ERROR_FILE:
                return "File Error";
            case WebViewClient.ERROR_HOST_LOOKUP:
                return "Look up failed";
            case WebViewClient.ERROR_PROXY_AUTHENTICATION:
                return "Proxy authentication error";
            case WebViewClient.ERROR_REDIRECT_LOOP:
                return "Redirect loop error";
            case WebViewClient.ERROR_TOO_MANY_REQUESTS:
                return "Too many request error";
            case WebViewClient.ERROR_UNKNOWN:
                return "Unknown error";
            case WebViewClient.ERROR_UNSAFE_RESOURCE:
                return "Unsafe resource error";
            case WebViewClient.ERROR_UNSUPPORTED_AUTH_SCHEME:
                return "Unsupported auth scheme error";
            case WebViewClient.ERROR_UNSUPPORTED_SCHEME:
                return "Unsupported scheme error";
        }
        return "Web error";
    }

}
