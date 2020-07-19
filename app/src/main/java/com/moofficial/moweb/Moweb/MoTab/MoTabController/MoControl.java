package com.moofficial.moweb.Moweb.MoTab.MoTabController;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;


public class MoControl implements MoSavable, MoLoadable {

    private final String SEP_KEY = "aos4idugfodpkmknj58329";



    private int index;
    private int type;

    public MoControl(int index, int type) {
        this.index = index;
        this.type = type;
    }

    public MoControl(String data,Context c){
        this.load(data,c);
    }

    public MoControl() {}

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public void notifyRemovedIndex(){
        if(this.index > 0) {
            // only decrement it if it is higher than 0
            // else it is set to zero and handled by other classes
            this.index--;
        }
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
        this.index = Integer.parseInt(com[0]);
        this.type = Integer.parseInt(com[1]);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(index,type);
    }
}
