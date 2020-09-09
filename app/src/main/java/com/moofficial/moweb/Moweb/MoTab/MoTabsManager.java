package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.util.LongSparseArray;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoDir;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabExceptions.MoTabNotFoundException;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoIncognitoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoTabsManager {

    private static final String SEP_KEY = "&siugsiojga0123948&";
    private static final String TABS_FILE = "tabs_file";
    public static final String TAB_BITMAP_DIRECTORY = "tab_bitmap_dir";
    public static final String TAB_DIR = "mo_tab_dir";


    private static List<MoTab> tabs = new ArrayList<>();
    private static List<MoTab> privateTabs = new ArrayList<>();
    private static LongSparseArray<MoTab> tabSparseArray = new LongSparseArray<>();

    public static List<MoTab> getTabs(){
        return tabs;
    }
    public static List<MoTab> getPrivateTabs(){
        return privateTabs;
    }


    /**
     * creates a normal tab
     * @param context
     * @param url
     */
    public static MoTab newTab(Context context, String url, MoTab parentTab){
        MoTab tab = new MoTab(context,url);
        tab.setParentTab(parentTab);
        tabs.add(tab);
        addToSparseArray(tab);
        return tab;
    }

    /**
     * creates a new incognito tab
     * and adds it to the list
     * @param a context of the place
     * @param url url to put as the initial tab url
     */
    public static MoTab newPrivateTab(Context a, String url, MoTab parentTab){
        MoIncognitoTab t = new MoIncognitoTab(a,url);
        t.setParentTab(parentTab);
        privateTabs.add(t);
        addToSparseArray(t);
        return t;
    }


    /**
     * adds the tab to the sparse array based on it's id
     * we should not have duplicate id for any
     * tab
     * @param t tab to be added inside sparse array
     */
    public static void addToSparseArray(MoTab t) {
        if(tabSparseArray.get(t.getId())!=null){
            MoLog.print("ERRRRRRRRRRRRRRRRRRRRORRRRRRRRRRRRRRRRRRRR SPARSE ARRAY HAS THIS ID");
        }
        tabSparseArray.put(t.getId(),t);
    }

    /**
     * returns the tab with the id
     * associated with it
     * @param id of the tab
     * @return tab with corresponding id
     */
    public static MoTab getTab(long id){
        return tabSparseArray.get(id);
    }

    // size of normal tabs
    public static int size() {
        return tabs.size();
    }
    // size of incognito tabs
    public static int sizePrivate(){
        return privateTabs.size();
    }

    /**
     *
     * @param type of the tab you want to get
     * @return the last tab based on the type that is passed
     * it will return null if there is no more tab of that type
     */
    public static MoTab getLastTab(@MoTabType.TabType int type){
        switch (type){
            case MoTabType.TYPE_NORMAL:
                return tabs.isEmpty()?null:tabs.get(tabs.size()-1);
            case MoTabType.TYPE_PRIVATE:
                return privateTabs.isEmpty()?null:privateTabs.get(privateTabs.size()-1);
        }
        return null;
    }

    /**
     * saving tabs data into a file and reloading them when they launch the app again
     * @param context
     */
    public static void save(Context context) {
        try {
            MoFileManagerUtils.write(context,tabs);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * reloads the tabs if they have been not loaded
     * @param context
     */
    public static void load(Context context) {
        if(tabs == null || tabs.isEmpty()){
           reload(context);
        }
        MoLog.print("number of tabs: " + tabs.size());
        MoLog.print("sparse of tabs: " + tabSparseArray.size());
    }

    /**
     * reloads the tabs
     */
    @SuppressWarnings("SynchronizeOnNonFinalField")
    private synchronized static void reload(Context context) {
        MoTab[] retainOrder = new MoTab[MoDir.getFilesSizeDir(context,TAB_DIR)];
        MoFileManager.readAllDirFilesAsync(context, TAB_DIR, (s, i) -> {
            MoTab tab = new MoTab(context);
            tab.load(s, context);
            synchronized (retainOrder) {
                retainOrder[i] = tab;
            }
            synchronized (tabSparseArray) {
                addToSparseArray(tab);
            }
        });
        tabs = Arrays.asList(retainOrder);
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
        return privateTabs.isEmpty()?null: privateTabs.get(index);
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
     * deletes the tab that is passed,
     * we also remove it from its parent
     * list and notify the controller that
     * something has been removed
     * @param t
     */
    public static void delete(MoTab t) {
        t.deleteTab();
        switch (t.getType()){
            case MoTabType.TYPE_NORMAL:
                tabs.remove(t);
                break;
            case MoTabType.TYPE_PRIVATE:
                privateTabs.remove(t);
                break;
        }
        MoTabController.instance.notifyTabRemoved(t);
    }







    /**
     * adds a normal tab with a parent
     * @param context
     * @param url
     */
    public static void addTab(Context context, String url,boolean getCurrentAsParent) {
        setCurrentTab(context,MoTabsManager.newTab(context,
                url,getCurrentAsParent?MoTabController.instance.getCurrent():null));
    }




    /**
     * adds incognito tab with parent tab
     * @param c context of app
     * @param url url to open the new private tab in
     */
    public static void addPrivateTab(Context c, String url, boolean getCurrentAsParent){
        setCurrentTab(c,MoTabsManager.newPrivateTab(c,url,
                getCurrentAsParent?MoTabController.instance.getCurrent():null));
    }


    /**
     *
     * @param context
     * @param t
     */
    static void setCurrentTab(Context context,MoTab t) {
        MoTabController.instance.setNewTab(context,t);
    }


    /**
     * selects the tab to be the one that the user
     * is currently viewing
     * @param tab
     */
    public static void selectTab(Context c,MoTab tab) {
        MoTabController.instance.setNewTab(c,tab);
    }


    /**
     * returns the index of tab based on its type
     * @param tab
     * @return
     * @throws MoTabNotFoundException
     */
    public static int getIndexOf(MoTab tab) throws MoTabNotFoundException {
        if(tab == null)
            throw new MoTabNotFoundException();
        switch (tab.getType()){
            case MoTabType.TYPE_PRIVATE:
                return getIndexOf(tab, privateTabs);
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
    private static int getIndexOf(MoTab t, List<MoTab> ts) throws MoTabNotFoundException{
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
        for(MoTab t:tabs) {
            delete(t);
        }
        save(context);
        Toast.makeText(context,context.getString(R.string.toast_closed_all_normal_tabs),Toast.LENGTH_SHORT).show();
    }

    /**
     * removes and deletes the files for
     * the selected mo tabs
     * @param selected selected tabs to be removed
     */
    public static void removeSelectedTabs(List<MoTab> selected){
        for(MoTab t: selected) {
            delete(t);
        }
    }

    /**
     * removes all the incognito tabs
     * from our database
     */
    public static void clearAllPrivateTabs(Context context){
        for(MoTab t: privateTabs){
            delete(t);
        }
        Toast.makeText(context,context.getString(R.string.toast_closed_all_incognito_tabs),Toast.LENGTH_SHORT).show();
    }



    /**
     * calls on destroy for every
     * tab that exists both private
     * and normal tabs
     * (so that the web views are destroyed
     * and no memory leak is possible)
     */
    public static void onDestroy(){
        for(MoTab t : tabs){
            t.onDestroy();
        }
        for(MoTab t: privateTabs){
            t.onDestroy();
        }
        tabs = null;
        privateTabs = null;
    }


}
