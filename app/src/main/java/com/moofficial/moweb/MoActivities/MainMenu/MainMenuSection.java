package com.moofficial.moweb.MoActivities.MainMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoOnBackPressed;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoEmptyLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoWrappers.MoWrapperLinearLayout;
import com.moofficial.moweb.MoActivities.HistoryActivity;
import com.moofficial.moweb.MoActivities.MainActivity;
import com.moofficial.moweb.MoActivities.MainTransitionTo;
import com.moofficial.moweb.MoActivities.SettingsActivity;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabExceptions.MoTabNotFoundException;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.R;

import java.util.List;

@SuppressWarnings("ConstantConditions")
public abstract class MainMenuSection extends MoEmptyLayout implements MoOnBackPressed ,MoOnTabClickListener{



    protected MoTabRecyclerAdapter tabRecyclerAdapter;
    protected MoRecyclerView tabRecyclerView;
    protected MoToolBar moToolBar;
   // protected MoPopUpMenu addPopUpMenu;
    protected MoPopUpMenu morePopUpMenu;
    protected MoSelectable<MoTab> tabSelectable;
    protected MoToolBar tabSelectableToolbar;
    protected MoSearchable searchable;
    protected MoSearchBar searchBar;
    protected MoListViewSync viewSync;
    protected FloatingActionButton floatingActionButton;
    protected BottomAppBar bottomAppBar;
    protected BottomAppBar barsAppBar;
    //protected MoWrapperToolbar wrapperToolbar;
    protected MainTransitionTo transitionToTab;
    protected Activity activity;

    public MainMenuSection(Context context) {
        super(context);
    }

    public MainMenuSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainMenuSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MainMenuSection setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    public Activity getActivity() {
        return activity;
    }

    abstract List<MoTab> getTabs();

    public MainMenuSection setTransitionToTab(MainTransitionTo transitionToTab) {
        this.transitionToTab = transitionToTab;
        return this;
    }

    @Override
    public void initViews() {

    }


    public void init(){
        initUI();
        initClass();
    }

    void initUI() {
        initMoToolbar();
        initTabSelectableToolbar();
        initSearchBar();
        initFloatingActionButton();
        initBottomAppbar();
        syncToolbars();
    }

    void initClass() {
        initTabAdapter();
        initTabRecyclerView();
        //initAddPopUpMenu();
        initMorePopUpMenu();
        initTabSelectable();
        initMoSearchable();
        initViewSync();
    }

    private void initFloatingActionButton() {
        floatingActionButton = findViewById(R.id.main_menu_fab);
        floatingActionButton.setOnClickListener(view -> createNewTab());
    }

    private void initBottomAppbar(){
        bottomAppBar = findViewById(R.id.main_menu_toolbar);
        barsAppBar = findViewById(R.id.main_menu_second_bottom_app_bar);
    }

    private void syncToolbars() {
        MoWrapperLinearLayout w = new MoWrapperLinearLayout(findViewById(R.id.main_menu_toolbar_linear_layout));
        //w.setupMultipleBars(this.moToolBar, this.moToolBar, this.tabSelectableToolbar, this.searchBar);
        w.addView(this.moToolBar);

        new MoWrapperLinearLayout(findViewById(R.id.main_menu_bars_linear_layout))
                .setupMultipleBars(this.tabSelectableToolbar,this.tabSelectableToolbar, this.searchBar);
    }

    private void initTabSelectableToolbar() {
        this.tabSelectableToolbar = new MoToolBar(getContext())
                .showCheckBox()
                .hideLeft()
                .setMiddleIcon(R.drawable.ic_baseline_delete_24)
                .setMiddleOnClickListener(view -> performDelete())
                .setRightIcon(R.drawable.ic_baseline_share_24);
        this.tabSelectableToolbar.getCardView().makeTransparent();
    }// delete the selected tabs

    private void performDelete() {
        tabRecyclerAdapter.deleteSelectedItems();
        if (tabSelectable.isInActionMode()) {
            viewSync.removeAction();
        }
    }

    private void initTabRecyclerView() {
        tabRecyclerView = MoRecyclerUtils.get((ViewGroup)this, R.id.main_menu_tab_recycler, tabRecyclerAdapter)
                .setLayoutManagerType(MoRecyclerView.LINEAR_LAYOUT_MANAGER)
                .setOrientation(MoRecyclerView.HORIZONTAL)
                .show();
        tabRecyclerView.setNestedScrollingEnabled(true);
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

    }

    private void initTabAdapter() {
        tabRecyclerAdapter = new MoTabRecyclerAdapter(getTabs(), getContext(), false)
                .setOnTabClickListener(this);
    }

    private void initTabSelectable() {
        this.tabSelectable = new MoSelectable<MoTab>(getContext(), (ViewGroup) this, this.tabRecyclerAdapter)
                .setCounterView(tabSelectableToolbar.getTitle())
                .setSelectAllCheckBox(tabSelectableToolbar.getCheckBox())
                .addNormalViews(this.searchBar)
                .addUnNormalViews(barsAppBar,this.tabSelectableToolbar);
    }


    private void initMoToolbar() {
        this.moToolBar = new MoToolBar(getActivity())
                .setRightIcon(R.drawable.ic_baseline_more_vert_24)
                .setRightOnClickListener(view -> morePopUpMenu.show(view))
                .setMiddleIcon(R.drawable.ic_baseline_search_24);
        this.moToolBar.getCardView().makeTransparent();
    }

    private void initMoSearchable() {
        this.searchable = new MoSearchable(getActivity(), (ViewGroup) this, MoTabsManager::getTabs)
                .setActivity(getActivity())
                //.setAppBarLayout(this.appBarLayout)
                .setClearSearch(this.searchBar.getRightButton())
                .setCancelSearch(this.searchBar.getLeftButton())
                .addUnNormalViews(barsAppBar,this.searchBar)
                .addNormalViews(this.tabSelectableToolbar)
                .setSearchButton(this.moToolBar.getMiddleButton())
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
        //this.searchable.setTransitionIn(new Slide()).setTransitionOut(new Slide());


    }

//    public void hideFab() {
//        CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
//                ViewGroup.LayoutParams.WRAP_CONTENT);
//        p.setAnchorId(View.NO_ID);
//        floatingActionButton.setLayoutParams(p);
//        bottomAppBar.post(()->bottomAppBar.());
//        MoLog.print("hide fab");
//    }

    /**
     * updates the adapter with a new list
     * of tabs
     *
     * @param lt list of tabs to update the
     *           adapter to
     */
    private void updateAdapter(List<MoTab> lt) {
        tabRecyclerAdapter.setDataSet(lt);
        getActivity().runOnUiThread(() -> tabRecyclerAdapter.notifyDataSetChanged());
    }

    private void initSearchBar() {
        this.searchBar = new MoSearchBar(getActivity())
                .setSearchHint(R.string.tab_search_hint);
        this.searchBar.getCardView().makeTransparent();
    }

    private void initViewSync() {
        this.viewSync = new MoListViewSync((ViewGroup) this, this.tabSelectable, this.searchable)
                .setPutOnHold(true)
                .setSharedElements(bottomAppBar,floatingActionButton);
    }

//    /**
//     * when the user clicks the middle toolbar icon
//     * we show this pop up menu that we have init
//     */
//    private void initAddPopUpMenu() {
//        addPopUpMenu = new MoPopUpMenu(getContext()).setEntries(
//                new Pair<>(getContext().getString(R.string.NewTab), menuItem -> {
//                    createNewTab();
//                    return false;
//                }),
//                new Pair<>(getContext().getString(R.string.NewIncognitoTab), menuItem -> {
//                    MoTabsManager.addPrivateTab(getContext(), MoSearchEngine.instance.homePage(), false);
//                    return false;
//                }));
//    }

    private void createNewTab() {
        MoTabsManager.addTab(getActivity(), MoSearchEngine.instance.homePage(), false);
        transitionToTab.transition();
    }

    /**
     * when the user clicks on the more button
     * this menu pops up
     */
    private void initMorePopUpMenu() {
        morePopUpMenu = new MoPopUpMenu(getActivity()).setEntries(
                new Pair<>(getContext().getString(R.string.Settings), menuItem -> {
                    Intent setting = new Intent(getActivity(), SettingsActivity.class);
                    getActivity().startActivity(setting);
                    return false;
                }),
                new Pair<>(getContext().getString(R.string.History), menuItem -> {
                    HistoryActivity.launch(getActivity(), MainActivity.HISTORY_FROM_MAIN_MENU_REQUEST);
                    return false;
                }),
                new Pair<>(getContext().getString(R.string.Clear_All_Normal_Tabs), menuItem -> {
                    MoTabsManager.clearAllNormalTabs(getActivity());
                    tabRecyclerAdapter.notifyDataSetChanged();
                    return false;
                })
        );
    }


    public void notifyDataSetChanged() {
        tabRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onTabClickListener(MoTab t, View sharedView, int index) {
        MoTabController.instance.setNewTab(getActivity(),t);
        transitionToTab.transition();
    }

    @Override
    public int getLayoutId() {
        return R.layout.main_menu_tabs_fragment;
    }


}