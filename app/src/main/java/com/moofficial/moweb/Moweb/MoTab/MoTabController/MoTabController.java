package com.moofficial.moweb.Moweb.MoTab.MoTabController;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;
import com.moofficial.moweb.MoSection.MoSectionManager;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import static com.moofficial.moweb.MoSection.MoSectionManager.IN_TAB_VIEW;

// designed to know where the current tab is
public class MoTabController implements MoSavable, MoLoadable {

    private static final String SEP_KEY = "&7&as&ifuyfd&";
    private static final String FILE_NAME = "mo_main_tab_file";



    private MoControl currentTabControl;
    private MoControl incognitoTabsControl;
    private MoControl normalTabsControl;

    private Context context;
    private Runnable changeContentView;
    private Runnable tabsButtonPressed;


    public static MoTabController instance;



    public static void init(Context context, Runnable ccv, Runnable tbp){
        instance = new MoTabController(context,ccv,tbp);
    }

    MoTabController(Context context, Runnable ccv, Runnable tbp){
        this.context = context;
        this.changeContentView = ccv;
        this.tabsButtonPressed = tbp;
        initTabControllers();
    }

    private void initTabControllers(){
        this.incognitoTabsControl = new MoControl(0,MoTabType.TYPE_INCOGNITO);
        this.normalTabsControl = new MoControl(0,MoTabType.TYPE_NORMAL);
        this.currentTabControl = this.normalTabsControl;
    }




//    public int getIndex() {
//        return index;
//    }

    public void onTabsButtonPressed() {
        this.tabsButtonPressed.run();
    }



    /**
     * when you change the index that means you are changing the tab
     * so call the all tabs button pressed with (false)
     * @param index
     */
    public void setIndex(int index, int type) {
        saveCurrentTabState(index, type);
        this.setType(type);
        this.currentTabControl.setIndex(index);
        // save the changes
        save(this.context);
        // change the section to show the in tab view
        MoSectionManager.getInstance().setSection(IN_TAB_VIEW);
        changeContentView.run();
    }

    /**
     * pauses the current tab if the type of the new
     * selected tab is different
     * or if the index of the new selected tab is different
     * since we want to be able to show the web preview of all tabs
     * if we just call web view onPause, the web view becomes a solid
     * grey color, so we call onPause to stop any ongoing activity like playing
     * music or video or other... and then call resume to bring back the web view
     * and show a preview of it
     * @param index
     * @param type
     */
    private void saveCurrentTabState(int index, int type) {
        MoTab t = getCurrent();
        if(t!=null){
            // if the previous tab is not the current one
            // then we should call on pause
            if(index!=this.currentTabControl.getIndex() || t.getType()!=type){
                t.onPause();
                // we want to pause all the activities inside the web view
                // but we also want to be able to show it to user about what is happening
                t.onResume();
                // we also want to save a preview by taking screenshot of the
                // web view and reusing the bitmap if they close the app
                // so we can show it later to the user
                t.captureAndSaveWebViewBitmapAsync();
            }
        }
    }

    private void setType(int t){
        MoTabType.runForMultipleTypes(t,()->setCurrentTabControl(this.normalTabsControl),
                ()->setCurrentTabControl(this.incognitoTabsControl));
    }

    private void setCurrentTabControl(MoControl currentTabControl) {
        this.currentTabControl = currentTabControl;
    }

    /**
     *
     * @returns true if the normal tabs are empty and the current controller is
     * a normal controller, or if the incognito tabs are empty and the current controller is
     * incognito controller
     */
    public boolean isOutOfOptions(){
        return (MoTabsManager.size() == 0 && this.currentTabControl.getType() == MoTabType.TYPE_NORMAL)
                ||
                (MoTabsManager.sizeIncognito() == 0 && this.currentTabControl.getType() == MoTabType.TYPE_INCOGNITO);
    }

    /**
     *  this function makes sure that if the removed index is same
     *  as the controller index, then the controller index needs to be changed
     */
    public void notifyRemoved(int type){
        MoTabType.runForMultipleTypes(type,()->this.normalTabsControl.notifyRemovedIndex(),
                ()->this.incognitoTabsControl.notifyRemovedIndex());
    }


    public MoTab getCurrent(){
        switch (currentTabControl.getType()){
            case MoTabType.TYPE_INCOGNITO:
                setZeroIndexIfNotInBound(MoTabsManager.sizeIncognito());
                return MoTabsManager.getIncognitoTab(currentTabControl.getIndex());
                case MoTabType.TYPE_NORMAL:
                    setZeroIndexIfNotInBound(MoTabsManager.size());
                    return MoTabsManager.getTab(currentTabControl.getIndex());
        }
        return null;
    }

    private boolean notInBound(int size){
        return currentTabControl.getIndex() >= size || currentTabControl.getIndex() < 0;
    }
    private void setZeroIndexIfNotInBound(int size){
        if(notInBound(size)){
            currentTabControl.setIndex(0);
        }
    }



    // returns the normal control index
    public int getNormalIndex(){
        return this.normalTabsControl.getIndex();
    }

    // returns the incognito control index
    public int getIncognitoIndex(){
        return this.incognitoTabsControl.getIndex();
    }


    public void onPause(){
        if(isOutOfOptions()){
            return;
        }
        getCurrent().onPause();
    }

    public void onResume(){
        if(isOutOfOptions()){
            return;
        }
        getCurrent().onResume();
    }

    public void onDestroy(){
        if(isOutOfOptions()){
            return;
        }
        getCurrent().onDestroy();
    }


    /**
     * loads a savable object into its class
     *
     * @param data not applicable in this case
     * @param context gotten from the constructor
     */
    @Override
    public void load(String data, Context context) {
        data = MoReadWrite.readFile(FILE_NAME,context);
        try{
            String[] com = MoFile.loadable(data);
            this.incognitoTabsControl = new MoControl(com[0],context);
            this.normalTabsControl = new MoControl(com[1],context);
            this.currentTabControl = this.normalTabsControl;
        }catch(Exception e){
            initTabControllers();
        }
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(incognitoTabsControl.getData(),normalTabsControl.getData());
    }

    public void save(Context context){
        MoReadWrite.saveFile(FILE_NAME,getData(),context);
    }
}
