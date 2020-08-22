package com.moofficial.moweb;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartCoordinatorActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoTransitions.MoCircularTransition;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewGroupUtils.MoCoordinatorUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

public class MoTabActivity extends MoSmartCoordinatorActivity {


    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MoCircularTransition t = new MoCircularTransition();
       // t.setSlideEdge(Gravity.START);
        //t.setDuration(2000);
        Window w = getWindow();
        w.setEnterTransition(t);
        w.setExitTransition(t);
//        w.setAllowReturnTransitionOverlap(true);
//        w.setAllowEnterTransitionOverlap(true);

        super.onCreate(savedInstanceState);

    }

    @Override
    protected void init() {



        MoAppbarUtils.snapNoToolbar(collapsingToolbarLayout);
        appBarLayout.setExpanded(false);


        this.tab = MoTabController.instance.getCurrent();
        this.moTabSearchBar = new MoTabSearchBar(this)
                .setTab(tab)
                .setTabOnSearchListener(search -> tab.search(search))
                .setTextSearch(tab.getUrl())
                .setOnTabsButtonClicked(view -> {
                    MoTabController.instance.onTabsButtonPressed();
                    supportFinishAfterTransition();
//                    finishAfterTransition();
                    //exitReveal();
                })
                .setNumberOfTabs(MoTabsManager.size());
        this.tab.init();
        tab.updateWebViewIfNotUpdated();
        webView = tab.getMoWebView();


        // todo add back the on touch listener stuff
        webView.setOnTouchListener((view, motionEvent) -> false);
        webView.resumeTimers();
        webView.setNestedScrollingEnabled(true);
        webView.moveWebViewTo(coordinatorLayout, MoCoordinatorUtils.getScrollingParams());








        //MoTabUtils.transitionToInTabMode(webView,linearNested.getLinearLayout());
        linearBottom.addView(moTabSearchBar);


//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                enterReveal();
//                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });

    }


    void enterReveal() {
        // previously invisible view
        final View myView = rootView;
        //myView.measure(0,0);
        // get the center for the clipping circle
        int cx = getWidthPixels() / 2;
        int cy = getHeightPixels() / 2;

        // get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(cx, cy);

        // create the animator for this view (the start radius is zero)
        Animator anim = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0f, finalRadius);

        // make the view visible and start the animation
        myView.setVisibility(View.VISIBLE);
        anim.start();
        MoLog.print("enter reveal");
    }

    void exitReveal() {
        // previously visible view
        final View myView = rootView;

        // get the center for the clipping circle
        int cx = getWidthPixels() / 2;
        int cy = getHeightPixels() / 2;

        // get the initial radius for the clipping circle
        int initialRadius = getHeightPixels();

        // create the animation (the final radius is zero)
        Animator anim =
                ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);

        // make the view invisible when the animation is done
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                rootView.setVisibility(View.INVISIBLE);
                finish();
                overridePendingTransition(0,0);
                //myView.setVisibility(View.INVISIBLE);
            }
        });

        // start the animation
        anim.start();

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
        // destroying the search bar for tab
        this.moTabSearchBar.onDestroy();
        // todo
    }

    @Override
    public void onBackPressed() {
        tab.onBackPressed(MoTabActivity.super::finishAffinity);
    }
}