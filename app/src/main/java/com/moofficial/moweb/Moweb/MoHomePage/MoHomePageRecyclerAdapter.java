package com.moofficial.moweb.Moweb.MoHomePage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoAnimation;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletableInterface.MoListDeletable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoPreviewSelectableAdapter;
import com.moofficial.moweb.R;

import java.util.List;

public class MoHomePageRecyclerAdapter extends MoPreviewSelectableAdapter<MoHomePageViewHolder,MoHomePage>
        implements MoListDeletable<MoHomePage> {


    private Context context;
    private MoDeletable<MoHomePage> moListDelete;


    public MoHomePageRecyclerAdapter(Context context, List<MoHomePage> dataSet) {
        super(dataSet);
        this.context = context;
    }

    @Override
    protected void onBindViewHolderDifferentVersion(@NonNull MoHomePageViewHolder holder, int position) {
        MoHomePage homePage = dataSet.get(position);
        holder.urlTextView.setText(homePage.getUrl());
        holder.radioButton.setChecked(homePage.isActivated());
        hideRadioButton(holder);
        radioButtonPress(holder, position);
        pressCard(holder, position);
        longPressCard(holder,position);
        MoDeletableUtils.applyDeleteColor(this.context,holder.coverLayout,homePage);
    }

    private void hideRadioButton(@NonNull MoHomePageViewHolder holder) {
        if(moListDelete.isInActionMode()){
            MoAnimation.animateNoTag(holder.radioButton, View.INVISIBLE,MoAnimation.FADE_OUT);
        }else{
            MoAnimation.animateNoTag(holder.radioButton,View.VISIBLE,MoAnimation.FADE_IN);
        }
    }

    private void radioButtonPress(@NonNull MoHomePageViewHolder holder, int position) {
        holder.radioButton.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!compoundButton.isPressed()){
                return;
            }
            activateThisHomePage(position);
        });
    }

    private void activateThisHomePage(int position) {
        MoHomePageManager.activate(context, position);
        notifyItemRangeChanged(0, getItemCount());
    }

    private void pressCard(@NonNull MoHomePageViewHolder holder, int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // activate deactivate select or choosing this home page
                if(moListDelete.isInActionMode()){
                    onSelect(position);
                }else{
                    activateThisHomePage(position);
                }
            }
        });
    }

    private void longPressCard(@NonNull MoHomePageViewHolder holder,int position) {
        holder.cardView.setOnLongClickListener(view -> {
            // this is to activate the delete mode
            if(!moListDelete.isInActionMode()){
                moListDelete.setDeleteMode(true);
                onSelect(position);
            }
            return false;
        });
    }

    @NonNull
    @Override
    public MoHomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  MoInflaterView.inflate(R.layout.home_page_view_holder,parent.getContext());
        v.setLayoutParams(getMatchWrapParams());
        return new MoHomePageViewHolder(v);
    }




    @Override
    public void setListSelectable(MoSelectable<MoHomePage> s) {}

    @Override
    public void setMoDelete(MoDeletable<MoHomePage> moDeletable) {
        this.moListDelete = moDeletable;
    }

    @Override
    public void deleteSelected() {
        MoHomePageManager.deleteAllSelected(this.context);
    }

    @Override
    public int size() {
        return this.getItemCount();
    }


    @Override
    public void onSelect(int i) {
        moListDelete.onSelect(dataSet.get(i),i);
    }
}
