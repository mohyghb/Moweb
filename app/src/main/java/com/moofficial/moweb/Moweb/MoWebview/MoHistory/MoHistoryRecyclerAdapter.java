package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoPreviewSelectableAdapter;
import com.moofficial.moweb.R;

import java.util.ArrayList;

public class MoHistoryRecyclerAdapter extends MoPreviewSelectableAdapter<MoHistoryHolder,MoHistory> implements
        MoSelectableList<MoHistory> {



    private Context context;
    private MoSelectable<MoHistory> selectable;


    public MoHistoryRecyclerAdapter(ArrayList<MoHistory> dataSet,Context c) {
        super(dataSet);
        this.context = c;
        setHasStableIds(true);
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
    protected void onBindViewHolderDifferentVersion(@NonNull MoHistoryHolder holder, int position, int i1) {
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
                makeHistoryClickable(holder, i1);
                makeHistoryLongClickable(holder, i1);
                MoSelectableUtils.applySelectedColor(context,holder.cover,history);
                break;
        }
    }

    private void makeHistoryClickable(@NonNull MoHistoryHolder holder, int position) {
        holder.cardView.setOnClickListener(view -> {
            if(selectable.isInActionMode()){
                onSelect(position);
            }else{
                // todo open the history inside their browser
            }
        });
    }

    private void makeHistoryLongClickable(@NonNull MoHistoryHolder holder, int position) {
        holder.cardView.setOnLongClickListener(view -> {
            if(!selectable.isInActionMode()){
                selectable.activateSpecialMode();
                onSelect(position);
                return true;
            }
            return false;
        });
    }




    @Override
    public int getItemViewType(int position) {
        return dataSet.get(getCorrectPosition(position)).getType();
    }


    @Override
    public void setListSelectable(MoSelectable<MoHistory> moSelectable) {
        this.selectable = moSelectable;
    }

    @Override
    public void onSelect(int i) {
        this.selectable.onSelect(dataSet.get(getCorrectPosition(i)),i);
    }
}
