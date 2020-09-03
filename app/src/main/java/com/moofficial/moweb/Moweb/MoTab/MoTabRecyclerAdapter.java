package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoDynamicUnit.MoDynamicUnit;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoPaddingBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.util.List;

public class MoTabRecyclerAdapter extends MoSelectableAdapter<MoTabRecyclerAdapter.TabViewHolder,MoTab>
        implements MoSelectableList<MoTab> {

    private static final int TAB_PADDING = MoDynamicUnit.convertDpToPixels(8f);
    private static final int TAB_PADDING_GRID_VIEW = MoDynamicUnit.convertDpToPixels(8f);
    private final float SCREEN_TO_TAB_RATIO = 1.6f;


    private boolean isInGrid;

    private MoOnTabClickListener onTabClickListener = (t, v,index) -> {};

    public MoOnTabClickListener getOnTabClickListener() {
        return onTabClickListener;
    }

    public MoTabRecyclerAdapter setOnTabClickListener(MoOnTabClickListener onTabClickListener) {
        this.onTabClickListener = onTabClickListener;
        return this;
    }



    public static class TabViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView url;
       // private LinearLayout linearLayout;
        //private MoCardView innerCard;
        private MoCardView outerCard;
        private LinearLayout coverView;
       // private LinearLayout linearLayout;

        public TabViewHolder(View v) {
            super(v);
            background = v.findViewById(R.id.web_view_bitmap);
            url = v.findViewById(R.id.tab_url_list_view);
           // linearLayout = v.findViewById(R.id.tab_mode_list_linear_layout);
           // innerCard = v.findViewById(R.id.tab_mode_list_inner_card_view);
            outerCard = v.findViewById(R.id.tab_mode_list_card_view);
            outerCard.makeCardRecRound();


           // linearLayout = v.findViewById(R.id.tab_mode_list_linear_layout);
            coverView = v.findViewById(R.id.tab_modE_list_cover_view);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoTabRecyclerAdapter(List<MoTab> dataSet,Context c,boolean isInGrid) {
        super(c,dataSet);

        this.isInGrid = isInGrid;
        setHasStableIds(true);
    }




    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = MoInflaterView.inflate(R.layout.tab_mode_list,parent.getContext());
        new MoPaddingBuilder(this.isInGrid?TAB_PADDING_GRID_VIEW:TAB_PADDING).apply(v);
        v.setLayoutParams(getRecyclerParams(RecyclerView.LayoutParams.WRAP_CONTENT,
                RecyclerView.LayoutParams.MATCH_PARENT));
        return new TabViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        MoTab tab = dataSet.get(position);

        positionTab(holder);
        onTabClickListener(holder, position, tab);
        updateUI(holder, tab);
        MoSelectableUtils.applySelectedColor(context,holder.coverView,tab);
        onLongTabClickListener(holder, position);

        holder.background.setTransitionName(tab.getTransitionName());
    }

    private void positionTab(@NonNull TabViewHolder holder) {
        int height = (int)(context.getResources().getDisplayMetrics()
                .heightPixels/ SCREEN_TO_TAB_RATIO);
        int width = (int)(context.getResources().getDisplayMetrics()
                .widthPixels/SCREEN_TO_TAB_RATIO);
        LinearLayout.LayoutParams p =new LinearLayout.LayoutParams(width,
                height);
        holder.outerCard.setLayoutParams(p);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position, @NonNull List<Object> payloads) {
        if(payloads.isEmpty()){
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // we run partial efficient update
            // the payload that is coming for sure we know it is
            // from the mo selectable class
            // so just update the selectable color
            MoSelectableUtils.applySelectedColor(context,holder.coverView,dataSet.get(position));
        }

    }

    /**
     * methods for updating the ui
     * @param holder
     * @param tab
     */
    private void updateUI(@NonNull TabViewHolder holder, MoTab tab) {
        providePreview(holder, tab);
        setTabUrl(holder, tab);
    }

    private void onLongTabClickListener(@NonNull TabViewHolder holder, int position) {
        holder.outerCard.setOnLongClickListener(view -> {
            if(isNotSelecting()){
                startSelecting(position);
                return true;
            }
            return false;
        });
    }


    private void setTabUrl(TabViewHolder holder, MoTab tab) {
        // setting the url of the current tab
        holder.url.setText(tab.getUrl());
    }


    private void onTabClickListener(TabViewHolder holder, int position, MoTab tab) {
        // going inside the tab or selecting it to be removed or shared ...
        holder.outerCard.setOnClickListener(view -> {
            if(isSelecting()){
                onSelect(position);
            }else {
                onTabClickListener.onTabClickListener(tab,holder.outerCard,position);
            }
        });
    }

    private void providePreview(TabViewHolder holder, MoTab tab) {
        // if its web view is loaded, then we just use that
        // as the preview, if not we load
        // in the image preview that we saved
//        holder.linearLayout.removeAllViews();
//        if(tab.isUpToDate()){
//            MoTabUtils.transitionToListTabMode(context,tab.getMoWebView(),holder.linearLayout);
//        }else{
//        if(tab.getWebViewBitmap()==null){
//
//        }else {
            holder.background.setImageBitmap(tab.getWebViewBitmap());
//            holder.background.setLayoutParams(new LinearLayout.LayoutParams(
//                    ViewGroup.LayoutParams.MATCH_PARENT,
//                    (int)(context.getResources().getDisplayMetrics()
//                .heightPixels/1.5f)
//            ));
            holder.background.setVisibility(View.VISIBLE);
        //}

//            holder.linearLayout.addView(holder.background);
//        }
    }

    /**
     * removes the tabs from this adapter
     * and from the main manager class
     */
    public void deleteSelectedItems() {
        MoTabsManager.removeSelectedTabs(this.selectedItems);
        dataSet.removeAll(selectedItems);
        notifyDataSetChanged();
    }
}
