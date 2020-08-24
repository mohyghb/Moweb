package com.moofficial.moweb.MoActivities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartCoordinatorActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoWindow.MoWindowTransitions;
import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoTransitions.MoCircularTransition;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewGroupUtils.MoCoordinatorUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebAppLoader.MoWebAppLoader;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

public class MoTabActivity extends MoSmartCoordinatorActivity {

    private static final int MAIN_MENU_REQUEST_CODE = 0;


    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;
    private MoToolBar moToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MoWindowTransitions.apply(new MoCircularTransition(),this);
        MoWebAppLoader.loadApp(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        MoAppbarUtils.snapNoToolbar(collapsingToolbarLayout);
        appBarLayout.setExpanded(false);
        initSearchBar();
        update();
        initToolbar();
    }

    /**
     * updates the activity so that the user
     * only sees the updated version of the
     * tab activity and everything seems normal
     */
    private void update() {
        updateTab();
        updateWebView();
        updateSearchBar();
        updateTitle();
        updateSubtitle();
    }

    private void updateTitle(){
        setTitle(this.webView.getTitle());
    }

    private void updateSubtitle(){
        setSubTitle(this.webView.getUrl());
    }

    private void updateWebView() {
        this.webView = tab.getMoWebView();
        MoTabUtils.transitionToInTabMode(webView,coordinatorLayout, MoCoordinatorUtils.getScrollingParams());
    }

    private void updateTab() {
        this.tab = MoTabController.instance.getCurrent();
        this.tab.init();
        this.tab.updateWebViewIfNotUpdated();
        this.tab.setOnUpdateUrlListener(s -> {
            updateTitle();
            updateSubtitle();
        });
    }

    /**
     * creates a search bar that can be
     * used multiple times over the life time
     * of the tab by changing its fields
     */
    private void initSearchBar() {
        this.moTabSearchBar = new MoTabSearchBar(this);
        linearBottom.addView(moTabSearchBar);
    }

    /**
     * puts the home button, refresh button
     * and the book mark button inside the toolbar
     */
    private void initToolbar() {
        this.moToolBar = new MoToolBar(this)
                .setLeftIcon(R.drawable.ic_baseline_home_24)
                .setLeftOnClickListener(view -> tab.goToHomepage())
                .hideTitle()
                .setMiddleIcon(R.drawable.ic_baseline_refresh_24)
                .setMiddleOnClickListener(view -> webView.forceReload())
                .setRightIcon(tab.urlIsBookmarked()?
                        R.drawable.ic_baseline_star_24:
                        R.drawable.ic_baseline_star_border_24)
                .setRightOnClickListener(view -> {
                    tab.bookmarkTheTab();
                });
        moToolBar.getCardView().makeTransparent();

        // on tab change bookmark listener so we know which icon to use
        tab.setOnTabBookmarkChanged(isBookmarked -> {
            this.moToolBar.setRightIcon(isBookmarked?
                    R.drawable.ic_baseline_star_24:
                    R.drawable.ic_baseline_star_border_24);
        });
        // set it as a toolbar
        toolbar.addToolbar(this.moToolBar);
    }

    /**
     * removes all the listeners for the search bar
     * on the previous tab and loads this tabs
     * search bar module
     */
    private void updateSearchBar(){
        moTabSearchBar.onDestroy();
        moTabSearchBar.syncWith(tab)
                .setTextSearch(tab.getUrl())
                .setOnTabsButtonClicked(view -> {
                    startActivityForResult(new Intent(this, MainMenuActivity.class),
                            MAIN_MENU_REQUEST_CODE,
                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                })
                .setNumberOfTabs(MoTabsManager.size());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAIN_MENU_REQUEST_CODE) {
            update();
        }

//        if(resultCode == RESULT_OK && data != null){
//            // only check the data if the result is okay and
//            // data is not null
//
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // todo
    }

    @Override
    protected void onPause() {
        super.onPause();
        // todo
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // todo
        // monote remove the bookmark listener
        //  progress bar, web view, tab, search text
        //  other things from the tab when leaving
        //  or do them inside the on pause?

    }

    @Override
    public void onBackPressed() {
        tab.onBackPressed(MoTabActivity.super::onBackPressed);
    }
}