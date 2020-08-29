package com.moofficial.moweb.Moweb.MoHomePage;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoUI.MoDrawable.MoDrawableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletableInterface.MoListDeletable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.R;

import java.util.List;

public class MoHomePageRecyclerAdapter extends MoSelectableAdapter<MoHomePageViewHolder,MoHomePage>
        implements MoListDeletable<MoHomePage> {


    private final String PAYLOAD_UPDATE_LOGO = "logo";


    public MoHomePageRecyclerAdapter(Context context, List<MoHomePage> dataSet) {
        super(context,dataSet);
    }





    private void activateThisHomePage(int position) {
        MoHomePageManager.activate(context, position);
        notifyItemRangeChanged(0, getItemCount(),PAYLOAD_UPDATE_LOGO);
    }

    private void pressCard(@NonNull MoHomePageViewHolder holder, int position) {
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 // activate deactivate select or choosing this home page
                if(isSelecting()){
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
            if(isNotSelecting()) {
               startSelecting(position);
               notifyDataSetChanged();
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
    public void onBindViewHolder(@NonNull MoHomePageViewHolder holder, int position) {
        MoHomePage homePage = dataSet.get(position);
        holder.urlTextView.setText(homePage.getUrl());
        handleLogo(holder, homePage);
        pressCard(holder, position);
        longPressCard(holder,position);
        MoSelectableUtils.applySelectedColor(this.context,holder.coverLayout,dataSet.get(position));
    }

    private void handleLogo(@NonNull MoHomePageViewHolder holder, MoHomePage homePage) {
        if(homePage.isActivated()){
            holder.moLogo.filledCircle();
        }else{
            holder.moLogo.setLogoDrawable(MoDrawableUtils.outlineCircle(this.context));
        }
    }


    @Override
    public void onBindViewHolder(@NonNull MoHomePageViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        }else{

            String payload = (String)payloads.get(0);
            switch (payload){
                case PAYLOAD_UPDATE_LOGO:
                    handleLogo(holder,dataSet.get(position));
                    break;
                case MoSelectable.PAYLOAD_SELECTED_ITEM:
                    MoSelectableUtils.applySelectedColor(this.context,holder.coverLayout,dataSet.get(position));
                    break;
            }
        }
    }




    @Override
    public void deleteSelected() {
        MoHomePageManager.deleteAllSelected(this.context);
    }

}
