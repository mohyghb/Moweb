package com.moofficial.moweb.MoActivities.MainMenu;

import android.content.Context;
import android.util.AttributeSet;

import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.List;

public class MainMenuPrivateTabSection extends MainMenuSection {

    public MainMenuPrivateTabSection(Context context) {
        super(context);
    }

    public MainMenuPrivateTabSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MainMenuPrivateTabSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    List<MoTab> getTabs() {
        return MoTabsManager.getPrivateTabs();
    }


    @Override
    public int[] getAttrs() {
        return new int[0];
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }


}
