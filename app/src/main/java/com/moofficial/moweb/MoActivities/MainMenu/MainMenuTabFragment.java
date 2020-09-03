package com.moofficial.moweb.MoActivities.MainMenu;

import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.List;

public class MainMenuTabFragment extends MainMenuFragment {
    @Override
    List<MoTab> getTabs() {
        return MoTabsManager.getTabs();
    }
}
