package com.moofficial.moweb;

import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.MoSection.MoSectionManager;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MoTabSectionManager {

    private final MainActivity mainActivity;
    private MoRecyclerView tabsRecyclerView;
    private MoRecyclerView incognitoTabsRecyclerView;
    private MoTabRecyclerAdapter mTabAdapter;
    private MoTabRecyclerAdapter mIncognitoAdapter;

    private ImageButton addTabButton;
    private ImageButton changeGridButton;
    private ImageButton moreButton;
    private View mainView;
    private Runnable changeContentView;
    private boolean showInGrid;

    private MoPopUpMenu addPopUpMenu;
    private MoPopUpMenu morePopUpMenu;


    private MoSelectable<MoTab> moTabMoSelectable;

    public MoTabSectionManager(MainActivity mainActivity,Runnable changeContentView) {
        this.mainActivity = mainActivity;
        this.mainView = mainActivity.findViewById(R.id.activity_main_include_main_menu);
        this.changeContentView = changeContentView;
        this.showInGrid = true;
        this.init();
    }

    // for controller
    void onTabsButtonPressed() {
        MoSectionManager.getInstance().setSection(MoSectionManager.TABS_VIEW);
    }

    // init this class
    void init() {
        initRecyclerView();
        initIncognitoRecyclerView();
        changeGrid();
        initAddTab();
        initGridButton();
        initMoreButton();
        initAddPopUpMenu();
        initMorePopUpMenu();
        initMoTabSelectable();
    }

    private void initMoTabSelectable(){
        this.moTabMoSelectable = new MoSelectable<>(mainActivity,mainActivity.getRootView(),mTabAdapter);
    }


    // init the adapter fro normal tabs
    private void initNormalAdapter(){
        this.mTabAdapter = new MoTabRecyclerAdapter(MoTabsManager.getTabs(),mainActivity,showInGrid);
//        MoTabsManager.setTabRecyclerAdapter(mTabAdapter);
    }

    /**
     * init the recycler view and show all the tabs inside a
     * recycler view
     */
    private void initRecyclerView() {
        initNormalAdapter();
        tabsRecyclerView = MoRecyclerUtils.get(mainView.findViewById(R.id.recycler_tabs_view), mTabAdapter)
                .setOrientation(LinearLayoutManager.HORIZONTAL)
                .setMaxHeight(mainActivity.getResources().getDisplayMetrics().heightPixels);

        tabsRecyclerView.show();
    }

    // init the adapter for incognito tabs
    private void initIncognitoAdapter(){
        this.mIncognitoAdapter = new MoTabRecyclerAdapter(MoTabsManager.getPrivateTabs(),mainActivity,showInGrid);
        //MoTabsManager.setPrivateTabAdapter(mIncognitoAdapter);
    }

    private void initIncognitoRecyclerView(){
        initIncognitoAdapter();
        incognitoTabsRecyclerView = MoRecyclerUtils.get(mainView.findViewById(R.id.recycler_incognito_tabs),mIncognitoAdapter)
                .setOrientation(LinearLayoutManager.HORIZONTAL);
        incognitoTabsRecyclerView.show();
    }

    // init add tab button
    private void initAddTab() {
        addTabButton = mainView.findViewById(R.id.add_tab_button);
        addTabButton.setOnClickListener(view -> addPopUpMenu.show(view));
    }

    private void initGridButton(){
        changeGridButton  = mainView.findViewById(R.id.grid_button);
        changeGridButton.setOnClickListener(view -> {
            showInGrid = !showInGrid;
            changeGrid();
        });
    }

    private void initMoreButton(){
        this.moreButton = mainView.findViewById(R.id.settings_button);
        this.moreButton.setOnClickListener(view -> morePopUpMenu.show(view));
    }

    /**
     * when user presses the add button
     * this pop up menu shows up
     */
    private void initAddPopUpMenu(){
        addPopUpMenu = new MoPopUpMenu(mainActivity).setEntries(
                        new Pair<>(mainActivity.getString(R.string.NewTab), menuItem -> {
                            MoTabsManager.addTab(mainActivity, MoSearchEngine.instance.homePage(),false);
                            return false;
                        }),
                        new Pair<>(mainActivity.getString(R.string.NewIncognitoTab), menuItem -> {
                            MoTabsManager.addPrivateTab(mainActivity,MoSearchEngine.instance.homePage(),false);
                            return false;
                        })
        );
    }


    /**
     * when the user clicks on the more button
     * this menu pops up
     */
    private void initMorePopUpMenu(){
        morePopUpMenu = new MoPopUpMenu(mainActivity).setEntries(
                new Pair<>(mainActivity.getString(R.string.Settings), menuItem -> {
                    Intent setting = new Intent(mainActivity,SettingsActivity.class);
                    mainActivity.startActivity(setting);
                    return false;
                }),
                new Pair<>(mainActivity.getString(R.string.History), menuItem -> {
                    Intent in = new Intent(mainActivity,HistoryActivity.class);
                    mainActivity.startActivity(in);
                    return false;
                }),
                new Pair<>(mainActivity.getString(R.string.Clear_All_Normal_Tabs), menuItem -> {
                    MoTabsManager.clearAllNormalTabs(mainActivity);
                    this.mTabAdapter.notifyDataSetChanged();
                    return false;
                }),
                new Pair<>(mainActivity.getString(R.string.Clear_All_Incognito_Tabs), menuItem -> {
                    MoTabsManager.clearAllPrivateTabs(mainActivity);
                    this.mIncognitoAdapter.notifyDataSetChanged();
                    return false;
                })
        );
    }


    /**
     * changes the grid view to list view and vice versa
     */
    private void changeGrid() {
        this.tabsRecyclerView.switchLayoutManager(showInGrid?MoRecyclerView.GRID_LAYOUT_MANAGER:
                MoRecyclerView.LINEAR_LAYOUT_MANAGER);
        this.incognitoTabsRecyclerView.switchLayoutManager(showInGrid?MoRecyclerView.GRID_LAYOUT_MANAGER:
                MoRecyclerView.LINEAR_LAYOUT_MANAGER);
    }

    public void update(){
        mTabAdapter.notifyDataSetChanged();
        mIncognitoAdapter.notifyDataSetChanged();
    }

    /**
     * returns the outside view (or main menu view)
     * of this app
     * @return
     */
    public View getMainView() {
        return mainView;
    }


}