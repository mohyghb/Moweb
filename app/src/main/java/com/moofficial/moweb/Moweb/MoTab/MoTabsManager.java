package com.moofficial.moweb.Moweb.MoTab;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;
import com.moofficial.moweb.Moweb.MoBitmap.MoBitmapSaver;

import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabExceptions.MoTabNotFoundException;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoIncognitoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.util.ArrayList;

public class MoTabsManager {

    private static final String SEP_KEY = "&siugsiojga0123948&";
    private static final String TABS_FILE = "tabs_file";
    public static final String TAB_BITMAP_DIRECTORY = "tab_bitmap_dir";

    private static ArrayList<MoTab> tabs = new ArrayList<>();
    private static ArrayList<MoTab> incognitoTabs = new ArrayList<>();
    private static MoTabRecyclerAdapter tabRecyclerAdapter,incognitoTabAdapter;

    public static ArrayList<MoTab> getTabs(){
        return tabs;
    }
    public static ArrayList<MoTab> getIncognitoTabs(){
        return incognitoTabs;
    }
    public static MoTabRecyclerAdapter getTabRecyclerAdapter() {
        return tabRecyclerAdapter;
    }
    public static void setTabRecyclerAdapter(MoTabRecyclerAdapter tabRecyclerAdapter) {
        MoTabsManager.tabRecyclerAdapter = tabRecyclerAdapter;
    }
    public static MoTabRecyclerAdapter getIncognitoTabAdapter() {
        return incognitoTabAdapter;
    }
    public static void setIncognitoTabAdapter(MoTabRecyclerAdapter incognitoTabAdapter) {
        MoTabsManager.incognitoTabAdapter = incognitoTabAdapter;
    }


    /**
     * creates a normal tab
     * @param context
     * @param url
     */
    private static void newTab(Context context, String url, MoTab parentTab){
        MoTab tab = new MoTab(context,url);
        tab.setParentTab(parentTab);
        tabs.add(tab);
    }

    /**
     * creates a new incognito tab
     * @param a
     * @param url
     */
    private static void newIncognitoTab(Activity a, String url, MoTab parentTab){
        MoIncognitoTab t = new MoIncognitoTab(a,url);
        t.setParentTab(parentTab);
        incognitoTabs.add(t);
    }



    // size of normal tabs
    public static int size() {
        return tabs.size();
    }
    // size of incognito tabs
    public static int sizeIncognito(){
        return incognitoTabs.size();
    }


    /**
     * saving tabs data into a file and reloading them when they launch the app again
     * @param context
     */
    public static void save(Context context) {
        String data = MoFile.getData(tabs);
        MoReadWrite.saveFile(TABS_FILE,data,context);
        // save the bitmaps of the tabs
        saveBitmapsOfTabs(context);
    }


    /**
     * saves the bitmap preview of the webviews for later use
     * @param context
     */
    private static void saveBitmapsOfTabs(Context context){
        MoBitmapSaver saver = new MoBitmapSaver(context)
                .setExternal(false)
                .setDirectoryName(TAB_BITMAP_DIRECTORY);
        for(MoTab t: tabs){
            t.saveWebViewBitmap(saver);
        }
    }

    /**
     * reloads the tabs if they have been not loaded
     * @param context
     */
    public static void load(Context context) {
        tabs.clear();
        // TODO UNCOMMENT THE RELOAD LINE
        //if(tabs.isEmpty()){
           reload(context);
        //}
    }

    /**
     * reloads the tabs
     */
    private static void reload(Context context) {
        tabs = new ArrayList<>();
        String data = MoReadWrite.readFile(TABS_FILE,context);
        String[] components = MoFile.loadable(data);
        if(MoFile.isValidData(components)){
            String[] tbs = MoFile.loadable(components[0]);
            for(String t: tbs){
                if(!t.isEmpty()) {
                    try{
                        MoTab tab = new MoTab(context);
                        tab.load(t, context);
                        tabs.add(tab);
                    }catch(Exception e){
                        Toast.makeText(context,"Caught exception while trying to load tabs",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }


    /**
     * returns the motab at index
     * returns null if the tabs are empty
     * @param index of the motab
     * @return the motab at index
     */
    public static MoTab getTab(int index){
        return tabs.isEmpty()?null:tabs.get(index);
    }

    /**
     * returns the incognito tab at index
     * return null if the incognito tabs are empty
     * @param index
     * @return
     */
    public static MoTab getIncognitoTab(int index){
        return incognitoTabs.isEmpty()?null:incognitoTabs.get(index);
    }


    /**
     *
     * @returns whether there is any tabs left
     */
    public static boolean isEmpty(){
        return tabs.isEmpty();
    }

    /**
     *
     * @returns whether tabs list is not empty or not
     */
    public static boolean isNotEmpty(){
        return !tabs.isEmpty();
    }


    /**
     * remove the tab from the list
     * notify the recycler adapter, so they know that an item was removed
     * and notify the tab controller so that it identifies a tab has been removed
     * and manage the index properly
     * @param index of the tab to be removed from tabs
     * @param context  of the activity
     */
    private static void remove(int index,Context context,int type) {
        switch (type){
            case MoTabType.TYPE_NORMAL:
                removeTabStuff(index,context,tabs);
                notifyItemRemoved(index,tabRecyclerAdapter);
                break;
            case MoTabType.TYPE_INCOGNITO:
                removeTabStuff(index,context,incognitoTabs);
                notifyItemRemoved(index,incognitoTabAdapter);
                break;
        }
        MoTabController.instance.notifyRemoved(type);
    }

    /**
     * removes the tab bitmap previews that were
     * previously saved on the internal storage
     * we need to remove them so they don't take space as
     * they continue to search in our app
     * @param index
     * @param context
     * @param tabs
     */
    private static void removeTabStuff(int index,Context context,ArrayList<MoTab> tabs){
        // deleting the bitmap used to show user
        tabs.get(index).deleteWebViewBitmap(
                new MoBitmapSaver(context)
                        .setDirectoryName(TAB_BITMAP_DIRECTORY)
                        .setExternal(false)
        );
        // removing it from tabs list
        tabs.remove(index);
    }

    /**
     * remove the tab from the list
     * @param index of the tab to be removed from tabs
     * @param context needed in order to save the changes onto a file
     */
    public static void remove(Context context,int index, int type) {
        remove(index,context,type);
        //save(context);
    }




    /**
     * adds a normal tab with a parent
     * @param context
     * @param url
     */
    public static void addTab(Context context, String url,boolean getCurrentAsParent) {
        MoTabsManager.newTab(context, url,getCurrentAsParent?MoTabController.instance.getCurrent():null);
        int index = tabs.size() - 1;
        MoTabController.instance.setIndex(index,MoTabType.TYPE_NORMAL);
        notifyItemInserted(index, tabRecyclerAdapter);
    }


    /**
     * adds incognito tab with parent tab
     * @param a
     * @param url
     */
    public static void addIncognitoTab(Activity a,String url,boolean getCurrentAsParent){
        MoTabsManager.newIncognitoTab(a,url,getCurrentAsParent?MoTabController.instance.getCurrent():null);
        int index = incognitoTabs.size() - 1;
        MoTabController.instance.setIndex(index,MoTabType.TYPE_INCOGNITO);
        notifyItemInserted(index, incognitoTabAdapter);
    }




    /**
     * selects the tab to be the one that the user
     * is currently viewing
     * @param tab
     * @throws MoTabNotFoundException
     */
    public static void selectTab(MoTab tab) throws MoTabNotFoundException {
        MoTabController.instance.setIndex(getIndexOf(tab),tab.getType());
    }


    /**
     * returns the index of tab based on its type
     * @param tab
     * @return
     * @throws MoTabNotFoundException
     */
    private static int getIndexOf(MoTab tab) throws MoTabNotFoundException {
        switch (tab.getType()){
            case MoTabType.TYPE_INCOGNITO:
                return getIndexOf(tab,incognitoTabs);
            case MoTabType.TYPE_NORMAL:
                return getIndexOf(tab,tabs);
        }
        throw new MoTabNotFoundException();
    }

    /**
     * returns the index of tab from a list of tabs
     * @param t
     * @param ts
     * @return
     * @throws MoTabNotFoundException
     */
    private static int getIndexOf(MoTab t, ArrayList<MoTab> ts) throws MoTabNotFoundException{
        for(int i =0; i < ts.size(); i++){
            if(t==ts.get(i)){
                return i;
            }
        }
        throw new MoTabNotFoundException();
    }

    /**
     * removes all the normal tabs
     * from our database
     */
    public static void clearAllNormalTabs(Context context){
        for(int i = tabs.size()-1; i>=0;i--){
            remove(i,context,MoTabType.TYPE_NORMAL);
        }
        Toast.makeText(context,context.getString(R.string.toast_closed_all_normal_tabs),Toast.LENGTH_SHORT).show();
    }

    /**
     * removes all the incognito tabs
     * from our database
     */
    public static void clearAllIncognitoTabs(Context context){
        for(int i = incognitoTabs.size()-1; i>=0;i--){
            remove(i,context,MoTabType.TYPE_INCOGNITO);
        }
        Toast.makeText(context,context.getString(R.string.toast_closed_all_incognito_tabs),Toast.LENGTH_SHORT).show();
    }


    // tab recycler methods

    /**
     * notifies the recycler adapter that an item has been inserted
     * @param index of the item
     * @param tabRecyclerAdapter corresponding adapter
     */
    private static void notifyItemInserted(int index, MoTabRecyclerAdapter tabRecyclerAdapter) {
        if (tabRecyclerAdapter != null) {
            tabRecyclerAdapter.notifyItemInserted(index);
        }
    }

    /**
     * notifies the recycler adapter that an item has been removed
     * @param index of the item
     * @param tabRecyclerAdapter corresponding adapter
     */
    private static void notifyItemRemoved(int index, MoTabRecyclerAdapter tabRecyclerAdapter) {
        if (tabRecyclerAdapter != null) {
            tabRecyclerAdapter.notifyItemRemoved(index);
        }
    }




}
