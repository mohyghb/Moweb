package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;

import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoTabOpenable;

import java.util.List;

// opens new tabs
public class MoOpenTab {

    /**
     *
     * @param context of the app
     * @param l list of mo tab openable
     * @param <T> whether a class can be opened inside a tab or not
     * opens all the given list of MoTabOpenable into
     * new tabs, the last one is the one that is shown to
     * the user
     */
    public static <T extends MoTabOpenable> void openInNewTabs(Context context, List<T> l) {
        if(l.isEmpty())
            return;

        for(T o : l){
            MoTabsManager.newTab(context,o.getSearchString(),null);
        }

        MoTabsManager.setCurrentTab(context,MoTabsManager.getLastTab(MoTabType.TYPE_NORMAL));
    }

    public static <T extends MoTabOpenable> void openInNewTab(Context context, T l ) {
        MoTabsManager.newTab(context,l.getSearchString(),null);
        MoTabsManager.setCurrentTab(context,MoTabsManager.getLastTab(MoTabType.TYPE_NORMAL));
    }


    /**
     *
     * @param context of the app
     * @param l list of mo tab openable
     * @param <T> whether a class can be opened inside a tab or not
     * opens all the given list of MoTabOpenable into
     * new private tabs, the last one is the one that is shown to
     * the user
     */
    public static <T extends MoTabOpenable> void openInNewPrivateTabs(Context context, List<T> l) {
        if(l.isEmpty())
            return;

        for(T o : l) {
            MoTabsManager.newPrivateTab(context,o.getSearchString(),null);
        }
        MoTabsManager.setCurrentTab(context,MoTabsManager.getLastTab(MoTabType.TYPE_PRIVATE));
    }



}
