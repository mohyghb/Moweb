package com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces;

import android.content.Context;
import android.webkit.JavascriptInterface;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;

import java.io.IOException;
import java.io.InputStream;

// sends js inputs back to the app
// for example when the user uses a
// text field to enter things
public class MoJsInput {

    public static String SCRIPT = "";
    public static final String NAME = "jsInput";

    // when the interface returns an id
    @JavascriptInterface
    public void onReturn(String id,String innerText,String value,String type) {
        int i = 0;
        int p = 0;
        MoLog.print(String.format("%s,%s,%s,%s",id,innerText,value,type));
    }

    @JavascriptInterface
    public void isWorking() {
        MoLog.print("is working");
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
