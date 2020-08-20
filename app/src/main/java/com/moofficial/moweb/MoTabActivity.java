package com.moofficial.moweb;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebView;

public class MoTabActivity extends MoSmartActivity {


    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;

    @Override
    protected void init() {
       // getRootView().setFitsSystemWindows(true);
        noToolbarNeeded();

        this.tab = MoTabController.instance.getCurrent();

        this.moTabSearchBar = new MoTabSearchBar(this)
                .setTab(tab)
                .setTabOnSearchListener(search -> tab.search(search))
                .setTextSearch(tab.getUrl())
                .setOnTabsButtonClicked(view -> {
                    MoTabController.instance.onTabsButtonPressed();
                    finish();
                })
                .setNumberOfTabs(MoTabsManager.size());
        this.tab.init();
        tab.updateWebViewIfNotUpdated();
        webView = tab.getMoWebView();
        // webView.enableCorrectMode();
        webView.setNestedScrollingEnabled(true);
        // webView.setLayerType(View.LAYER_TYPE_HARDWARE,null);


        MoTabUtils.transitionToInTabMode(webView,linearNested.getLinearLayout());
//        linearNested.addView(webView,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT));
        //nestedScrollView.setFillViewport(true);
        linearBottom.addView(moTabSearchBar);






    }

    @Override
    protected void onResume() {
        super.onResume();
        tab.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        tab.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // destroying the search bar for tab
        this.moTabSearchBar.onDestroy();
    }

    @Override
    public void onBackPressed() {
        tab.onBackPressed(MoTabActivity.super::finishAffinity);
    }
}