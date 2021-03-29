package com.moofficial.moweb.Moweb.MoWebAppLoader;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoAnimation;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;

import java.io.IOException;

//import com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces.MoJsInput;

public class MoWebAppLoader {

    public static void loadApp(Context context) {
        // init home pages
        MoHomePageManager.load(context);
        // init bookmarks
        MoBookmarkManager.load(context);
        // init animations
        MoAnimation.initAllAnimations(context);
        // init tabs and tab controller
        MoTabsManager.load(context);
        // load the tab controller
        MoTabController.instance.load("", context);
        // init history
        try {
            MoHistoryManager.load(context);
        } catch (IOException ignore) {
        }
        // setup download manager
        MoDownloadManager.setUp(context);
    }


}
