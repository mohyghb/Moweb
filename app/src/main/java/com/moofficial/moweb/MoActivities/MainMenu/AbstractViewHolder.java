package com.moofficial.moweb.MoActivities.MainMenu;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoWebview.MoEmptyLayoutView;
import com.moofficial.moweb.R;

public class AbstractViewHolder extends RecyclerView.ViewHolder {

    public MoRecyclerView recyclerView;
    public MoEmptyLayoutView emptyLayoutView;

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
        recyclerView = itemView.findViewById(R.id.main_menu_tab_recycler);
        emptyLayoutView = itemView.findViewById(R.id.main_menu_empty_view);
    }
}
