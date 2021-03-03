package com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
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
        if (this.recyclerView!=null) {
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
    }

    public void notifyDataSetChanged() {
        tabRecyclerAdapter.notifyDataSetChanged();
    }

    public void notifyItemInsertedLast() {
        tabRecyclerAdapter.notifyItemInsertedAtEnd();
    }

    abstract List<MoTab> getTabs();

}
