package com.moofficial.moweb.Moweb.MoTab.MoTabType;

import android.content.Context;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.annotation.IntDef;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;


public class MoTabType implements MoSavable, MoLoadable {


    @IntDef({TYPE_NORMAL,TYPE_PRIVATE})
    public @interface TabType{}
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_PRIVATE = 1;


    @TabType private int type;
    private WebView webView;

    public MoTabType(int t){
        this.type = t;
    }

    public MoTabType(int t,WebView webView){
        type = t;
        this.webView = webView;
    }

    public MoTabType(String type,Context c,WebView webView){
        this.webView = webView;
        this.load(type,c);
    }

    public int getType() {
        return type;
    }

    public void setType(@TabType int type) {
        this.type = type;
        if(webView!=null){
            switch (type){
                case TYPE_NORMAL:
                    enableNormal();
                    break;
                case TYPE_PRIVATE:
                    enablePrivate();
                    break;
            }
        }
    }

    /**
     * this is used to make web view store no cache or any form data
     * which makes the browsing private
     */
    private void enablePrivate() {
        //Make sure No cookies are created
        CookieManager.getInstance().setAcceptCookie(false);
        //Make sure no caching is done
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.getSettings().setAppCacheEnabled(false);
        webView.clearHistory();
        webView.clearCache(true);
        //Make sure no auto-fill for Forms/ user-name password happens for the app
        webView.clearFormData();
        webView.getSettings().setSavePassword(false);
        webView.getSettings().setSaveFormData(false);
    }

    private void enableNormal() {
        CookieManager.getInstance().setAcceptCookie(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webView.getSettings().setSaveFormData(true);
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        this.setType(Integer.parseInt(data));
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return this.type+"";
    }



    // rs[0] = normal
    // rs[1] = incognito
    public static void runForMultipleTypes(int type, Runnable ... rs){
        switch (type){
            case MoTabType.TYPE_NORMAL:
                rs[0].run();
                break;
            case MoTabType.TYPE_PRIVATE:
                rs[1].run();
                break;
        }
    }


}
