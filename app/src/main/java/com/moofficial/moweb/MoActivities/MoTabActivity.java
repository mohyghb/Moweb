package com.moofficial.moweb.MoActivities;

import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoBasicFragment;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoCoordinatorUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoUpdateTabActivity;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

@SuppressWarnings("ConstantConditions")
public class MoTabActivity extends MoBasicFragment implements MoUpdateTabActivity {

    private static final int MAIN_MENU_REQUEST_CODE = 0;
    public static final int GO_TO_TAB_ACTIVITY_REQUEST = 1;



    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;
    private MoToolBar moToolBar;
    ImageView imageView;
    private MainTransitionTo moveToMainMenu;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.shared_element));
        setSharedElementReturnTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.shared_element));
    }


    //    @Override
//    public void onCreate(Bundle savedInstanceState) {
//
//
////        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
////                .inflateTransition(R.transition.shared_element));
////        getWindow().setSharedElementExitTransition(TransitionInflater.from(this)
////                .inflateTransition(R.transition.shared_element));
//
//        super.onCreate(savedInstanceState);
//
//
//
//
//
//        // mo for shared transition this is needed
//        //coordinatorLayout.setTransitionName(tab.getTransitionName());
//
//
//
//        //postponeEnterTransition();

//
//
//    }


    public void showTransition() {

        imageView.setImageBitmap(tab.getWebViewBitmap());
        imageView.setTransitionName(tab.getTransitionName());
        ((Transition)getSharedElementEnterTransition()).addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                webView.setVisibility(View.GONE);
                imageView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                MoLog.print("transition end");
                imageView.post(() -> imageView.setVisibility(View.GONE));
                webView.setVisibility(View.VISIBLE);
                transition.removeListener(this);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
    }


    public MoTabActivity setMoveToMainMenu(MainTransitionTo moveToMainMenu) {
        this.moveToMainMenu = moveToMainMenu;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MoLog.print("on create view is called once");
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }



    protected void init() {
        MoLog.print("init is called");

        MoAppbarUtils.snapNoToolbar(layout.collapsingToolbarLayout);
        layout.appBarLayout.setExpanded(false);
        initSearchBar();
        initToolbar();
        MoTabController.instance.setUpdateTabActivity(this);

        imageView = new ImageView(getContext());
        layout.coordinatorLayout.addView(imageView,MoCoordinatorUtils.getScrollingParams());


        update();
    }

    /**
     * updates the activity so that the user
     * only sees the updated version of the
     * tab activity and everything seems normal
     */
    @Override
    public void update() {
//        if(MoTabController.instance.isOutOfOptions()) {
//            // if we are out of options, make a new tab
//            MoTabsManager.addTab(this, MoSearchEngine.instance.homePage(),false);
//        }else {

            imageView.setVisibility(View.GONE);
            removePreviousTab();
            updateTab();
            updateWebView();
            updateSearchBar();
            updateTitle();
            updateSubtitle();
            updateToolbar();
            MoLog.print("update tab");
//        }
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
        layout.setTitle(this.webView.getTitle());
    }

    private void updateSubtitle(){
        layout.setSubTitle(this.webView.getUrl());
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
        this.moTabSearchBar = new MoTabSearchBar(getContext())
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
                    onTabsButtonPressed();
                })
                .setNumberOfTabs(MoTabsManager.size());
    }

    /**
     * goes to the main menu activity
     * to show all the tabs to the user
     */
    private void onTabsButtonPressed() {

        tab.captureAndSaveWebViewBitmapAsync(layout.coordinatorLayout);
        imageView.setImageBitmap(tab.getWebViewBitmap());
        imageView.setVisibility(View.VISIBLE);
        imageView.setTransitionName(tab.getTransitionName());
        webView.setVisibility(View.GONE);

        moveToMainMenu.transition(imageView);


//
//        getActivity().supportFinishAfterTransition();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == MAIN_MENU_REQUEST_CODE) {
//            update();
//        }
//
//    }


    public View getSharedView() {
        return this.imageView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        //
      //  update();
        //showTransition();
        //
        postponeEnterTransition();
        showTransition();
        startPostponedEnterTransition();
        MoLog.print("on resume tab");
    }

    @Override
    public void onPause() {
        super.onPause();
        MoLog.print("on pause tab");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
//        tab.onBackPressed(MoTabActivity.super::onBackPressed);
        return false;
    }
}