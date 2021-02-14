package com.moofficial.moweb.Moweb.MoWebview.MoWebError;

import android.net.http.SslError;

public class MoSSLUtils {

    public static String getTitle(SslError error) {
        switch (error.getPrimaryError()) {
            case SslError.SSL_EXPIRED:
                return "SSL Expired";
            case SslError.SSL_DATE_INVALID:
                return "SSL Invalid Date";
            case SslError.SSL_IDMISMATCH:
                return "SSL Id Mismatch";
            case SslError.SSL_INVALID:
                return "SSL Invalid";
            case SslError.SSL_NOTYETVALID:
                return "SSL Not Yet Valid";
            case SslError.SSL_UNTRUSTED:
                return "SSL Untrusted";
        }
        return "SSL Error";
    }
}
