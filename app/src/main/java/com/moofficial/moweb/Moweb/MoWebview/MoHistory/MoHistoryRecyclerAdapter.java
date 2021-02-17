package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.MoActivities.History.HistoryActivity;
import com.moofficial.moweb.Moweb.MoTab.MoOpenTab;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MoHistoryRecyclerAdapter extends MoSelectableAdapter<MoHistoryHolder,MoHistory> implements
        MoSelectableList<MoHistory> {

    private MoPopUpMenu selectedPopup;
    private MoOnHistoryClicked onHistoryClicked  = new MoOnHistoryClicked() {
        @Override
        public void onHistoryClicked(MoHistory h, int position) {

        }

        @Override
        public void goBackToActivity() {

        }
    };


    public MoHistoryRecyclerAdapter(ArrayList<MoHistory> dataSet,Context c) {
        super(c,dataSet);
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
                String s = MoString.getSignature(history.getTitle());
                MoLog.print(s + ", isempty = " + s.isEmpty() + ", charval = " + ((int)s.charAt(0)));
                holder.moImageTextLogo
                        .setOuter()
                        .setText(MoString.getSignature(history.getTitle()))
                        .hideLogo()
                        .showText();
                makeHistoryClickable(holder, history,position);
                makeHistoryLongClickable(holder, position);
                addSelectedColor(holder,history);
                break;
        }
    }

    private void addSelectedColor(@NonNull MoHistoryHolder holder, MoHistory history) {
        if (history.isTypeHistory()) {
            holder.moImageTextLogo.onSelectFill(history, false);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MoHistoryHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
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
            if (isNotSelecting()) {
                this.selectedPopup = new MoPopUpMenu(context).setEntries(
                        new Pair<>(context.getString(R.string.open_in_tab), menuItem -> {
                            MoOpenTab.openInNewTabs(context, Collections.singletonList(dataSet.get(position)));
                            onHistoryClicked.goBackToActivity();
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.open_in_private_tab), menuItem -> {
                            MoOpenTab.openInNewPrivateTabs(context,Collections.singletonList(dataSet.get(position)));
                            onHistoryClicked.goBackToActivity();
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.select), item -> {
                            startSelecting(position);
                            return false;
                        })
                );
                this.selectedPopup.show(view);
            }
            return false;
        });
    }






    @Override
    public int getItemViewType(int position) {
        return dataSet.get(position).getType();
    }

}
