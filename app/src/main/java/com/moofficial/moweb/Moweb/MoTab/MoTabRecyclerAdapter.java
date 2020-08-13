package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoPreviewSelectableAdapter;
import com.moofficial.moweb.MoSection.MoSectionManager;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.util.ArrayList;

public class MoTabRecyclerAdapter extends MoPreviewSelectableAdapter<MoTabRecyclerAdapter.TabViewHolder,MoTab>
        implements MoSelectableList<MoTab> {

    private static final int TAB_PADDING = 54;
    private static final int TAB_PADDING_GRID_VIEW = 24;



    private Context context;
    private boolean isInGrid;
    private MoSelectable<MoTab> selectable;

    @Override
    public void setListSelectable(MoSelectable<MoTab> moSelectable) {
        this.selectable = moSelectable;
    }

    @Override
    public void onSelect(int i) {
        selectable.onSelect(dataSet.get(i),i);
    }


    public static class TabViewHolder extends RecyclerView.ViewHolder {

        private ImageView background;
        private TextView url;
        private LinearLayout linearLayout;
        private CardView cardView;

        public TabViewHolder(View v) {
            super(v);
            background = v.findViewById(R.id.web_view_bitmap);
            url = v.findViewById(R.id.tab_url_list_view);
            linearLayout = v.findViewById(R.id.tab_mode_list_linear_layout);
            cardView = v.findViewById(R.id.tab_mode_list_inner_card_view);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoTabRecyclerAdapter(ArrayList<MoTab> myDataset,Context c,boolean isInGrid) {
        super(myDataset);
        this.context = c;
        this.isInGrid = isInGrid;
    }




    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = MoInflaterView.inflate(R.layout.tab_mode_list,parent.getContext());
        if(!this.isInGrid){
            v.setPadding(TAB_PADDING,TAB_PADDING,TAB_PADDING,TAB_PADDING);
        }else{
            v.setPadding(TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW);
        }
        return new TabViewHolder(v);
    }


    @Override
    protected void onBindViewHolderDifferentVersion(@NonNull TabViewHolder holder, int position) {
        MoTab tab = dataSet.get(position);
        makeTabAware(position, tab);
        if(MoSectionManager.getInstance().isInTabView()){
            return;
        }
        MoLog.print("recycler adapter: " + position);
        onTabClickListener(holder, position, tab);
        updateUI(holder, tab);
        applySelectedBackground(holder, tab);
        onLongTabClickListener(holder, position);
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
        holder.cardView.setOnLongClickListener(view -> {
            if(!selectable.isInActionMode()){
                selectable.activateSpecialMode();
                onSelect(position);
            }
            return false;
        });
    }

    private void applySelectedBackground(@NonNull TabViewHolder holder, MoTab tab) {
        if(tab.isSelected()){
            holder.url.setBackgroundColor(context.getColor(R.color.error_color));
           // new MoCardBuilder(context).setBackgroundColorId(R.color.error_color).build(holder.cardView);
        }else{
           // new MoCardBuilder(context).setBackgroundColorId(R.color.transparent).build(holder.cardView);
            holder.url.setBackgroundColor(context.getColor(R.color.transparent));
        }
    }


    private void setTabUrl(TabViewHolder holder, MoTab tab) {
        // setting the url of the current tab
        holder.url.setText(tab.getUrl());
    }

    private void makeTabAware(int position, MoTab tab) {
        // updating the preview whenever it is possible
        tab.setOnBitmapUpdateListener(() -> {
            notifyItemChanged(position);
        });
    }

    private void onTabClickListener(TabViewHolder holder, int position, MoTab tab) {
        // going inside the tab
        holder.cardView.setOnClickListener(view -> {
            if(selectable.isInActionMode()){
                onSelect(position);
            }else{
                MoTabController.instance.setIndex(position,
                        tab.getType());
            }

        });
    }

    private void providePreview(TabViewHolder holder, MoTab tab) {
        // if its web view is loaded, then we just use that
        // as the preview, if not we load
        // in the image preview that we saved
        holder.linearLayout.removeAllViews();
        if(tab.isUpToDate()){
            MoTabUtils.transitionToListTabMode(context,tab.getMoWebView(),holder.linearLayout);
        }else{
            holder.background.setImageBitmap(tab.getWebViewBitmap());
            holder.background.setVisibility(View.VISIBLE);
            holder.linearLayout.addView(holder.background);
        }
    }


//    private void onRemove(int position){
//        synchronized (tabs){
//            if(position < getItemCount()){
//                MoTabsManager.remove(this.context,position,tabs.get(position).getType());
//                notifyItemRemoved(position);
//                notifyItemRangeChanged(position,getItemCount());
//            }
//        }
//    }



}
