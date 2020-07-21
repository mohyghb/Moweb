package com.moofficial.moweb.Moweb.MoHomePage;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;

import java.util.ArrayList;

public class MoHomePageManager {

    private static final int NONE_ACTIVATED = -1;
    private static final String FILE_NAME = "homepagefile";
    private static ArrayList<MoHomePage> homePages = new ArrayList<>();
    private static Integer activeHomePageIndex = NONE_ACTIVATED;

    //TODO: test the correctness of active home page index;

    /**
     *
     * @param url
     * @return true if this was added, false if it already was in the list
     */
    public static boolean add(Context context,String url){
        MoHomePage p = new MoHomePage(url);
        if(!homePages.contains(p)){
            homePages.add(p);
            activateOneIfNoneIsActivated();
            save(context);
            return true;
        }
        return false;
    }

    /**
     * activates the home page at [index]
     * @param index
     */
    public static void activate(Context c,int index){
        activeHomePageIndex = index;
        for(int i = 0; i < homePages.size(); i++){
            if(i == index){
                homePages.get(i).setActivated(true);
            }else{
                homePages.get(i).setActivated(false);
            }
        }
        save(c);
    }

    /**
     *
     */
    public static void activateOneIfNoneIsActivated(){
        if(activeHomePageIndex == NONE_ACTIVATED || activeHomePageIndex >= homePages.size()){
            activeHomePageIndex = homePages.size()-1;
        }
    }

    /**
     * deletes all the selected home pages
     * @param context
     */
    public static void deleteAllSelected(Context context){
        for(int i = homePages.size()-1;i>=0;i--){
            if(homePages.get(i).isSelected()){
                homePages.remove(i);
            }
        }
        activateOneIfNoneIsActivated();
        save(context);
    }

    /**
     *
     * @param context
     * @param position
     */
    public static void remove(Context context,int position){
        homePages.remove(position);
        save(context);
    }

    public static void clear(Context context){
        homePages.clear();
        save(context);
    }

    /**
     *
     * @param context
     */
    public static void save(Context context){
        MoReadWrite.saveFile(FILE_NAME, MoFile.getData(homePages,activeHomePageIndex),context);
    }

    /**
     * loads back in the active index
     * @param context
     */
    public static void load(Context context){
        if(homePages.isEmpty()){
            String[] data = MoFile.loadable(MoReadWrite.readFile(FILE_NAME,context));
            if(MoFile.isValidData(data)){
                activeHomePageIndex = Integer.parseInt(data[1]);
                String[] hps = MoFile.loadable(data[0]);
                for(String h: hps){
                    if(!h.isEmpty()){
                        try{
                            homePages.add(new MoHomePage(h,context));
                        }catch (Exception ignore){}
                    }
                }
                activateOneIfNoneIsActivated();
            }
        }
    }


    /**
     *
     * @return
     */
    public static ArrayList<MoHomePage> get(){
        return homePages;
    }

}
