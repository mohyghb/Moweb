package com.moofficial.moweb.Moweb.MoBookmark;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoDelete.MoListDeletable;
import com.moofficial.moessentials.MoEssentials.MoDelete.MoListDelete;
import com.moofficial.moessentials.MoEssentials.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoPreviewable.MoPreviewAdapter;
import com.moofficial.moessentials.MoEssentials.MoSelectable.MoSelectableItem;
import com.moofficial.moessentials.MoEssentials.MoSelectable.MoSelectableUtils;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageViewHolder;
import com.moofficial.moweb.R;

import java.util.List;

public class MoBookmarkRecyclerAdapter extends MoPreviewAdapter<MoBookmarkViewHolder,MoBookmark> implements MoListDeletable {


    private MoListDelete moListDelete;
    private Context context;

    public MoBookmarkRecyclerAdapter(Context context, List<MoBookmark> dataSet) {
        super(dataSet);
        this.context = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolderDifferentVersion(@NonNull MoBookmarkViewHolder h, int i) {
        MoBookmark bookmark = dataSet.get(i);
        switch (bookmark.getType()){
            case MoBookmark.BOOKMARK:
                h.url.setText(bookmark.getUrl());
                h.title.setText(bookmark.getName());
                break;
            case MoBookmark.FOLDER:
                h.url.setText(bookmark.subBookMarkSize()+" items");
                h.title.setText(bookmark.getName());
                break;
        }
        onClickListener(h, i);
        onLongClickListener(h, i);
    }

    private void onLongClickListener(@NonNull MoBookmarkViewHolder h, int i) {
        h.cardView.setOnLongClickListener(view -> {
            if(!moListDelete.isInActionMode()){
                moListDelete.setDeleteMode(true);
                onSelect(i);
            }
            return false;
        });
    }

    private void onClickListener(@NonNull MoBookmarkViewHolder h, int i) {
        h.cardView.setOnClickListener(view -> {
            if(moListDelete.isInActionMode()){
                onSelect(i);
            }else{
                // Todo
                //  open this bookmark into a tab
            }
        });
    }

    @NonNull
    @Override
    public MoBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  MoInflaterView.inflate(R.layout.book_mark_view_holder,parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new MoBookmarkViewHolder(v);
    }

    @Override
    public void setMoDelete(MoListDelete moListDelete) {
        this.moListDelete = moListDelete;
    }

    @Override
    public void notifySituationChanged() {
        notifyDataSetChanged();
    }

    @Override
    public void deleteSelected() {
        for(int i = dataSet.size() - 1;i>=0;i--){
            if(dataSet.get(i).isSelected()){
                dataSet.remove(i);
            }
        }
        MoBookmarkManager.save(context);
    }

    @Override
    public int size() {
        return getItemCount();
    }

    @Override
    public void selectAllElements() {
        MoSelectableUtils.selectAllItems(dataSet,this.moListDelete);
    }

    @Override
    public void deselectAllElements() {
        MoSelectableUtils.deselectAllItems(dataSet,this.moListDelete);
    }

    @Override
    public void onSelect(int i) {
        moListDelete.notifySizeChange(dataSet.get(i).onSelect());
        notifyItemChanged(i);
    }

    @Override
    public List<? extends MoSelectableItem> getSelectedItems() {
        return dataSet;
    }
}
