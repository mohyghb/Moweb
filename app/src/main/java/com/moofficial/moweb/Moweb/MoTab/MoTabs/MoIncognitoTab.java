package com.moofficial.moweb.Moweb.MoTab.MoTabs;

import android.app.Activity;
import android.view.WindowManager;

import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;

// no web view cache
// no history save
// no screen shot
public class MoIncognitoTab extends MoTab {


    public MoIncognitoTab(Activity a, String url) {
        super(a, url);
        // this makes sure that we are browsing incognito
        super.setType(MoTabType.TYPE_PRIVATE);
        // to make sure the user can not take screen shots
        a.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE,WindowManager.LayoutParams.FLAG_SECURE);
    }

    @Override
    protected void initWebView() {
        // we do not need to add anything to the search history
        // because this tab is incognito
        moWebView.setSaveHistory(false);
        super.initWebView();
    }

    @Override
    public void saveTab() {
        // no saving the tab
    }

}
