package com.moofficial.moweb.Moweb.MoTab.MoTabController;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoContext.MoContext;
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
public class MoTabController extends MoContext implements MoSavable, MoLoadable {

    private static final String SEP_KEY = "&7&as&ifuyfd&";
    private static final String FILE_NAME = "mo_main_tab_file";



    private MoControl currentTabControl;
    private MoControl incognitoTabsControl;
    private MoControl normalTabsControl;


    private Runnable changeContentView;
    private Runnable tabsButtonPressed;


    public static MoTabController instance;



    public static void init(Context context, Runnable ccv, Runnable tbp){
        instance = new MoTabController(context,ccv,tbp);
    }

    MoTabController(Context context, Runnable ccv, Runnable tbp){
        super(context);
        this.changeContentView = ccv;
        this.tabsButtonPressed = tbp;
        initTabControllers();
    }

    private void initTabControllers(){
        this.incognitoTabsControl = new MoControl(0,MoTabType.TYPE_PRIVATE);
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
     * @return true if the normal tabs are empty and the current controller is
     * a normal controller, or if the incognito tabs are empty and the current controller is
     * incognito controller
     */
    public boolean isOutOfOptions(){
        return (MoTabsManager.size() == 0 && this.currentTabControl.getType() == MoTabType.TYPE_NORMAL)
                ||
                (MoTabsManager.sizePrivate() == 0 && this.currentTabControl.getType() == MoTabType.TYPE_PRIVATE);
    }

    /**
     *
     * @return if the opposite of the above function
     * is true
     */
    public boolean isNotOutOfOptions(){
        return !isOutOfOptions();
    }

    /**
     *  this function makes sure that if the removed index is same
     *  as the controller index, then the controller index needs to be changed
     */
    public void notifyRemoved(int type){
        MoTabType.runForMultipleTypes(type,()->this.normalTabsControl.notifyRemovedIndex(),
                ()->this.incognitoTabsControl.notifyRemovedIndex());
    }

    /**
     * notifies that something has changed inside
     * the recycler view about the current tab
     */
    public void notifyCurrentTabInRecycler(){
        MoTab t = getCurrent();
        if(t!=null){
            t.getNotifyTabChanged().notifyTabChanged();
        }
    }

    public MoTab getCurrent(){
        switch (currentTabControl.getType()){
            case MoTabType.TYPE_PRIVATE:
                setZeroIndexIfNotInBound(MoTabsManager.sizePrivate());
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


    /**
     * opens the search string inside the current tab if possible
     * or it opens a new tab if the boolean is true
     * @param search search string to be opened inside a new tab
     * @param createNewTabIfNoTabExists if no current tab exists, we open it inside
     *                                  a new tab if this param is true
     */
    public void openUrlInCurrentTab(String search,boolean createNewTabIfNoTabExists){
        MoTab t = getCurrent();
        if(t!=null){
            // open the url inside the t
            t.search(search);
        } else if(createNewTabIfNoTabExists){
            // create a new tab
            MoTabsManager.addTab(this.context,search,false);
        }
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
