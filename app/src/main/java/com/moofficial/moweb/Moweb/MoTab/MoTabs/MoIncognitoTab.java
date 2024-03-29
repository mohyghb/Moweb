package com.moofficial.moweb.Moweb.MoTab.MoTabs;

import android.app.Activity;
import android.content.Context;
import android.view.WindowManager;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;

// no web view cache
// no history save
// no screen shot
// no saving tab
public class MoIncognitoTab extends MoTab {


    public MoIncognitoTab(Context a, String url) {
        super(a, url);
        // this makes sure that we are browsing incognito
        super.setType(MoTabType.TYPE_PRIVATE);
    }

    @Override
    protected void initWebView() {
        // we do not need to add anything to the search history
        // because this tab is incognito
        super.initWebView();
        moWebView.setSaveHistory(false);
    }

    @Override
    public void saveTab() {
        // no saving the tab
        MoLog.print("private tab id: " + this.getTabId().stringify());
    }
}
