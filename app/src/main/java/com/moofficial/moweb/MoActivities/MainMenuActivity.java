package com.moofficial.moweb.MoActivities;

import android.os.Bundle;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoWindow.MoWindowTransitions;
import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoTransitions.MoCircularTransition;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MainMenuActivity extends MoSmartActivity implements MoOnTabClickListener {


    private MoCardRecyclerView tabCardRecycler;
    private MoTabRecyclerAdapter tabRecyclerAdapter;
    private MoRecyclerView tabRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MoWindowTransitions.apply(new MoCircularTransition(),this);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void init() {
        initUI();
        initClass();
    }

    private void initUI() {
        initTabCard();
    }



    private void initClass() {
        tabRecyclerAdapter = new MoTabRecyclerAdapter(MoTabsManager.getTabs(),this,true)
                .setOnTabClickListener(this);
        tabRecyclerView = MoRecyclerUtils.get(tabCardRecycler.getRecyclerView(),tabRecyclerAdapter)
                .setMaxHeight(getHeightPixels())
                .setLayoutManagerType(MoRecyclerView.GRID_LAYOUT_MANAGER)
                .show();
    }


    private void initTabCard() {
        tabCardRecycler = new MoCardRecyclerView(this);
        linearNested.addView(tabCardRecycler, MoMarginBuilder.getLinearParams(0,8,0,0));
    }

    @Override
    public void onClickListener(MoTab t, int index) {
        // when the user clicks on a tab, this is activated
        MoTabController.instance.setIndex(index,t.getType());
        supportFinishAfterTransition();
    }
}