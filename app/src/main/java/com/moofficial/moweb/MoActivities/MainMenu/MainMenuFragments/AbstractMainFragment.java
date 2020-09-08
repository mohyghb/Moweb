package com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments;

import android.content.Context;

import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;

import java.util.List;

// abstract fragment to show both normal and private tabs
public abstract class AbstractMainFragment {


    protected MoTabRecyclerAdapter tabRecyclerAdapter;
    protected MoOnTabClickListener tabClickListener;


    public AbstractMainFragment setTabClickListener(MoOnTabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
        return this;
    }

    public MoTabRecyclerAdapter getTabRecyclerAdapter() {
        return tabRecyclerAdapter;
    }



    public AbstractMainFragment initTabAdapter(Context context) {
        tabRecyclerAdapter = new MoTabRecyclerAdapter(getTabs(), context, true)
                .setOnTabClickListener(tabClickListener);
        return this;
    }


    public void deleteSelectedItems() {
        tabRecyclerAdapter.deleteSelectedItems();
    }

    public void notifyDataSetChanged() {
        tabRecyclerAdapter.notifyDataSetChanged();
    }

    public void notifyItemInsertedLast() {
        tabRecyclerAdapter.notifyItemInsertedAtEnd();
    }

    abstract List<MoTab> getTabs();

}
