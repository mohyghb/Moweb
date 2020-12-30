package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.List;

public class MoHistoryRecyclerAdapter extends MoSelectableAdapter<MoHistoryHolder,MoHistory> implements
        MoSelectableList<MoHistory> {


    private MoOnHistoryClicked onHistoryClicked = (h, position) -> {};


    public MoHistoryRecyclerAdapter(ArrayList<MoHistory> dataSet,Context c) {
        super(c,dataSet);
        setHasStableIds(true);
    }

    public MoOnHistoryClicked getOnHistoryClicked() {
        return onHistoryClicked;
    }

    public MoHistoryRecyclerAdapter setOnHistoryClicked(MoOnHistoryClicked onHistoryClicked) {
        this.onHistoryClicked = onHistoryClicked;
        return this;
    }

    @NonNull
    @Override
    public MoHistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        if (viewType == MoHistory.TYPE_HISTORY) {
            v = MoInflaterView.inflate(R.layout.history_tile, parent.getContext());
        } else {
            v = MoInflaterView.inflate(R.layout.history_date_tile, parent.getContext());
        }
        v.setLayoutParams(getMatchWrapParams());
        return new MoHistoryHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull MoHistoryHolder holder, int position) {
        MoHistory history = dataSet.get(position);
        switch (holder.type){
            case MoHistory.TYPE_DATE:
                holder.date.setText(history.getDate());
                break;
            case MoHistory.TYPE_HISTORY:
                holder.urlTextView.setText(history.getUrl());
                holder.dateTimeTextView.setText(history.getDate());
                holder.titleTextView.setText(history.getTitle());
                holder.moImageTextLogo.setText(history.getSignatureLetter());
                makeHistoryClickable(holder, history,position);
                makeHistoryLongClickable(holder, position);
                addSelectedColor(holder, history);
                break;
        }
    }

    private void addSelectedColor(@NonNull MoHistoryHolder holder, MoHistory history) {
        holder.moImageTextLogo.onSelectFill(history);
    }

    @Override
    public void onBindViewHolder(@NonNull MoHistoryHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // selected payload
            addSelectedColor(holder,dataSet.get(position));
        }

    }

    private void makeHistoryClickable(@NonNull MoHistoryHolder holder, MoHistory h, int position) {
        holder.cardView.setOnClickListener(view -> {
            if(isSelecting()){
                onSelect(position);
            }else {
                onHistoryClicked.onHistoryClicked(h,position);
            }
        });
    }

    private void makeHistoryLongClickable(@NonNull MoHistoryHolder holder, int position) {
        holder.cardView.setOnLongClickListener(view -> {
            if(isNotSelecting()){
                startSelecting(position);
                return true;
            }
            return false;
        });
    }




    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }

}
