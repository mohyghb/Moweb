package com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments;

import android.content.Context;
import android.content.res.Configuration;
import android.view.View;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewInterfaces.MoOnConfigurationChanged;
import com.moofficial.moweb.Moweb.MoTab.MoTabExceptions.MoTabNotFoundException;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.List;

// abstract fragment to show both normal and private tabs
public abstract class AbstractMainFragment {


    protected MoTabRecyclerAdapter tabRecyclerAdapter;
    protected MoOnTabClickListener tabClickListener;
    private MoRecyclerView recyclerView;


    public AbstractMainFragment setTabClickListener(MoOnTabClickListener tabClickListener) {
        this.tabClickListener = tabClickListener;
        return this;
    }

    public AbstractMainFragment setRecyclerView(MoRecyclerView recyclerView) {
        this.recyclerView = recyclerView;
        checkEmpty();
        this.recyclerView.setDynamicallyCalculateSpanCount(true);
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

    public void scrollTo(MoTab t) {
        if (this.recyclerView != null) {
            try {
                int i = MoTabsManager.getIndexOf(t);
                if (i < this.tabRecyclerAdapter.getItemCount()) {
                    this.recyclerView.scrollToPosition(i);
                }
            } catch (MoTabNotFoundException e) {
                MoLog.print("could not scroll to the tab position because of tab not found exception");
            }
        }
    }


    public void deleteSelectedItems() {
        tabRecyclerAdapter.deleteSelectedItems();
        checkEmpty();
    }

    public void notifyDataSetChanged() {
        tabRecyclerAdapter.notifyDataSetChanged();
        checkEmpty();
    }

    private void checkEmpty() {
        if (recyclerView == null)
            return;
        if (tabRecyclerAdapter.getItemCount() == 0) {
            this.recyclerView.setVisibility(View.GONE);
        } else {
            this.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    public void notifyItemInsertedLast() {
        tabRecyclerAdapter.notifyItemInsertedAtEnd();
    }

    abstract List<MoTab> getTabs();

}
