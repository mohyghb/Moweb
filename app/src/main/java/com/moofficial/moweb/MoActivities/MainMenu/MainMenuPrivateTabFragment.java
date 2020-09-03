package com.moofficial.moweb.MoActivities.MainMenu;

import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.List;

public class MainMenuPrivateTabFragment extends MainMenuFragment {
    @Override
    List<MoTab> getTabs() {
        return MoTabsManager.getPrivateTabs();
    }
}
