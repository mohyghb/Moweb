package com.moofficial.moweb;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

public class MoTabActivity extends MoSmartActivity {


    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;

    @Override
    protected void init() {
       // getRootView().setFitsSystemWindows(true);
        MoAppbarUtils.snapNoToolbar(collapsingToolbarLayout);
//        snapAppbar();
//        noToolbarNeeded();

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

//        webView.setOnResizeWebViewListener(h -> {
//            int newHeight = (int) (h * getResources().getDisplayMetrics().density);
//            MoLog.print("diff = " + (newHeight - webView.getWebState().getHeight()));
//            if(newHeight!= webView.getWebState().getHeight()){
//                // saving it for later use (x and y scroll should be zero to show the top of the web view)
//                webView.getWebState().setHeight(newHeight).setScrollX(0).setScrollY(0);
//                // what to do when we resize it
//                // we are resizing the web view so it only shows the
//                // height that it needs
//                runOnUiThread(() -> {
//                    TransitionManager.beginDelayedTransition(getGroupRootView(),new ChangeBounds());
//                    webView.restoreState((NestedScrollView) null);
//                });
//                MoLog.print("resize");
//            }
//        });
//        webView.setOnPageFinishedListener(new MoOnPageFinishedListener() {
//            @Override
//            public void onFinished(WebView view, String url) {
//                nestedScrollView.scrollTo(0,0);
//            }
//        });

        /**
         * construction
         */

        coordinatorLayout.removeView(nestedScrollView);

        CoordinatorLayout.LayoutParams lp = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT);
        lp.setBehavior(new AppBarLayout.ScrollingViewBehavior());

       // webView.setLayoutParams(lp);
        webView.setOnTouchListener((view, motionEvent) -> false);
        webView.resumeTimers();
        webView.setNestedScrollingEnabled(true);
        webView.moveWebViewTo(coordinatorLayout,lp);

      //  coordinatorLayout.addView(webView);


//        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int i, int i1, int i2, int i3) {
//                MoLog.print(String.format("%d,%d,%d,%d",i,i1,i2,i3));
//            }
//        });

        appBarLayout.setExpanded(false);



        /**
         * construction
         */

        //MoTabUtils.transitionToInTabMode(webView,linearNested.getLinearLayout());
        linearBottom.addView(moTabSearchBar);


    }

    @Override
    protected void onResume() {
        super.onResume();
        // restore web view
        //webView.restoreState(nestedScrollView);

       // tab.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // saving web state
//        webView.getWebState()
//                .setScrollX(nestedScrollView.getScrollX())
//                .setScrollY(nestedScrollView.getScrollY());
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