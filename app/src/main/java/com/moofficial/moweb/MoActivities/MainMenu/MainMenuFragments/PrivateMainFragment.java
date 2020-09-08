package com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments;

import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.List;

public class PrivateMainFragment extends AbstractMainFragment {


    @Override
    List<MoTab> getTabs() {
        return MoTabsManager.getPrivateTabs();
    }
}
