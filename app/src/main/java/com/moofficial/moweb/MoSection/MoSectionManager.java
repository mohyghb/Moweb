package com.moofficial.moweb.MoSection;

import android.content.Context;

import com.moofficial.moweb.MoIO.MoLoadable;
import com.moofficial.moweb.MoIO.MoSavable;
import com.moofficial.moweb.MoReadWrite.MoReadWrite;


public class MoSectionManager implements MoSavable, MoLoadable {

    private final String FILE_NAME = "sectionmanager";

    // showing one tab only
    public static final int IN_TAB_VIEW = 0 ;
    // showing all the tabs
    public static final int TABS_VIEW = 1;

    private int section;


    private static MoSectionManager ourInstance = new MoSectionManager();

    public static MoSectionManager getInstance() {
        return ourInstance;
    }

    private MoSectionManager() {
        this.section = IN_TAB_VIEW;
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String value = MoReadWrite.readFile(FILE_NAME,context);
        this.section = Integer.parseInt(value);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return this.section+"";
    }


    public void save(Context context) {
        MoReadWrite.saveFile(FILE_NAME,this.getData(),context);
    }


    public int getSection() {
        return section;
    }

    // returns true if section == s;
    public boolean is(int s){
        return this.section == s;
    }

    public void setSection(int section) {
        this.section = section;
    }

    public void onDestroy(){
        ourInstance = null;
        ourInstance = new MoSectionManager();
    }
}
