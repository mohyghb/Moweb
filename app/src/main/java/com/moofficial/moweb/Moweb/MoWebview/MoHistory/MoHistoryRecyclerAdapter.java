package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.moofficial.moessentials.MoEssentials.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.R;

import java.util.ArrayList;

public class MoHistoryRecyclerAdapter extends RecyclerView.Adapter<MoHistoryHolder> {



    private ArrayList<MoHistory> dataSet;
    private Context context;




    public MoHistoryRecyclerAdapter(ArrayList<MoHistory> dataSet,Context c) {
        this.dataSet = dataSet;
        this.context = c;
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public MoHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType){
            case MoHistory.TYPE_HISTORY:
                v = MoInflaterView.inflate(R.layout.history_tile,parent.getContext());
                break;
            default:
                v = MoInflaterView.inflate(R.layout.history_date_tile,parent.getContext());
                break;
        }
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new MoHistoryHolder(v,viewType);
    }


    @Override
    public void onBindViewHolder(@NonNull MoHistoryHolder holder, int position) {
        switch (holder.type){
            case MoHistory.TYPE_DATE_TILE:
                holder.date.setText(dataSet.get(position).getDate());
                break;
            case MoHistory.TYPE_HISTORY:
                MoHistory history = dataSet.get(position);
                holder.urlTextView.setText(history.getLimitedUrl());
                holder.dateTimeTextView.setText(history.getDate());
                holder.titleTextView.setText(history.getLimitedTitle());
                holder.firstLetter.setText(history.getSignatureLetter());
                break;
        }

    }

    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }




    private void onRemove(int position){
        MoHistoryManager.remove(position,context);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, MoTabsManager.size());
    }



}
