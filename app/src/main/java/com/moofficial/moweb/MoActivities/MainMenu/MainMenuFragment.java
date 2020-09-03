package com.moofficial.moweb.MoActivities.MainMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoFragment;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoWrappers.MoWrapperToolbar;
import com.moofficial.moweb.MoActivities.HistoryActivity;
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
public abstract class MainMenuFragment extends MoFragment {
    private MoTabRecyclerAdapter tabRecyclerAdapter;
    private MoRecyclerView tabRecyclerView;
    private MoToolBar moToolBar;
    private MoPopUpMenu addPopUpMenu;
    private MoPopUpMenu morePopUpMenu;
    private MoSelectable<MoTab> tabSelectable;
    private MoToolBar tabSelectableToolbar;
    private MoSearchable searchable;
    private MoSearchBar searchBar;
    private MoListViewSync viewSync;
    private MoWrapperToolbar wrapperToolbar;
    private ViewGroup rootGroup;
    private MoOnTabClickListener onTabClickListener;

    public MainMenuFragment() {
    }

    abstract List<MoTab> getTabs();

    public MainMenuFragment setOnTabClickListener(MoOnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rootGroup = (ViewGroup) view;
        initUI();
        initClass();
    }

    void initUI() {
        initMoToolbar();
        initTabSelectableToolbar();
        initSearchBar();
        syncToolbars();
    }

    void initClass() {
        initTabAdapter();
        initTabRecyclerView();
        initAddPopUpMenu();
        initMorePopUpMenu();
        initTabSelectable();
        initMoSearchable();
        initViewSync();
    }

    private void syncToolbars() {
        wrapperToolbar = new MoWrapperToolbar(rootGroup.findViewById(R.id.main_menu_toolbar),
                rootGroup.findViewById(R.id.main_menu_toolbar_linear_layout));
        wrapperToolbar.setupMultipleToolbars(this.moToolBar, this.moToolBar, this.tabSelectableToolbar,
                this.searchBar);
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
        tabRecyclerView = MoRecyclerUtils.get(rootGroup, R.id.main_menu_tab_recycler, tabRecyclerAdapter)
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

//        tabRecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @SuppressWarnings("ConstantConditions")
//            @Override
//            public void onGlobalLayout() {
//                tabRecyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                getActivity().startPostponedEnterTransition();
//            }
//        });





        tabRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                int action = e.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
//                        LinearLayoutManager l = ((LinearLayoutManager)rv.getLayoutManager());
//                        int i = l.findLastCompletelyVisibleItemPosition();
//                        int b = l.findLastVisibleItemPosition();
//                        if(i != MoTabsManager.size()-1){
//                            rv.getParent().requestDisallowInterceptTouchEvent(true);
//                        }
                        // look at the motion

                        break;

                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


    }

    private void initTabAdapter() {
        tabRecyclerAdapter = new MoTabRecyclerAdapter(getTabs(), getContext(), false)
                .setOnTabClickListener(onTabClickListener);
    }

    private void initTabSelectable() {
        this.tabSelectable = new MoSelectable<MoTab>(getContext(), rootGroup, this.tabRecyclerAdapter)
                .setCounterView(tabSelectableToolbar.getTitle())
                .setSelectAllCheckBox(tabSelectableToolbar.getCheckBox())
                .addUnNormalViews(this.tabSelectableToolbar);
    }


    private void initMoToolbar() {
        this.moToolBar = new MoToolBar(getContext())
                .showExtraButton()
                .setMiddleIcon(R.drawable.ic_add_black_24dp)
                .setMiddleOnClickListener(view -> addPopUpMenu.show(view))
                .setRightIcon(R.drawable.ic_baseline_more_vert_24)
                .setRightOnClickListener(view -> morePopUpMenu.show(view))
                .setExtraIcon(R.drawable.ic_baseline_search_24);
        this.moToolBar.getCardView().makeTransparent();

    }

    private void initMoSearchable() {
        this.searchable = new MoSearchable(getContext(), rootGroup, MoTabsManager::getTabs)
                .setActivity(getActivity())
                //.setAppBarLayout(this.appBarLayout)
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
     *
     * @param lt list of tabs to update the
     *           adapter to
     */
    private void updateAdapter(List<MoTab> lt) {
        tabRecyclerAdapter.setDataSet(lt);
        getActivity().runOnUiThread(() -> tabRecyclerAdapter.notifyDataSetChanged());
    }

    private void initSearchBar() {
        this.searchBar = new MoSearchBar(getContext())
                .setSearchHint(R.string.tab_search_hint);
        this.searchBar.getCardView().makeTransparent();
    }

    private void initViewSync() {
        this.viewSync = new MoListViewSync(rootGroup, this.tabSelectable, this.searchable)
                .setPutOnHold(true)
                .setSharedElements(moToolBar);
    }

    /**
     * when the user clicks the middle toolbar icon
     * we show this pop up menu that we have init
     */
    private void initAddPopUpMenu() {
        addPopUpMenu = new MoPopUpMenu(getContext()).setEntries(
                new Pair<>(getContext().getString(R.string.NewTab), menuItem -> {
                    MoTabsManager.addTab(getContext(), MoSearchEngine.instance.homePage(), false);
                    getActivity().supportFinishAfterTransition();
                    return false;
                }),
                new Pair<>(getContext().getString(R.string.NewIncognitoTab), menuItem -> {
                    MoTabsManager.addPrivateTab(getContext(), MoSearchEngine.instance.homePage(), false);
                    getActivity().supportFinishAfterTransition();
                    return false;
                }));
    }

    /**
     * when the user clicks on the more button
     * this menu pops up
     */
    private void initMorePopUpMenu() {
        morePopUpMenu = new MoPopUpMenu(getContext()).setEntries(
                new Pair<String, MenuItem.OnMenuItemClickListener>(getContext().getString(R.string.Settings), menuItem -> {
                    Intent setting = new Intent(getContext(), SettingsActivity.class);
                    getActivity().startActivity(setting);
                    return false;
                }),
                new Pair<String, MenuItem.OnMenuItemClickListener>(getContext().getString(R.string.History), menuItem -> {
                    HistoryActivity.launch(getActivity(), MainMenuActivity.HISTORY_REQUEST_CODE);
                    return false;
                }),
                new Pair<String, MenuItem.OnMenuItemClickListener>(getContext().getString(R.string.Clear_All_Normal_Tabs), menuItem -> {
                    MoTabsManager.clearAllNormalTabs(getContext());
                    tabRecyclerAdapter.notifyDataSetChanged();
                    return false;
                }),
                new Pair<String, MenuItem.OnMenuItemClickListener>(getContext().getString(R.string.Clear_All_Incognito_Tabs), menuItem -> {
                    MoTabsManager.clearAllPrivateTabs(getContext());
                    // monote add this feature
                    //this.mIncognitoAdapter.notifyDataSetChanged();
                    return false;
                })
        );
    }


    public void notifyDataSetChanged() {
        tabRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onBackPressed() {

        return false;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.main_menu_tabs_fragment;
    }
}