package com.moofficial.moweb.MoHTML.Html;


import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;


public class MoHtml {

    public static Connection.Response doc;

    public static String getHtml(String url) throws IOException {
        doc = Jsoup.connect(url).ignoreContentType(true).execute();
        return doc.body();
    }

}
