package com.moofficial.moweb.MoPopUpMenu;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;


public class MoPopUpMenu {

    private Context context;
    private Pair<String, MenuItem.OnMenuItemClickListener> [] menuItems;

    public MoPopUpMenu(Context c){
        this.context = c;
    }

    @SafeVarargs
    public final MoPopUpMenu setEntries(Pair<String, MenuItem.OnMenuItemClickListener>... pair){
        this.menuItems = pair;
        return this;
    }

    public MoPopUpMenu show(View anchor) {
        if(menuItems==null || menuItems.length == 0){
            // we don't show anything if menu items are empty
            return this;
        }
        PopupMenu popup = new PopupMenu(context, anchor);
        Menu menu = popup.getMenu();
        for(Pair<String, MenuItem.OnMenuItemClickListener> item: menuItems){
            MenuItem i = menu.add(item.first);
            i.setOnMenuItemClickListener(item.second);
        }
        popup.show();
        return this;
    }

}
