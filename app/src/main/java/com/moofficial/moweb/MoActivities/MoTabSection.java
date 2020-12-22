package com.moofficial.moweb.MoActivities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.ViewGroup;

import androidx.cardview.widget.CardView;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoActivitySettings.MoActivitySettings;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoOnBackPressed;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoBasicLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoCoordinatorUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoFindBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moweb.MoActivities.History.SavedPasswordsActivity;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoUpdateTabActivity;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

@SuppressWarnings("ConstantConditions")
public class MoTabSection extends MoBasicLayout implements MoUpdateTabActivity, MoOnBackPressed {

    private static final int MAIN_MENU_REQUEST_CODE = 0;
    public static final int GO_TO_TAB_ACTIVITY_REQUEST = 1;



    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;
    private MoToolBar moToolBar;
    private MainTransitionTo moveToMainMenu;
    private MoCardView webCard;

    public MoTabSection(Context context) {
        super(context);
    }

    public MoTabSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoTabSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    MoTabSection setMoveToMainMenu(MainTransitionTo moveToMainMenu) {
        this.moveToMainMenu = moveToMainMenu;
        return this;
    }



    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onAppbarLayoutHeightChanged(MoActivitySettings.MO_GOLDEN_RATIO);
    }

    protected void init() {
        MoAppbarUtils.snapNoToolbar(collapsingToolbarLayout);
        removeNestedScrollView();
        appBarLayout.setExpanded(false);
        initSearchBar();
        initToolbar();
        initWebCardView();
        MoTabController.instance.setUpdateTabActivity(this);
        update();
    }

    /**
     * updates the activity so that the user
     * only sees the updated version of the
     * tab activity and everything seems normal
     */
    @Override
    public void update() {
        if(MoTabController.instance.isOutOfOptions()) {
            // if we are out of options, make a new tab
            MoTabsManager.addTab(getContext(), MoSearchEngine.instance.homePage(),false);
        }else {
            // only perform update when the new tab is different than the old tab
            removePreviousTab();
            updateTab();
            updateWebView();
            updateSearchBar();
            updateTitle();
            updateSubtitle();
            updateToolbar();
            MoLog.print("update tab");
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
        if(tab!=null && webView != null){
            // then remove it from web view when updating
            coordinatorLayout.removeView(webView);
            moTabSearchBar.onDestroy();
        }
    }


    private void updateTitle(){
        setTitle(this.webView.getTitle());
    }

    private void updateSubtitle(){
        setSubTitle(this.webView.getUrl());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateWebView() {
        this.webView = tab.getMoWebView();
        MoTabUtils.transitionToInTabMode(webView,webCard, new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.webView.onResume();
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
        MoFindBar findBar = new MoFindBar(getContext());
        MoCardRecyclerView suggestionCard = new MoCardRecyclerView(getContext());
        this.moTabSearchBar = new MoTabSearchBar(getContext())
                .setParentLayout(coordinatorLayout)
                .setSuggestionCardRecyclerView(suggestionCard)
                .setBottomParentLayout(linearBottom.getLinearLayout())
                .setMoFindBar(findBar);
        this.moTabSearchBar.init();
        linearBottom.setupMultipleBars(moTabSearchBar,suggestionCard,moTabSearchBar,findBar);
    }

    /**
     * puts the home button, refresh button
     * and the book mark button inside the toolbar
     */
    private void initToolbar() {
        this.moToolBar = new MoToolBar(getContext());
        // set it as a toolbar
        toolbar.addToolbar(this.moToolBar);
    }

    private void initWebCardView() {
        webCard = new MoCardView(getContext()).makeCardRecRound().makeTransparent();
        coordinatorLayout.addView(webCard,MoCoordinatorUtils.getScrollingParams());
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
    private void updateSearchBar() {
        //monote hide suggestion on every update
        moTabSearchBar.syncWith(tab)
                .clearEditTextFocus()
                .setTextSearch(tab.getUrl())
                .setOnTabsButtonClicked(view -> {

                    getContext().startActivity(new Intent(getContext(), SavedPasswordsActivity.class));


//                    MoKeyboardUtils.hideSoftKeyboard(view);
//                    onTabsButtonPressed();
                })
                .setNumberOfTabs(tab.isPrivate()?MoTabsManager.sizePrivate():MoTabsManager.size());
    }

    /**
     * goes to the main menu activity
     * to show all the tabs to the user
     */
    private void onTabsButtonPressed() {
        tab.captureAndSaveWebViewBitmapAsync(coordinatorLayout);
        moveToMainMenu.transition();
    }

    @Override
    public boolean onBackPressed() {

        return tab.onBackPressed();
    }

}