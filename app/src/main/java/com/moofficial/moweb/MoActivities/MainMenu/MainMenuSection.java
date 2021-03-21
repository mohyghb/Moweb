package com.moofficial.moweb.MoActivities.MainMenu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomappbar.BottomAppBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoOnBackPressed;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoEmptyLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoWrappers.MoWrapperLinearLayout;
import com.moofficial.moweb.MoActivities.History.HistoryActivity;
import com.moofficial.moweb.MoActivities.MainActivity;
import com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments.NormalMainFragment;
import com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments.PrivateMainFragment;
import com.moofficial.moweb.MoActivities.MainTransitionTo;
import com.moofficial.moweb.MoActivities.SettingsActivity;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.R;

import java.util.Arrays;

@SuppressWarnings("ConstantConditions")
public class MainMenuSection extends MoEmptyLayout implements MoOnBackPressed ,MoOnTabClickListener{




    protected MoMainMenuBottomBar moToolBar;
    protected MoPopUpMenu morePopUpMenu;
    protected MoSelectable<MoTab> tabSelectable;
    protected MoToolBar tabSelectableToolbar;

    protected BottomAppBar bottomAppBar;
    protected MainTransitionTo transitionToTab;
    protected ViewPager2 pager2;
    protected AbstractStateAdapter abstractStateAdapter;

    protected Activity activity;
    protected FragmentManager fragmentManager;
    protected Lifecycle lifecycle;


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

    public MainMenuSection setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        return this;
    }

    public MainMenuSection setLifecycle(Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
        return this;
    }

    public MainMenuSection setTransitionToTab(MainTransitionTo transitionToTab) {
        this.transitionToTab = transitionToTab;
        return this;
    }

    @Override
    public void initViews() {

    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }



    public void init(){
        initUI();
        initClass();
    }

    void initUI() {
        initViewPager();
        initMoToolbar();
        initTabSelectableToolbar();
        initBottomAppbar();
        syncToolbars();
    }

    void initClass() {
        initMorePopUpMenu();
        initTabSelectable();
    }




    private void initViewPager() {
        pager2 = findViewById(R.id.main_menu_view_pager);
        abstractStateAdapter = new AbstractStateAdapter(getActivity(),
                Arrays.asList(new NormalMainFragment().setTabClickListener(this).initTabAdapter(getContext()),
                        new PrivateMainFragment().setTabClickListener(this).initTabAdapter(getContext())))
                .setOnTabClickListener(this);
        pager2.setAdapter(abstractStateAdapter);
    }

    private void initBottomAppbar(){
        bottomAppBar = findViewById(R.id.main_menu_toolbar);
    }

    private void syncToolbars() {
        MoWrapperLinearLayout w = new MoWrapperLinearLayout(findViewById(R.id.main_menu_toolbar_linear_layout));
        w.setupMultipleBars(this.moToolBar, this.moToolBar, this.tabSelectableToolbar);
    }

    private void initTabSelectableToolbar() {
        this.tabSelectableToolbar = new MoToolBar(getContext())
                .showCheckBox()
                .hideLeft()
                .setMiddleIcon(R.drawable.ic_baseline_delete_24)
                .setMiddleOnClickListener(view -> performDelete())
                .setRightIcon(R.drawable.ic_baseline_close_24)
                .setRightOnClickListener((view) -> {
                    TransitionManager.beginDelayedTransition(this);
                    if (tabSelectable.hasAction()) {
                        tabSelectable.removeAction();
                    }
                });
    }

    //monote delete private tabs as well
    private void performDelete() {
        abstractStateAdapter.performDelete();
        updateNumberOfTabs();
        if (tabSelectable.isInActionMode()) {
            tabSelectable.removeAction();
        }
    }





    private void initTabSelectable() {
        this.tabSelectable = new MoSelectable<>(getContext(), (ViewGroup) this,
                abstractStateAdapter.get())
                .setCounterView(tabSelectableToolbar.getTitle())
                .setSelectAllCheckBox(tabSelectableToolbar.getCheckBox())
                .addUnNormalViews(this.tabSelectableToolbar)
                .addNormalViews(this.moToolBar);
    }


    private void initMoToolbar() {
        // monote number of tabs should be updated
        this.moToolBar = new MoMainMenuBottomBar(getActivity())
                .setAddNormalTab(this::createNewTab)
                .setAddPrivateTab(this::createNewPrivateTab)
                .setMoreClickListener(view -> morePopUpMenu.show(view))
                .setTabsNumber(MoTabsManager.size())
                .syncWithViewPager2(pager2);
    }





    private void createNewTab() {
        MoTabsManager.addTab(getActivity(), MoSearchEngine.instance.homePage(), false);
        transitionToTab.transition();
    }

    private void createNewPrivateTab() {
        MoTabsManager.addPrivateTab(getActivity(), MoSearchEngine.instance.homePage(), false);
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
                    abstractStateAdapter.notifyDataSetChanged(0);
                    updateNumberOfTabs();
                    return false;
                }),
                new Pair<>(getContext().getString(R.string.Clear_All_Incognito_Tabs), menuItem -> {
                    MoTabsManager.clearAllPrivateTabs(getActivity());
                    abstractStateAdapter.notifyDataSetChanged(1);
                    return false;
                })
        );
    }


    public void onResume() {
        // updating the number of tabs
        updateNumberOfTabs();
        // set to current page
        setToCurrentPage();
        // updating both pages
        abstractStateAdapter.notifyAllSections();
    }

    public void updateNumberOfTabs() {
        moToolBar.setTabsNumber(MoTabsManager.size());
    }

    public void setToCurrentPage() {
        MoTab t = MoTabController.instance.getCurrent();
        if(t==null) return;
        pager2.setCurrentItem(t.isPrivate()?1:0);
        this.abstractStateAdapter.scrollTo(t);
    }




    @Override
    public void onTabClickListener(MoTab t, View sharedView, int index) {
        MoTabController.instance.setNewTab(getContext(),t);
        transitionToTab.transition();
    }

    @Override
    public int getLayoutId() {
        return R.layout.main_menu_tabs_fragment;
    }


    @Override
    public boolean onBackPressed() {
        if(tabSelectable.hasAction()) {
            tabSelectable.removeAction();
            return true;
        }else if(MoTabController.instance.isNotOutOfOptions()){
            transitionToTab.transition();
            return true;
        }
        return false;
    }
}