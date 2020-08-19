package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;

import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoTabOpenable;

import java.util.List;

// opens new tabs
public class MoOpenTab {

    /**
     *
     * @param context
     * @param l
     * @param <T>
     */
    public static <T extends MoTabOpenable> void openInNewTabs(Context context, List<T> l) {
        for(T o : l){
            MoTabsManager.addTab(context,o.getSearchString(),false);
        }
    }

}
