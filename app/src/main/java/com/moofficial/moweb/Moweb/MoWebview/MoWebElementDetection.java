package com.moofficial.moweb.Moweb.MoWebview;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class MoWebElementDetection extends Object {

    public static final String CLASS_NAME = "mowebelement";
    public static final String UNDEFINED = "undefined";
    public static final String URL = "url=";


    private static String INJECTED_JS = "";



    private String href = null;
    private String innerText = null;
    private String title = null;
    private MoWebView webView;

    public MoWebElementDetection(MoWebView webView) {
        this.webView = webView;
    }

    //    @JavascriptInterface
//    // inner text or href serves as a URL
//    public void onClick(String event, String id,
//                        String className, String href,
//                        String nodeName, String innerText,
//                        String innerHTML, String outerHTML,
//                        String dataset, String title,
//                        String tagName, String firstChild,
//                        String nextElementSibling) {
//
//    }


    /**
     * every time the user touches an element, we update this info
     * in case we want to access it via the web view long click listener
     * @param href link of the website
     * @param innerText inner text of element
     * @param title title of element
     */
    @JavascriptInterface
    public void hrefClick(String href,String innerText,String title){
        // need to parse href
        // if it contains 'url' just parse that part out
        this.href = parseHref(href);
        this.innerText = parseInnerText(innerText);
        this.title = title;
    }

    @JavascriptInterface
    public void onTouchEnd(){
        this.href = null;
        this.innerText = null;
        this.title = null;
    }


    private String parseInnerText(String innerText){
        String[] list = innerText.split("\n");
        return list[list.length-1];
    }

    // TODO  a case where it doesnt have url but also the text is not url
    private String parseHref(String href){
        if(href == null)
            return "";
        if(href.contains(URL)){
            String[] spl = href.split(URL);
            if(spl.length>1){
                String[] spl1 = spl[1].split("&");
                return spl1[0];
            }else{
                String[] spl1 = spl[0].split("&");
                return spl1[0];
            }
//        }else if(href.contains(MoGoogle.GOOGLE_SEARCH_COMMAND)){
//            // we need to add the google search to it
//            return MoGoogle.GOOGLE_ADDRESS + href;
          }
          else if(!MoUrlUtils.isUrl(href)){
            // if it is not a valid url
            // then add the base url to it
            return webView.getBaseUrl() + href;
        }
        return href;
    }



    static String injectJs(Context c) {
        if(!INJECTED_JS.isEmpty()){
            return INJECTED_JS;
        }
        BufferedReader reader = null;
        StringBuilder js = new StringBuilder();
        try {
            reader = new BufferedReader(
                    new InputStreamReader(c.getAssets().open("onLongClick"), StandardCharsets.UTF_8));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //process line
                js.append(mLine);
            }
        } catch (IOException e) {
            //log the exception
            int i = 0;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        INJECTED_JS  =js.toString();
        return js.toString();
    }


    /**
     * returns true if the input is not null and not empty and not equal to undefined
     * @param input
     * @return
     */
    static boolean isValidVar(String input){
        return input != null && !input.isEmpty() && !input.equals(UNDEFINED);
    }


    boolean isValidDialog() {
        return isValidVar(href) || isValidVar(innerText);
    }


    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getInnerText() {
        return innerText;
    }

    public void setInnerText(String innerText) {
        this.innerText = innerText;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
