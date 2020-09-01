package com.moofficial.moweb.MoActivities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Pair;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabExceptions.MoTabNotFoundException;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.R;

import java.util.List;

public class MainMenuActivity extends MoSmartActivity implements MoOnTabClickListener {

    public static final int HISTORY_REQUEST_CODE = 0;

    private MoCardRecyclerView tabCardRecycler;
    private MoTabRecyclerAdapter tabRecyclerAdapter;
    private MoRecyclerView tabRecyclerView;
    private MoToolBar moToolBar;
    private MoPopUpMenu addPopUpMenu,morePopUpMenu;
    private MoSelectable<MoTab> tabSelectable;
    private MoToolBar tabSelectableToolbar;
    private MoSearchable searchable;
    private MoSearchBar searchBar;

    private MoListViewSync viewSync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //MoWindowTransitions.apply(new Slide(),this);
        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.shared_element));
        getWindow().setSharedElementExitTransition(TransitionInflater.from(this)
                .inflateTransition(R.transition.shared_element));
        postponeEnterTransition();
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        initUI();
        initClass();
    }

    private void initUI() {
        setTitle(getString(R.string.app_name));
        //setSubTitle(MoTabsManager.size() + " tabs");
        //appBarLayout.setExpanded(false);
        initTabCard();
        initMoToolbar();
        initTabSelectableToolbar();
        initSearchBar();
        syncToolbars();
    }



    private void initClass() {
        initTabAdapter();
        initTabRecyclerView();
        initAddPopUpMenu();
        initMorePopUpMenu();
        initTabSelectable();
        initMoSearchable();
        initViewSync();
    }

    private void syncToolbars(){
        syncTitle(this.moToolBar.getTitle(),this.tabSelectableToolbar.getTitle());
        setupMultipleToolbars(this.moToolBar,this.moToolBar,this.tabSelectableToolbar,this.searchBar);
    }

    private void initTabSelectableToolbar(){
        this.tabSelectableToolbar = new MoToolBar(this)
                .showCheckBox()
                .hideLeft()
                .setMiddleIcon(R.drawable.ic_baseline_delete_24)
                .setMiddleOnClickListener(view -> performDelete())
                .setRightIcon(R.drawable.ic_baseline_share_24);
        this.tabSelectableToolbar.getCardView().makeTransparent();
    }

    // delete the selected tabs
    private void performDelete() {
        tabRecyclerAdapter.deleteSelectedItems();
        if(tabSelectable.isInActionMode()) {
            viewSync.removeAction();
        }
    }

    private void initTabRecyclerView() {
        tabRecyclerView = MoRecyclerUtils.get(tabCardRecycler.getRecyclerView(),tabRecyclerAdapter)
                .setMaxHeight(getHeightPixels())
                .setLayoutManagerType(MoRecyclerView.GRID_LAYOUT_MANAGER)
                .show();
        // scroll to the current tab that we are on
        // monote reterive the index some how (linear search??!!)
        //      a better way to locate the tab that we are on
        //   maybe first time scroll to it and for the rest of them
        //  you just save the instance and pass it back (store/re-store)
        try {
            tabRecyclerView.scrollToPosition(MoTabsManager.getIndexOf(MoTabController.instance.getCurrent()));
        } catch (MoTabNotFoundException e) {
            e.printStackTrace();
        }

        tabRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                tabRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                startPostponedEnterTransition();
            }
        });

    }



    private void initTabAdapter() {
        tabRecyclerAdapter = new MoTabRecyclerAdapter(MoTabsManager.getTabs(),this,true)
                .setOnTabClickListener(this);
    }

    private void initTabSelectable(){
        this.tabSelectable = new MoSelectable<>(this,getGroupRootView(), this.tabRecyclerAdapter)
                .setCounterView(title)
                .setSelectAllCheckBox(tabSelectableToolbar.getCheckBox())
                .addUnNormalViews(this.tabSelectableToolbar);
    }

    private void initTabCard() {
        tabCardRecycler = new MoCardRecyclerView(this);
        tabCardRecycler.getCardView().makeTransparent();
        linearNested.addView(tabCardRecycler, MoMarginBuilder.getLinearParams(0,8,0,0));
    }

    private void initMoToolbar(){
        this.moToolBar = new MoToolBar(this)
                .showExtraButton()
                .setMiddleIcon(R.drawable.ic_add_black_24dp)
                .setMiddleOnClickListener(view -> addPopUpMenu.show(view))
                .setRightIcon(R.drawable.ic_baseline_more_vert_24)
                .setRightOnClickListener(view -> morePopUpMenu.show(view))
                .setExtraIcon(R.drawable.ic_baseline_search_24);
        this.moToolBar.getCardView().makeTransparent();

    }

    private void initMoSearchable(){
        this.searchable = new MoSearchable(this, getGroupRootView(), MoTabsManager::getTabs)
                .setActivity(this)
                .setAppBarLayout(this.appBarLayout)
                .setClearSearch(this.searchBar.getRightButton())
                .setCancelSearch(this.searchBar.getLeftButton())
                .addUnNormalViews(this.searchBar)
                .setSearchButton(this.moToolBar.getExtraButton())
                .setSearchTextView(this.searchBar.getEditText())
                .setSearchOnTextChanged(true)
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    updateAdapter((List<MoTab>) list);
                })
                .setOnSearchCanceled(() -> {
                    // restore the tabs
                    updateAdapter(MoTabsManager.getTabs());
                });
    }

    /**
     * updates the adapter with a new list
     * of tabs
     * @param lt list of tabs to update the
     *           adapter to
     */
    private void updateAdapter(List<MoTab> lt){
        tabRecyclerAdapter.setDataSet(lt);
        runOnUiThread(()->tabRecyclerAdapter.notifyDataSetChanged());
    }

    private void initSearchBar(){
        this.searchBar = new MoSearchBar(this)
                .setSearchHint(R.string.tab_search_hint);
        this.searchBar.getCardView().makeTransparent();
    }

    private void initViewSync() {
        this.viewSync = new MoListViewSync(getGroupRootView(),this.tabSelectable,this.searchable)
                .setPutOnHold(true)
                .setSharedElements(moToolBar);
    }



    /**
     * when the user clicks the middle toolbar icon
     * we show this pop up menu that we have init
     */
    private void initAddPopUpMenu(){
        addPopUpMenu = new MoPopUpMenu(this).setEntries(
                        new Pair<>(getString(R.string.NewTab), menuItem -> {
                            MoTabsManager.addTab(this, MoSearchEngine.instance.homePage(),false);
                            supportFinishAfterTransition();
                            return false;
                        }),
                        new Pair<>(getString(R.string.NewIncognitoTab), menuItem -> {
                            MoTabsManager.addPrivateTab(this,MoSearchEngine.instance.homePage(),false);
                            supportFinishAfterTransition();
                            return false;
                        }));
    }


    /**
     * when the user clicks on the more button
     * this menu pops up
     */
    private void initMorePopUpMenu(){
        morePopUpMenu = new MoPopUpMenu(this).setEntries(
                new Pair<>(getString(R.string.Settings), menuItem -> {
                    Intent setting = new Intent(this,SettingsActivity.class);
                    this.startActivity(setting);
                    return false;
                }),
                new Pair<>(getString(R.string.History), menuItem -> {
                    HistoryActivity.launch(this,HISTORY_REQUEST_CODE);
                    return false;
                }),
                new Pair<>(getString(R.string.Clear_All_Normal_Tabs), menuItem -> {
                    MoTabsManager.clearAllNormalTabs(this);
                    tabRecyclerAdapter.notifyDataSetChanged();
                    return false;
                }),
                new Pair<>(getString(R.string.Clear_All_Incognito_Tabs), menuItem -> {
                    MoTabsManager.clearAllPrivateTabs(this);
                    // monote add this feature
                    //this.mIncognitoAdapter.notifyDataSetChanged();
                    return false;
                })
        );
    }

    @Override
    public void onTabClickListener(MoTab t, int index) {
        // when the user clicks on a tab, this is activated
        // we only change the index if the new one is not the same
        if (!MoTabController.instance.currentIs(t)) {
            MoTabController.instance.setNewTab(this,t);
        }
        supportFinishAfterTransition();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == HISTORY_REQUEST_CODE && resultCode == MoTabActivity.GO_TO_TAB_ACTIVITY_REQUEST){
            // we need to finish this activity and go to the tab activity
            supportFinishAfterTransition();
        }
    }

    @Override
    public void onBackPressed() {
        // if there is no more tabs, leave the app
        if(viewSync.hasAction()){
            viewSync.removeAction();
        }else if(MoTabController.instance.isOutOfOptions()){
            finishAffinity();
        }else{
            super.onBackPressed();
        }
    }
}