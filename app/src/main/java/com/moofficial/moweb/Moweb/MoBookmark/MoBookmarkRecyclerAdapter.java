package com.moofficial.moweb.Moweb.MoBookmark;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPreviewable.MoPreviewAdapter;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoDelete.MoDeleteUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoDelete.MoListDeletable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoDelete.MoListDelete;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSelectable.MoSelectableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSelectable.MoSelectableUtils;
import com.moofficial.moweb.BookmarkActivity;
import com.moofficial.moweb.EditBookmarkActivity;
import com.moofficial.moweb.R;

import java.util.List;

public class MoBookmarkRecyclerAdapter extends MoPreviewAdapter<MoBookmarkViewHolder,MoBookmark>
        implements MoListDeletable, MoSearchableList {


    private MoListDelete moListDelete;
    private Context context;
    private boolean disableLongClick = false;
    private MoOnOpenBookmarkListener openBookmarkListener = new MoOnOpenBookmarkListener() {
        @Override
        public void openFolder(MoBookmark folder) {

        }

        @Override
        public void openBookmark(MoBookmark bookmark) {

        }
    };


    public MoBookmarkRecyclerAdapter(Context context, List<MoBookmark> dataSet) {
        super(dataSet);
        this.context = context;
    }

    public MoOnOpenBookmarkListener getOpenBookmarkListener() {
        return openBookmarkListener;
    }

    public MoBookmarkRecyclerAdapter setOpenBookmarkListener(MoOnOpenBookmarkListener openBookmarkListener) {
        this.openBookmarkListener = openBookmarkListener;
        return this;
    }

    public boolean isDisableLongClick() {
        return disableLongClick;
    }

    public MoBookmarkRecyclerAdapter setDisableLongClick(boolean disableLongClick) {
        this.disableLongClick = disableLongClick;
        return this;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onBindViewHolderDifferentVersion(@NonNull MoBookmarkViewHolder h, int i) {
        MoBookmark bookmark = dataSet.get(i);

        if (wasDeleted(h, bookmark,i)) return;

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
        h.imageTextLogo.setText(MoString.getSignature(bookmark.getName()));
        onClickListener(h, bookmark,i);
        onLongClickListener(h, bookmark,i);
        MoDeleteUtils.applyDeleteColor(this.context,h.coverLayout,bookmark);
    }

    private boolean wasDeleted(@NonNull MoBookmarkViewHolder h, MoBookmark bookmark,int po) {
        if(!bookmark.isSavable()){
            // make it so that the user thinks it's gone (or deleted for the time being)
            // then with the next load, everything will be right
            h.itemView.setVisibility(View.GONE);
            return true;
        }else{
            h.itemView.setVisibility(View.VISIBLE);
        }
        return false;
    }

    private void onLongClickListener(@NonNull MoBookmarkViewHolder h, MoBookmark b,int i) {
        if(!disableLongClick){
            h.cardView.setOnLongClickListener(view -> {
                MoPopUpMenu p =new MoPopUpMenu(context).setEntries(
                        new Pair<>(context.getString(R.string.share), menuItem -> {
                            MoBookmarkManager.shareBookmark(context,b);
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.delete), menuItem -> {
                            activateDeleteMode(i);
                            return false;
                        })
                );
                if(!b.isFolder()){
                    p.addEntry( new Pair<>(context.getString(R.string.edit), menuItem -> {
                        EditBookmarkActivity.startActivityForResult((Activity) context,b,
                                BookmarkActivity.EDIT_BOOKMARK_REQUEST);
                        return false;
                    }));
                }
                p.show(view);
                return true;
            });
        }
    }

    private void activateDeleteMode(int i) {
        if(!moListDelete.isInActionMode()){
            moListDelete.setDeleteMode(true);
            onSelect(i);
        }
    }


    private void onClickListener(@NonNull MoBookmarkViewHolder h, MoBookmark bookmark,int i) {
        h.cardView.setOnClickListener(view -> {
            if(moListDelete!=null && moListDelete.isInActionMode()){
                onSelect(i);
            }else{
                if(bookmark.isFolder()){
                    openBookmarkListener.openFolder(bookmark);
                }else{
                    openBookmarkListener.openBookmark(bookmark);
                }
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
                MoBookmarkManager.remove(dataSet.get(i));
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

    @Override
    public List<? extends MoSearchableItem> getSearchableItems() {
        return dataSet;
    }
}
