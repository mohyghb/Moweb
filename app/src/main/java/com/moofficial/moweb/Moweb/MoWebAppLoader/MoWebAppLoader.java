package com.moofficial.moweb.Moweb.MoWebAppLoader;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoCache.MoCache;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoAnimation;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces.MoJsInput;

import java.io.IOException;

public class MoWebAppLoader {

    public static void loadApp(Context context){
        // init home pages
        MoLog.printRunTime("home pages",()-> MoHomePageManager.load(context));
        // init bookmarks
        MoLog.printRunTime("bookmarks",()-> MoBookmarkManager.load(context));
        // init animations
        MoAnimation.initAllAnimations(context);

        // init tabs and tab controller
        MoLog.printRunTime("load state ", new Runnable() {
            @Override
            public void run() {
                // load back all the tabs
                MoLog.printRunTime("tabs",()-> MoTabsManager.load(context));
                // load the tab controller
                MoTabController.instance.load("",context);

            }
        });


        // init history
        MoLog.printRunTime("history", () -> {
            try {
                MoHistoryManager.load(context);
//                for(int i = 0; i < 300;i++){
//                    MoHistoryManager.add(this, MoSearchEngine.instance.getURL(i+""),i+"");
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        MoCache.printCache(context);

        loadJsScripts(context);

    }

    private static void loadJsScripts(Context context) {
        MoJsInput.loadScript(context);
    }


}
