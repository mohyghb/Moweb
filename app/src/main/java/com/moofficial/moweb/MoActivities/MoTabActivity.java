package com.moofficial.moweb.MoActivities;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoCoordinatorUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoUpdateTabActivity;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebAppLoader.MoWebAppLoader;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

public class MoTabActivity extends MoSmartActivity implements MoUpdateTabActivity {

    private static final int MAIN_MENU_REQUEST_CODE = 0;
    public static final int GO_TO_TAB_ACTIVITY_REQUEST = 1;


    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;
    private MoToolBar moToolBar;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        //MoWindowTransitions.apply(new MoCircularTransition(),this);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.shared_element_transition));
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.shared_element_transition));
        super.onCreate(savedInstanceState);
        MoWebAppLoader.loadApp(this);
        MoTabController.instance.setUpdateTabActivity(this);

        update();
    }



    private void initUI() {
        MoAppbarUtils.snapNoToolbar(layout.collapsingToolbarLayout);
        layout.appBarLayout.setExpanded(false);
//        layout.makeActivityRound();
        initSearchBar();
        initToolbar();
    }

    @Override
    protected void init() {
        initUI();
    }

    /**
     * updates the activity so that the user
     * only sees the updated version of the
     * tab activity and everything seems normal
     */
    @Override
    public void update() {
        if(MoTabController.instance.isOutOfOptions()){
            // launch the main menu
            // when layout is done constructing
//            getRootView().getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    // monote: maybe make sure that other views are invisible for
//                    //  better user interactions?
//                    launchMainMenu();
//                    getRootView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                }
//            });
        }else {
            removePreviousTab();
            updateTab();
            updateWebView();
            updateSearchBar();
            updateTitle();
            updateSubtitle();
            updateToolbar();
        }
    }

    /**
     * removes the previous tab from the coordinator
     * layout before passing it to the recycler view inside
     * main menu
     * this solved the issue where we opened a tab
     * from somewhere else and it didn't remove the
     * previous web view and tab from here
     * so we only are removing it if there is a current tab
     * and the current tab is not the same as the new one
     */
    private void removePreviousTab() {
        if(tab!=null && !MoTabController.instance.currentIs(this.tab) && webView != null){
            // then remove it from web view when updating
            layout.coordinatorLayout.removeView(webView);
            moTabSearchBar.onDestroy();
        }
    }


    private void updateTitle(){
        layout.title.setText(this.webView.getTitle());
    }

    private void updateSubtitle(){
        layout.subtitle.setText(this.webView.getUrl());
    }

    private void updateWebView() {
        this.webView = tab.getMoWebView();
        MoTabUtils.transitionToInTabMode(webView,layout.coordinatorLayout, MoCoordinatorUtils.getScrollingParams());
        this.webView.setOnLongClickListener(view -> {
            webView.getHitTestResultParser().createDialogOrSmartText(getContext());
            return false;
        });
    }

    private void updateTab() {
        this.tab = MoTabController.instance.getCurrent();
        this.tab.init();
        this.tab.updateWebViewIfNotUpdated();
        this.tab.setOnUpdateUrlListener(s -> {
            updateTitle();
            updateSubtitle();
            updateToolbar();
        });
    }

    /**
     * creates a search bar that can be
     * used multiple times over the life time
     * of the tab by changing its fields
     */
    private void initSearchBar() {
        this.moTabSearchBar = new MoTabSearchBar(this)
                .setParentLayout(layout.coordinatorLayout);
        layout.linearBottom.addView(moTabSearchBar);
    }

    /**
     * puts the home button, refresh button
     * and the book mark button inside the toolbar
     */
    private void initToolbar() {
        this.moToolBar = new MoToolBar(getContext());
        // set it as a toolbar
        layout.toolbar.addToolbar(this.moToolBar);
    }

    private void updateToolbar() {
        this.moToolBar.setLeftIcon(R.drawable.ic_baseline_home_24)
                .setLeftOnClickListener(view -> tab.goToHomepage())
                .hideTitle()
                .setRightIcon(R.drawable.ic_baseline_refresh_24)
                .setRightOnClickListener(view -> webView.forceReload())
                .setMiddleIcon(tab.urlIsBookmarked()?
                        R.drawable.ic_baseline_star_24:
                        R.drawable.ic_baseline_star_border_24)
                .setMiddleOnClickListener(view -> {
                    tab.bookmarkTheTab();
                })
                .getCardView().makeTransparent();

        // on tab change bookmark listener so we know which icon to use
        tab.setOnTabBookmarkChanged(isBookmarked -> {
            this.moToolBar.setMiddleIcon(isBookmarked?
                    R.drawable.ic_baseline_star_24:
                    R.drawable.ic_baseline_star_border_24);
        });
    }



    /**
     * removes all the listeners for the search bar
     * on the previous tab and loads this tabs
     * search bar module
     */
    private void updateSearchBar(){
        moTabSearchBar.syncWith(tab)
                .clearEditTextFocus()
                .setTextSearch(tab.getUrl())
                .setOnTabsButtonClicked(view -> {
                    //construction
                    tab.captureAndSaveWebViewBitmapAsync();

                    // add a preview to this root
                    ImageView imageView = new ImageView(this);
                    imageView.setImageBitmap(tab.getWebViewBitmap());
                    imageView.setTransitionName(tab.getTransitionName());
                    getGroupRootView().addView(imageView,new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));

                    startActivityForResult(new Intent(MoTabActivity.this, MainMenuActivity.class),
                            MAIN_MENU_REQUEST_CODE,
                            ActivityOptions.makeSceneTransitionAnimation(this,imageView,
                                    imageView.getTransitionName()).toBundle());
                    //launchMainMenu();
                })
                .setNumberOfTabs(MoTabsManager.size());
    }

    /**
     * goes to the main menu activity
     * to show all the tabs to the user
     */
    private void launchMainMenu() {
        //onTabPressed.run();
        startActivityForResult(new Intent(MoTabActivity.this, MainMenuActivity.class),
                MAIN_MENU_REQUEST_CODE,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

//    @Override
//    public boolean onBackPressed() {
//        return tab.onBackPressed();
//    }

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
    }

    @Override
    public void onBackPressed() {
        if(!tab.onBackPressed()){
            super.onBackPressed();
        }
    }
}