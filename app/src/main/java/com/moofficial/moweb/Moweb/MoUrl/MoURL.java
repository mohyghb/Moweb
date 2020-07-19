package com.moofficial.moweb.Moweb.MoUrl;

import android.content.Context;
import android.webkit.URLUtil;


import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;

import java.net.MalformedURLException;
import java.net.URL;

public class MoURL implements MoSavable, MoLoadable {





    private String urlString;
    private URL url;

    public MoURL(String url){
        this.urlString = url;
        try {
            this.url = new URL(this.urlString);
        } catch (MalformedURLException ignore) {}
    }

    public MoURL(String data,Context c){
        this(data);
    }

    public String getUrlString() {
        return urlString;
    }

    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }


    /**
     * returns true if the url is not null and valid url
     * @return
     */
    public boolean isValid(){
        return this.url!=null && URLUtil.isValidUrl(this.urlString);
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        // ignore
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return urlString;
    }
}
