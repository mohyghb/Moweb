package com.moofficial.moweb.Moweb.MoTab.MoTabs;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoBitmap.MoBitmap;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShare;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;
import com.moofficial.moessentials.MoEssentials.MoUtils.MoKeyboardUtils.MoKeyboardUtils;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabBitmap.MoTabBitmap;
import com.moofficial.moweb.Moweb.MoTab.MoTabId.MoTabId;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabBookmarkChanged;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnUpdateUrlListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

import java.io.IOException;
import java.util.Objects;

public class MoTab implements MoFileSavable, MoLoadable, MoSelectableItem, MoSearchableItem {



    private MoTabId tabId = new MoTabId();
    private MoTab parentTab;
    protected MoWebView moWebView;
    private MoURL url;
    private MoBitmap moBitmap = new MoTabBitmap().setOptimized(false);
    private MoTabType tabType = new MoTabType(MoTabType.TYPE_NORMAL);
    private MoOnUpdateUrlListener onUpdateUrlListener = s -> {};
    private MoOnTabBookmarkChanged onTabBookmarkChanged = isBookmarked -> {};
    private Context context;
    private View errorLayout;
    private TextView searchText;
    private boolean captureImage = true;
    private boolean isSelected = false;
    private boolean wasInitAlready = false;
    private boolean isSearched = false;
    private boolean isUpToDate = false;


    public MoTab(Context context, String url) {
        initContextView(context);
        this.url = new MoURL(url);
        saveTab();
    }

    private void initContextView(Context context) {
        this.context = context;
        this.moWebView = new MoWebView(context);
    }

    public MoTab(String searchText,Context context){
        this(context,MoSearchEngine.instance.getURL(searchText));
    }

    // for loading back tabs from saved files
    public MoTab(Context context){
        initContextView(context);
    }

    public MoTab setCaptureImage(boolean b){
        this.captureImage = b;
        return this;
    }

    public void setParentTab(MoTab t){
        this.parentTab = t;
    }


    public MoTab getParentTab() {
        return parentTab;
    }

    public MoWebView getMoWebView() {
        return moWebView;
    }

    public MoTab setMoWebView(MoWebView moWebView) {
        this.moWebView = moWebView;
        return this;
    }

    public MoTab setUrl(MoURL url) {
        this.url = url;
        return this;
    }

    public MoTab setMoBitmap(MoBitmap moBitmap) {
        this.moBitmap = moBitmap;
        return this;
    }


    public MoOnTabBookmarkChanged getOnTabBookmarkChanged() {
        return onTabBookmarkChanged;
    }

    public MoTab setOnTabBookmarkChanged(MoOnTabBookmarkChanged onTabBookmarkChanged) {
        this.onTabBookmarkChanged = onTabBookmarkChanged;
        return this;
    }

    public boolean isUpToDate() {
        return isUpToDate;
    }

    public MoTab setUpToDate(boolean upToDate) {
        isUpToDate = upToDate;
        return this;
    }

    public MoTabType getTabType() {
        return tabType;
    }

    public MoTab setTabType(MoTabType tabType) {
        this.tabType = tabType;
        return this;
    }


    public MoOnUpdateUrlListener getOnUpdateUrlListener() {
        return onUpdateUrlListener;
    }

    public MoTab setOnUpdateUrlListener(MoOnUpdateUrlListener onUpdateUrlListener) {
        this.onUpdateUrlListener = onUpdateUrlListener;
        return this;
    }

    public View getErrorLayout() {
        return errorLayout;
    }

    public MoTab setErrorLayout(View errorLayout) {
        this.errorLayout = errorLayout;
        return this;
    }



    public MoTabId getTabId() {
        return tabId;
    }

    public MoTab setTabId(MoTabId tabId) {
        this.tabId = tabId;
        return this;
    }

    public MoBitmap getMoBitmap() {
        return moBitmap;
    }

    public Context getContext() {
        return context;
    }

    public MoTab setContext(Context context) {
        this.context = context;
        return this;
    }


    /**
     * init the whole class
     */
    public void init(){
        if(wasInitAlready)
            return;
        initWebView();
        wasInitAlready = true;
    }






    //@SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    protected void initWebView(){
        moWebView.setMoBitmap(this.moBitmap)
                 .setChromeClient(new MoChromeClient(this.context))
                 .setOnUpdateUrlListener((url, isReload) -> updateUrl(url))
                 .neverOverScroll()
                 .setOnErrorReceived((view, request, error) -> onErrorReceived(request, error));
        moWebView.init();
        // todo we need to set the web view for tab type
    }

    public void setProgressBar(ProgressBar p) {
        this.moWebView.getChromeClient().setProgressBar(p);
    }

    public MoTab setSearchText(TextView searchText) {
        this.searchText = searchText;
        return this;
    }

    /**
     * bookmarks or un-bookmarks this url
     * that the tab is currently showing
     */
    public void bookmarkTheTab(){
        boolean b = MoBookmarkManager.addOrRemoveIfWasAddedAlready(this.context,
                this.url.getUrlString(),this.moWebView.getTitle());
        this.onTabBookmarkChanged.onChanged(b);
    }

    /**
     *
     * @return true if the tab's url
     * is inside the bookmarked urls database
     */
    public boolean urlIsBookmarked(){
        return MoBookmarkManager.has(getUrl());
    }

    /**
     * shares the url text of this tab
     * with any other app
     */
    public void shareTheTab(){
        new MoShare().setText(this.url.getUrlString()).shareText(this.context);
    }


    /**
     * searches the current universal
     * home page for this tab
     */
    public void goToHomepage(){
        search(MoHomePageManager.getCurrentActivatedURL());
    }

    /**
     * when the user presses back on main screen
     * if there is a parent tab, and this web view can not go back, go to
     * that parent tab
     * @return true if the on back pressed method was consumed
     * by the tab and false if it did nothing
     */
    public boolean onBackPressed(){
        if(this.moWebView.canGoBack()) {
            this.moWebView.goBack();
        } else if(parentTab!=null) {
            // we can go back to the parent tab
            MoTabsManager.selectTab(context,this.parentTab);
        } else {
            return false;
        }
        return true;
    }


    /**
     * updates the textView of the url
     * as well as the url itself
     * @param u update this.url to u
     */
    public void updateUrl(String u){
        // update url
        this.url.setUrlString(u);
        if(searchText!=null){
            // remove focus from search
            this.searchText.clearFocus();
            // set the text for url
            this.searchText.setText(url.getUrlString());
            // hide keyboard
            MoKeyboardUtils.hideSoftKeyboard(this.searchText);
        }
        // save the tab
        saveTab();
        // calling the listener
        this.onUpdateUrlListener.onUpdate(u);
    }




    /**
     * does a search and handles all the U.I changes
     * @param search string of search (can be url or just search text
     *               we handle both cases)
     */
    public void search(String search) {
        updateUrl(MoSearchEngine.instance.getURL(search));
        // load it inside the web view (do not use cache for loading any url)
        // we only use cache for pressing back or other changes
        this.moWebView.loadUrl(url.getUrlString(),false);
        this.isUpToDate = true;
    }





    public WebView getWebView() {
        return moWebView;
    }
    public String getUrl() {
        return url.getUrlString();
    }
    public long getId(){
        return this.tabId.getId();
    }
    public String getTransitionName(){
        return this.tabId.stringify();
    }



    // returns a bitmap of the web view background
    public Bitmap getWebViewBitmap(){
        return this.moBitmap.getBitmap();
    }


    /**
     *
     * @param type must be one of {MoTabType.TYPE_NORMAL,MoTabType.TYPE_INCOGNITO}
     */
    public void setType(int type){
        tabType.setType(type);
    }


    public int getType(){
        return this.tabType.getType();
    }

    public boolean isCaptureImage() {
        return captureImage;
    }
    public boolean isPrivate() {
        return this.tabType.isPrivate();
    }
    public boolean isNormal(){
        return this.tabType.isNormal();
    }

    /**
     * makes sure that you can take screen
     * shots inside normal tabs and you can't inside
     * private tabs
     * @param a activity to apply the window rule to
     */
    public void applyWindowRules(Activity a){
        // todo
       // a.getWindow().setStatusBarContrastEnforced();
    }

    /**
     * if it has not been updated yet
     * loads the url inside the web view
     * and sets is up to date to true
     */
    public void updateWebViewIfNotUpdated() {
        // loading the url if it has not been loaded yet
        if(!this.isUpToDate) {
            this.isUpToDate = true;
            moWebView.loadUrl(this.url.getUrlString());
            MoLog.print("not up to date");
        }
    }


    /**
     * captures a bitmap preview of the web view
     * and saves it async to boost the performance
     * @param v view to capture the bitmap for
     */
    public void captureAndSaveWebViewBitmapAsync(View v){
        // needs to be outside of async b/c the capture bitmap should
        // occur on web view thread not async
        this.moBitmap.captureBitmap(v,this.url.getUrlString());
        AsyncTask.execute(() -> {
            try {
                // save it if the bitmap is not null
                if(moBitmap.getBitmap()!=null){
                    moBitmap.saveBitmap(context);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * deletes the bitmap for this tab
     */
    public void deleteWebViewBitmap(Context context){
        moBitmap.deleteBitmap(context);
    }


    /**
     *  saves the tab inside its own
     *  unique file
     */
    public void saveTab(){
        try {
            MoLog.print("saving " + tabId.stringify());
            MoFileManagerUtils.write(context,this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * deletes the file of the tab completely
     */
    public void deleteTab() {
        MoFileManagerUtils.delete(context,this);
        deleteWebViewBitmap(context);
    }


    /**
     * returns the website or baseUrl of this tab's url
     */
    public String getBaseUrl(){
        return MoUrlUtils.getBaseUrl(this.url);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoTab)) return false;
        MoTab tab = (MoTab) o;
        return tab.getId() == getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] c = MoFile.loadable(data);
        this.url = new MoURL(c[0],context);
        this.moBitmap.load(c[1],context);
        this.moWebView.load(c[2],context);
        this.tabId.load(c[3],context);
    }



    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(this.url,this.moBitmap,this.moWebView,this.tabId);
    }

    @Override
    public String getDirName() {
        return MoTabsManager.TAB_DIR;
    }

    @Override
    public String getFileName() {
        return this.tabId.stringify();
    }


    // set of functions for different events of activity
    public void onPause(){
        this.moWebView.onPause();
    }
    public void onResume(){
        this.moWebView.onResume();
    }
    public void onDestroy(){
        this.moWebView.onDestroy();
    }



    public void onErrorReceived(WebResourceRequest request, WebResourceError error){
//        moWebView.setVisibility(View.INVISIBLE);
//        errorLayout.setVisibility(View.VISIBLE);
    }



    // mo selectable

    @Override
    public boolean onSelect() {
        this.isSelected = !this.isSelected;
        return this.isSelected;
    }

    @Override
    public void setSelected(boolean b) {
        this.isSelected = b;
    }

    @Override
    public boolean isSelected() {
        return this.isSelected;
    }


    // mo searchable

    @Override
    public boolean updateSearchable(Object... objects) {
        this.isSearched = MoSearchableUtils.isSearchable(true,objects,
                getUrl());
        return this.isSearched;
    }

    @Override
    public boolean isSearchable() {
        return this.isSearched;
    }

    @Override
    public void setSearchable(boolean b) {
        this.isSearched = b;
    }


}
