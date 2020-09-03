package com.moofficial.moweb.MoActivities.MainMenu;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.MenuItem;

import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.List;

public class MainMenuTabSection extends MainMenuSection {

    public MainMenuTabSection(Context context) {
        super(context);
    }

    public MainMenuTabSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainMenuTabSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void init() {
        super.init();
        morePopUpMenu.addEntry(new Pair<>("Open private tabs", new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return false;
            }
        }));
    }

    @Override
    List<MoTab> getTabs() {
        return MoTabsManager.getTabs();
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }

    @Override
    public boolean onBackPressed() {
        if(viewSync.hasAction()){
            viewSync.removeAction();
        }else if(MoTabController.instance.isNotOutOfOptions()) {
            transitionToTab.transition();
        }else{
            return false;
        }
        return true;
    }
}
