package com.moofficial.moweb.Moweb.MoTab.MoTabController;

import android.content.Context;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoTab.MoTabId.MoTabId;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.io.IOException;

// designed to know where the current tab is
public class MoTabController implements MoSavable, MoLoadable {

    private static final String FILE_NAME = "mo_main_tab_file";
    public static MoTabController instance = new MoTabController();


    private MoTabId currentTabId = new MoTabId();
    private MoTab currentTab;
    private @NonNull MoUpdateTabActivity updateTabActivity = () -> {};


    @NonNull
    public MoUpdateTabActivity getUpdateTabActivity() {
        return updateTabActivity;
    }

    public MoTabController setUpdateTabActivity(@NonNull MoUpdateTabActivity updateTabActivity) {
        this.updateTabActivity = updateTabActivity;
        return this;
    }

    /**
     * saves the state of the current (previous) tab
     * sets the new tab to the current tab
     * updates the id of the current tab
     * saves the changes
     * @param context of the app
     * @param newTab to be set as the current tab
     */
    public void setNewTab(Context context,MoTab newTab) {
        saveCurrentTabState(newTab);
        updateCurrent(newTab);
        save(context);
        updateTabActivity.update();
    }

    /**
     * updates the current tab
     * to the new tab that is passed
     * @param t tab
     */
    private void updateCurrent(MoTab t){
        currentTab = t;
        if(currentTab!=null){
            currentTabId = t.getTabId();
        }
    }

    public MoTab getCurrent() {
        return this.currentTab;
    }

    /**
     *
     * @return true if the normal tabs are empty and the current controller is
     * a normal controller, or if the incognito tabs are empty and the current controller is
     * incognito controller
     */
    public boolean isOutOfOptions() {
        return this.currentTab == null;
    }

    /**
     *
     * @return if the opposite of the above function
     * is true
     */
    public boolean isNotOutOfOptions(){
        return !isOutOfOptions();
    }

    public boolean currentIs(MoTab t){
        return currentTab!=null && currentTab.equals(t);
    }

    /**
     *  this function makes sure that if the removed index is same
     *  as the controller index, then the controller index needs to be changed
     *  also find the current tab again because the current tab
     *  might have been removed
     */
    public void notifyTabRemoved(MoTab t){
        if(t.equals(currentTab)) {
            // then we are removing the current tab
            // change the current tab to the last item inside
            // the tab array or null if none exist
            updateCurrent(MoTabsManager.getLastTab(t.getType()));
        }
    }


    /**
     //     * pauses the current tab if the type of the new
     //     * selected tab is different
     //     * or if the index of the new selected tab is different
     //     * since we want to be able to show the web preview of all tabs
     //     * if we just call web view onPause, the web view becomes a solid
     //     * grey color, so we call onPause to stop any ongoing activity like playing
     //     * music or video or other... and then call resume to bring back the web view
     //     * and show a preview of it
     //     * @param newTab new tab that is going to be open
     //     */
    private void saveCurrentTabState(MoTab newTab) {
        if(currentTab!=null){
            // if the previous tab is not the current one
            // then we should call on pause
            if(!currentTab.equals(newTab)){
                currentTab.onPause();
                // we want to pause all the activities inside the web view
                // but we also want to be able to show it to user about what is happening
                currentTab.onResume();
                // we also want to save a preview by taking screenshot of the
                // web view and reusing the bitmap if they close the app
                // so we can show it later to the user
                //currentTab.captureAndSaveWebViewBitmapAsync();
            }
        }
    }

    /**
     * opens the search string inside the current tab if possible
     * or it opens a new tab if the boolean is true
     * @param search search string to be opened inside a new tab
     * @param createNewTabIfNoTabExists if no current tab exists, we open it inside
     *                                  a new tab if this param is true
     */
    public void openUrlInCurrentTab(Context context,String search,boolean createNewTabIfNoTabExists){
        if(currentTab!=null){
            // open the url inside the t
            currentTab.search(search);
        } else if(createNewTabIfNoTabExists){
            // create a new tab
            MoTabsManager.addTab(context,search,false);
        }
    }


    // when the tab controller should be pause
    public void onPause() {
        if(isOutOfOptions()) {
            return;
        }
        currentTab.onPause();
    }

    // when the tab controller should be resumed
    public void onResume() {
        if(isOutOfOptions()) {
            return;
        }
        currentTab.onResume();
    }

    // when the tab controller should be destroyed
    public void onDestroy() {
        if(isOutOfOptions()) {
            return;
        }
        currentTab.onDestroy();
    }

    @Override
    public void load(String s, Context context) {
        try {
            s = MoFileManager.readInternalFile(context,FILE_NAME);
            if(s.isEmpty())
                return;
            this.currentTabId.load(s,context);
            this.currentTab = MoTabsManager.getTab(currentTabId.getId());
            if(this.currentTab == null && MoTabsManager.size()>0){
                // we need to assign it since we are not outta options
                updateCurrent(MoTabsManager.getLastTab(MoTabType.TYPE_NORMAL));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getData() {
        return currentTabId.getData();
    }

    public void save(Context context) {
        try {
            MoFileManager.writeInternalFile(context,FILE_NAME,getData());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
