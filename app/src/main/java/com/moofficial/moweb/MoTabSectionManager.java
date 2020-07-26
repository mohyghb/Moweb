package com.moofficial.moweb;

import android.app.Activity;
import android.content.Intent;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.MoSection.MoSectionManager;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MoTabSectionManager {

    private final Activity mainActivity;
    private MoRecyclerView tabsRecyclerView;
    private MoRecyclerView incognitoTabsRecyclerView;
    private MoTabRecyclerAdapter mAdapter;
    private MoTabRecyclerAdapter mIncognitoAdapter;

    private ImageButton addTabButton;
    private ImageButton changeGridButton;
    private ImageButton moreButton;
    private View mainView;
    private Runnable changeContentView;
    private boolean showInGrid;

    private MoPopUpMenu addPopUpMenu;
    private MoPopUpMenu morePopUpMenu;

    public MoTabSectionManager(Activity mainActivity,Runnable changeContentView) {
        this.mainActivity = mainActivity;
        this.mainView = MoInflaterView.inflate(R.layout.main_menu_layout,mainActivity);
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
    }


    // init the adapter fro normal tabs
    private void initNormalAdapter(){
        this.mAdapter = new MoTabRecyclerAdapter(MoTabsManager.getTabs(),mainActivity,showInGrid);
    }

    /**
     * init the recycler view and show all the tabs inside a
     * recycler view
     */
    private void initRecyclerView() {
        initNormalAdapter();
        tabsRecyclerView = new MoRecyclerView(mainView.findViewById(R.id.recycler_tabs_view),mAdapter)
                .setOrientation(LinearLayoutManager.HORIZONTAL);
        tabsRecyclerView.show();
    }

    // init the adapter for incognito tabs
    private void initIncognitoAdapter(){
        this.mIncognitoAdapter = new MoTabRecyclerAdapter(MoTabsManager.getIncognitoTabs(),mainActivity,showInGrid);
    }

    private void initIncognitoRecyclerView(){
        initIncognitoAdapter();
        incognitoTabsRecyclerView = new MoRecyclerView(mainView.findViewById(R.id.recycler_incognito_tabs),mIncognitoAdapter)
                .setOrientation(LinearLayoutManager.HORIZONTAL);
        incognitoTabsRecyclerView.show();
        //incognitoTabsRecyclerView.scrollTo(MoTabController.instance.getIndex());
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
                            MoTabsManager.addIncognitoTab(mainActivity,MoSearchEngine.instance.homePage(),false);
                            return false;
                        }),
                        new Pair<>(mainActivity.getString(R.string.Clear_All_Normal_Tabs), menuItem -> {
                            MoTabsManager.clearAllNormalTabs(mainActivity);
                            this.tabsRecyclerView.notifyDataSetChanged();
                            return false;
                        }),
                        new Pair<>(mainActivity.getString(R.string.Clear_All_Incognito_Tabs), menuItem -> {
                            MoTabsManager.clearAllIncognitoTabs(mainActivity);
                            this.incognitoTabsRecyclerView.notifyDataSetChanged();
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
                })
        );
    }


    /**
     * changes the grid view to list view and vice versa
     */
    private void changeGrid() {
        initNormalAdapter();
        initIncognitoAdapter();
        this.tabsRecyclerView.switchViewMode(showInGrid, mAdapter);
        this.incognitoTabsRecyclerView.switchViewMode(showInGrid, mIncognitoAdapter);
    }

    /**
     * returns the outside view (or main menu view)
     * of this app
     * @return
     */
    public View getMainView() {
        this.mAdapter.notifyDataSetChanged();
        this.incognitoTabsRecyclerView.notifyDataSetChanged();
        this.tabsRecyclerView.smoothScrollTo(MoTabController.instance.getNormalIndex());
        this.incognitoTabsRecyclerView.smoothScrollTo(MoTabController.instance.getIncognitoIndex());
        return mainView;
    }

    public void onResume() {
//        MoLog.print("onResume");
//        if(this.mAdapter!=null){
//            this.mAdapter.notifyDataSetChanged();
//        }
    }

}