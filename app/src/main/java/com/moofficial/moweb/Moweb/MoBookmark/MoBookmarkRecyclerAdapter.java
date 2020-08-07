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
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoPreviewAdapter;
import com.moofficial.moweb.BookmarkActivity;
import com.moofficial.moweb.EditBookmarkActivity;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.List;

public class MoBookmarkRecyclerAdapter extends MoPreviewAdapter<MoBookmarkViewHolder,MoBookmark>
        implements MoSearchableList, MoSelectableList<MoBookmark> {


    private MoSelectable<MoBookmark> moSelectable;
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
    private ArrayList<MoBookmark> selectedItems = new ArrayList<>();


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
                h.url.setText(bookmark.size()+" items");
                h.title.setText(bookmark.getName());
                break;
        }
        h.imageTextLogo.setText(MoString.getSignature(bookmark.getName()));
        onClickListener(h, bookmark,i);
        onLongClickListener(h, bookmark,i);
        MoDeletableUtils.applyDeleteColor(this.context,h.coverLayout,bookmark);
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
                if(this.moSelectable.isInActionMode()){
                    return false;
                }

                MoPopUpMenu p =new MoPopUpMenu(context).setEntries(
                        new Pair<>(context.getString(R.string.share), menuItem -> {
                            MoBookmarkManager.shareBookmark(context,true,b);
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.edit), menuItem -> {
                            EditBookmarkActivity.startActivityForResult((Activity) context,b,
                                    BookmarkActivity.EDIT_BOOKMARK_REQUEST);
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.select),menuItem -> {
                            activateSelectMode(i);
                            return false;
                        })
                );
                p.show(view);
                return true;
            });
        }
    }

    private void activateSelectMode(int i){
        if(!moSelectable.isInActionMode()){
            moSelectable.activateSpecialMode();
            onSelect(i);
        }
    }



    private void onClickListener(@NonNull MoBookmarkViewHolder h, MoBookmark bookmark,int i) {
        h.cardView.setOnClickListener(view -> {
            if(moSelectable !=null && moSelectable.isInActionMode()){
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

    @NonNull
    @Override
    public MoBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  MoInflaterView.inflate(R.layout.book_mark_view_holder,parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new MoBookmarkViewHolder(v);
    }


    public void deleteSelected() {
        for(MoBookmark b: selectedItems){
            MoBookmarkManager.remove(b);
        }
        MoBookmarkManager.save(context);
    }



    @Override
    public void setListSelectable(MoSelectable<MoBookmark> moSelectable) {
        this.moSelectable = moSelectable;
    }

    //@Override
    public void onSelect(int i) {
        moSelectable.onSelect(dataSet.get(i));
        notifyItemChanged(i);
    }


    @Override
    public List<MoBookmark> getSelectedItems() {
        return selectedItems;
    }


    @Override
    public List<? extends MoSearchableItem> getSearchableItems() {
        return dataSet;
    }
}
