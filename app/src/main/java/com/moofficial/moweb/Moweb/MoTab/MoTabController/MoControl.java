package com.moofficial.moweb.Moweb.MoTab.MoTabController;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoTab.MoTabId.MoTabId;


public class MoControl implements MoSavable, MoLoadable {



    private MoTabId tabId = new MoTabId();


    public MoControl(String data,Context c){
        this.load(data,c);
    }

    public MoControl() {}




    // construction not working as intended
    public void notifyRemovedIndex(){
//        if(this.index > 0) {
//            // only decrement it if it is higher than 0
//            // else it is set to zero and handled by other classes
//            this.index--;
//        }
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        tabId.load(data,context);
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
