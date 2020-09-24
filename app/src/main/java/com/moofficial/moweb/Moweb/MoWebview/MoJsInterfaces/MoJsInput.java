package com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoWebAutoFillSession;

import java.io.IOException;
import java.io.InputStream;

// sends js inputs back to the app
// for example when the user uses a
// text field to enter things
public class MoJsInput {

    public static final String MO_JS = "MoJs";
    public static String SCRIPT = "";
    public static final String NAME = "jsInput";

    /**
     * a boolean indicating that there is
     * a form on the current page
     * and the user tried to edit it
     * false initially because we do not know
     */
    private boolean thereIsAForm = false;
    private Context context;
    private MoWebAutoFillSession session;



    /**
     * notifies the class that there is
     * a form inside the current page and
     * the user has edit it
     */
    @JavascriptInterface
    public void activate() {
        this.thereIsAForm = true;
    }

    /**
     * gathers all the forms
     * data into our system
     * @param webView to gather auto-fill data for
     *
     */
    public void gatherData(WebView webView) {
        this.context = webView.getContext();
        session = new MoWebAutoFillSession(webView);
        webView.evaluateJavascript(SCRIPT,null);
    }

    /**
     * one by one the script will call this method
     * to pass in the id, value, and type of the
     * input that user typed into
     * and then we process them
     * @param id of the input
     * @param value of the input
     * @param type of the input
     * @param name used to find the input that we are looking for if
     *             the element does not have any ids
     */
    @JavascriptInterface
    public void onGather(String name,String id,String value,String type) {
        session.add(id.isEmpty()?name:id,value,type);
        print(String.format("{name = %s, id = %s,value = %s, type == %s}",name,id,value,type));
    }

    /**
     * called from the js interface
     * when it is done calling on gathered
     */
    @JavascriptInterface
    public void onFinishedGathering() {
        session.processAndClearSession(this.context);
        print("on finished gathering");
    }

    @JavascriptInterface
    public void print(String p) {
        Log.d(MO_JS,p);
    }

    /**
     * reads the js file inside the asset folder
     * and loads it inside a string and save it
     * so we don't load it multiple times
     * @param c context
     */
    public static void loadScript(Context c) {
        if(!SCRIPT.isEmpty())
            return;
        try {
            InputStream f = c.getAssets().open("TextFieldDetection.js");
            byte[] b = new byte[f.available()];
            f.read(b);
            SCRIPT = new String(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
