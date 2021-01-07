package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.R;

import java.util.List;

public class MoBookmarkRecyclerAdapter extends MoSelectableAdapter<MoBookmarkViewHolder,MoBookmark>
        implements MoSearchableList, MoSelectableList<MoBookmark> {



    private boolean disableLongClick = false;
    private boolean disableSelectColor = false;
    private MoOnOpenBookmarkListener openBookmarkListener = new MoOnOpenBookmarkListener() {
        @Override
        public void openFolder(MoBookmark folder) {

        }

        @Override
        public void openBookmark(MoBookmark bookmark) {

        }
    };


    public MoBookmarkRecyclerAdapter(Context context, List<MoBookmark> dataSet) {
        super(context,dataSet);
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

    public MoBookmarkRecyclerAdapter setDisableSelectColor(boolean disableSelectColor) {
        this.disableSelectColor = disableSelectColor;
        return this;
    }

    @NonNull
    @Override
    public MoBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  MoInflaterView.inflate(R.layout.book_mark_view_holder,parent.getContext());
        v.setLayoutParams(getMatchWrapParams());
        return new MoBookmarkViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull MoBookmarkViewHolder h, int i) {
        MoBookmark bookmark = dataSet.get(i);
        h.imageTextLogo.setOuter();
        switch (bookmark.getType()) {
            case MoBookmark.BOOKMARK:
                h.url.setText(bookmark.getUrl());
                h.title.setText(bookmark.getName());
                h.imageTextLogo.setText(MoString.getSignature(bookmark.getName()))
                        .hideLogo()
                        .showText();
                break;
            case MoBookmark.FOLDER:
                h.url.setText(bookmark.size() + " items");
                h.title.setText(bookmark.getName());
                h.imageTextLogo
                        .showLogoHideText()
                        .setInner(ContextCompat.getDrawable(context,R.drawable.ic_baseline_folder_open_24));
                break;
        }

        onClickListener(h, bookmark,i);
        onLongClickListener(h, bookmark,i);
        addSelectColorToHolder(h, bookmark);

    }

    private void addSelectColorToHolder(@NonNull MoBookmarkViewHolder h, MoBookmark bookmark) {
        if(!disableSelectColor) {
            h.imageTextLogo.onSelectFill(bookmark, bookmark.isFolder());
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MoBookmarkViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // 100 percent this is the select payload
            addSelectColorToHolder(holder,dataSet.get(position));
        }
    }

    private void onLongClickListener(@NonNull MoBookmarkViewHolder h, MoBookmark b, int i) {
        if (!disableLongClick) {
            h.cardView.setOnLongClickListener(view -> {
                if (this.selectable.isInActionMode()) {
                    return false;
                }
                activateSelectMode(i);
                return true;
            });
        }
    }

    private void activateSelectMode(int i){
        if(!selectable.isInActionMode()){
            selectable.activateSpecialMode();
            onSelect(i);
        }
    }



    private void onClickListener(@NonNull MoBookmarkViewHolder h, MoBookmark bookmark,int i) {
        h.cardView.setOnClickListener(view -> {
            if(isSelecting()){
                onSelect(i);
            }
            else{
                if(bookmark.isFolder()){
                    openBookmarkListener.openFolder(bookmark);
                }else{
                    openBookmarkListener.openBookmark(bookmark);
                }
            }
        });
    }


    /**
     * delete all the selected items
     */
    public void deleteSelected() {
        MoBookmarkManager.deleteSelectedBookmarks(this.context,this.selectedItems);
        this.dataSet.removeAll(selectedItems);
    }






    @Override
    public List<? extends MoSearchableItem> getSearchableItems() {
        return dataSet;
    }


}
