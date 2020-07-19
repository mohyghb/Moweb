package com.moofficial.moweb.MoBitmap;

import android.content.Context;
import android.graphics.Bitmap;


import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moweb.MoId.MoId;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MoBitmap implements MoSavable, MoLoadable {

    private static final String SEP_KEY = "*sn*aiuga*";

    private MoId id;
    private Bitmap bitmap;


    public MoBitmap(){
        id = new MoId();
    }

    public MoBitmap(String data,Context context){
        this.load(data,context);
    }

    public MoId getId() {
        return id;
    }

    public void setId(MoId id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
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
        this.id = new MoId();
        this.id.load(com[0],context);
        // loading the bitmap
        MoBitmapSaver saver = new MoBitmapSaver(context)
                .setDirectoryName(MoTabsManager.TAB_BITMAP_DIRECTORY)
                .setExternal(false)
                .setFileName(this.id.getSId());
        this.bitmap = saver.load();
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(id.getData());
    }
}
