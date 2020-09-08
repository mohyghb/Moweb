package com.moofficial.moweb.MoActivities.MainMenu;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.R;

public class AbstractViewHolder extends RecyclerView.ViewHolder {

    MoRecyclerView recyclerView;

    public AbstractViewHolder(@NonNull View itemView) {
        super(itemView);
        recyclerView = itemView.findViewById(R.id.main_menu_tab_recycler);
    }
}
