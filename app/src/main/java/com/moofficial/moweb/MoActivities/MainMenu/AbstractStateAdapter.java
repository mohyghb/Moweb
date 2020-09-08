package com.moofficial.moweb.MoActivities.MainMenu;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoRecyclerAdapter;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.MoActivities.MainMenu.MainMenuFragments.AbstractMainFragment;
import com.moofficial.moweb.Moweb.MoTab.MoTabRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.R;

import java.util.List;

public class AbstractStateAdapter extends MoRecyclerAdapter<AbstractViewHolder,AbstractMainFragment> {


    private MoOnTabClickListener onTabClickListener;

    public AbstractStateAdapter(Context c, List<AbstractMainFragment> dataSet) {
        super(c, dataSet);
    }


    public AbstractStateAdapter setOnTabClickListener(MoOnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }


    @NonNull
    @Override
    public AbstractViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View a = MoInflaterView.inflate(R.layout.abstract_main_fragment,context);
        a.setLayoutParams(getRecyclerParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.MATCH_PARENT));
        return new AbstractViewHolder(a);
    }

    @Override
    public void onBindViewHolder(@NonNull AbstractViewHolder holder, int position) {
        AbstractMainFragment item = dataSet.get(position);
        item.setTabClickListener(this.onTabClickListener);

        MoRecyclerUtils.get(holder.recyclerView,item.getTabRecyclerAdapter())
                .setSpanCount(2)
                .setLayoutManagerType(MoRecyclerView.STAGGERED_GRID_LAYOUT_MANAGER)
                .show();
    }


    public MoTabRecyclerAdapter[] get() {
        MoTabRecyclerAdapter[] ab = new MoTabRecyclerAdapter[dataSet.size()];
        for(int i =0; i < ab.length; i++){
            ab[i] = dataSet.get(i).getTabRecyclerAdapter();
        }
        return ab;
    }


    public void performDelete(){
        for(AbstractMainFragment f: dataSet) {
            f.deleteSelectedItems();
        }
    }

    /**
     * notifies the current tab
     */
    public void notifyDataSetChanged(int position) {
        dataSet.get(position).notifyDataSetChanged();
    }

    public void notifyAllSections() {
        for(AbstractMainFragment f: dataSet) {
            f.notifyDataSetChanged();
        }
    }

}
