package com.moofficial.moweb.Moweb.MoUrl;

import android.webkit.URLUtil;

import java.util.regex.Pattern;

public class MoUrlUtils {

    public static final String UNIQUE_URL_SANDWICH = "moweb";
    private static final Pattern UNIQUE_URL_REGEX = Pattern.compile("([?&])?moweb\\d*moweb");

    public static boolean isUrl(String s){
        return URLUtil.isValidUrl(s);
    }

    public static String getBaseUrl(String u){
        return getBaseUrl(new MoURL(u));
    }


    public static String getBaseUrl(MoURL url){
        return url.getUrl().getProtocol() + "://" + url.getUrl().getHost();
    }


    /**
     * given a url, make changes that makes it a different
     * unique url, but it still provides the same output as the previous
     * url
     * This was created to make sure that we use network to load the
     * url when changing the desktop mode, rather than getting it from cache
     * @param url
     * @return
     */
    public static String makeUrlUnique(String url) {
        if(url==null){
            return "";
        }
        // we remove any uniqueness to it first and change the url
        // again to make it unique
        url = removeUrlUniqueness(url);
        StringBuilder unique = new StringBuilder();
        unique.append(url);
        prepareFieldAddition(url, unique);
        unique.append(UNIQUE_URL_SANDWICH)
              .append(System.currentTimeMillis())
              .append(UNIQUE_URL_SANDWICH);
        return unique.toString();
    }

    /**
     * removing the uniqueness that we gave
     * the url inside the above function (url unique)
     * @param url to remove uniqueness from
     * @return url without being unique
     */
    public static String removeUrlUniqueness(String url) {
        return UNIQUE_URL_REGEX.matcher(url).replaceAll("").trim();
    }

    /**
     * when you want to add another argument to the
     * url, we use this algorithm to allow it
     * @param url to add another field to
     * @param unique string builder
     */
    private static void prepareFieldAddition(String url, StringBuilder unique) {
        if (url.contains("?")) {
            unique.append('&');
        }
        else {
            if (url.lastIndexOf('/') <= 7) {
                unique.append('/');
            }
            unique.append('?');
        }
    }


}
