package com.moofficial.moweb.Moweb.MoHomePage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoAnimation.MoAnimation;
import com.moofficial.moessentials.MoEssentials.MoDelete.MoListDeletable;
import com.moofficial.moessentials.MoEssentials.MoDelete.MoListDelete;
import com.moofficial.moessentials.MoEssentials.MoPreviewable.MoPreviewAdapter;
import com.moofficial.moessentials.MoEssentials.MoSelectable.MoSelectableItem;
import com.moofficial.moessentials.MoEssentials.MoSelectable.MoSelectableUtils;
import com.moofficial.moweb.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.R;

import java.util.List;

public class MoHomePageRecyclerAdapter extends MoPreviewAdapter<MoHomePageViewHolder,MoHomePage> implements MoListDeletable {


    private Context context;
    private MoListDelete moListDelete;

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
        applyColorDelete(holder, homePage);

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

    private void applyColorDelete(@NonNull MoHomePageViewHolder holder, MoHomePage homePage) {
        if(homePage.isSelected()){
            holder.coverLayout.setBackgroundColor(context.getColor(R.color.delete_item_low_transparency_color));
        }else{
            holder.coverLayout.setBackgroundColor(context.getColor(R.color.transparent));
        }
    }

    @NonNull
    @Override
    public MoHomePageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  MoInflaterView.inflate(R.layout.home_page_view_holder,parent.getContext());
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        v.setLayoutParams(lp);
        return new MoHomePageViewHolder(v);
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
        MoHomePageManager.deleteAllSelected(this.context);
    }

    @Override
    public int size() {
        return this.getItemCount();
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
        if(moListDelete!=null && moListDelete.isInActionMode()){
            // then this should select the sms
            moListDelete.notifySizeChange(dataSet.get(i).onSelect());
            notifyItemChanged(i);
        }
    }

    @Override
    public List<? extends MoSelectableItem> getSelectedItems() {
        return dataSet;
    }
}
