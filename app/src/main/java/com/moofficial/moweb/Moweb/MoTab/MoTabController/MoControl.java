package com.moofficial.moweb.Moweb.MoTab.MoTabController;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoTab.MoTabId.MoTabId;


public class MoControl implements MoSavable, MoLoadable {


    private MoTabId tabId = new MoTabId();


    public MoControl(String data, Context c) {
        this.load(data, c);
    }

    public MoControl() {
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        tabId.load(data, context);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return tabId.getData();
    }
}
