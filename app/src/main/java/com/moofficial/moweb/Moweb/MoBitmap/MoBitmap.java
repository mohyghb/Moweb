package com.moofficial.moweb.Moweb.MoBitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MoBitmap implements MoSavable, MoLoadable {


    public static long bitmapIdTracker = 0;

    private long id;
    private String name = "";
    private boolean optimized = true;
    private Bitmap bitmap;
    private MoOnBitmapCapturedListener onBitmapCapturedListener = b -> {};



    public MoBitmap(){
        id = getNextId();
    }

    public MoBitmap(String data,Context context){
        this.load(data,context);
    }

    public MoOnBitmapCapturedListener getOnBitmapCapturedListener() {
        return onBitmapCapturedListener;
    }

    public MoBitmap setOnBitmapCapturedListener(MoOnBitmapCapturedListener onBitmapCapturedListener) {
        this.onBitmapCapturedListener = onBitmapCapturedListener;
        return this;
    }

    public boolean isOptimized() {
        return optimized;
    }

    public MoBitmap setOptimized(boolean optimized) {
        this.optimized = optimized;
        return this;
    }

    public long getId() {
        return id;
    }

    public MoBitmap setId(long id) {
        this.id = id;
        return this;
    }

    public MoBitmap setId(int id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public MoBitmap setName(String name) {
        this.name = name;
        return this;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String stringifyId(){
        return this.id+"";
    }

    public void updateBitmapIdTracker(){
        if(this.id > bitmapIdTracker){
            bitmapIdTracker = this.id;
        }
    }

    /**
     * captures a bitmap from web view
     * we consider the optimized version as well
     * @param wv
     */
    public void captureBitmap(WebView wv){
        if(optimized && getName().equals(wv.getUrl())){
            MoLog.print("(Optimized) SKIPPED SS");
            return;
        }
        this.bitmap = MoBitmapUtils.createBitmapFromView(wv,
                0, 0);
        onBitmapCapturedListener.onBitmapCaptured(this.bitmap);
        MoLog.print("SCREENSHOT");
        setName(wv.getUrl());
    }

    /**
     * captures a bitmap from web view with delay
     * @param wv
     * @param delay
     */
    public void captureBitmapWithDelay(WebView wv,long delay){
        Handler h = new Handler();
        h.postDelayed(()->this.captureBitmap(wv),delay);
    }

    /**
     * if the web view is not loading, we capture a bitmap
     * @param wv
     */
    public void captureBitmapIfNotLoading(WebView wv){
        if(wv.getProgress()==100){
            captureBitmap(wv);
            MoLog.print("TAKE_PIC 100");
        }else{
            MoLog.print("NO_PIC " + wv.getProgress());
        }
    }

    /**
     * makes sure that we capture the bitmap
     * even if the name of the new bitmap and the
     * previous one is the same
     * @param wv web view
     */
    public void forceCaptureBitmapIfNotLoading(WebView wv){
        boolean save = optimized;
        optimized = false;
        captureBitmapIfNotLoading(wv);
        optimized = save;
    }


    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] com = MoFile.loadable(data);
        this.id = Long.parseLong(com[0]);
        if(com.length>1){
            this.name = com[1];
        }

        // loading the bitmap
        MoBitmapSaver saver = new MoBitmapSaver(context)
                .setDirectoryName(MoTabsManager.TAB_BITMAP_DIRECTORY)
                .setExternal(false)
                .setFileName(this.id+"");
        this.bitmap = saver.load();
        updateBitmapIdTracker();
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(id,name);
    }


    public static long getNextId(){
        bitmapIdTracker++;
        return bitmapIdTracker;
    }

}
