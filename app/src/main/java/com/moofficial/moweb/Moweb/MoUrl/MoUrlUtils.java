package com.moofficial.moweb.Moweb.MoUrl;

import android.webkit.URLUtil;

import java.net.MalformedURLException;
import java.net.URL;

public class MoUrlUtils {

    public static boolean isUrl(String s){
        return URLUtil.isValidUrl(s);
    }

    public static String getBaseUrl(String u){
        return getBaseUrl(new MoURL(u));
//        try {
//            URL url = new URL(u);
//            return url.getProtocol() + "://" + url.getHost();
//        } catch (MalformedURLException ignored) {
//        }
        //return "";
    }


    public static String getBaseUrl(MoURL url){
        return url.getUrl().getProtocol() + "://" + url.getUrl().getHost();
    }



}