package com.moofficial.moweb.Moweb.MoTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.util.ArrayList;

public class MoTabRecyclerAdapter extends RecyclerView.Adapter<MoTabRecyclerAdapter.TabViewHolder> {

    private static final int TAB_PADDING = 54;
    private static final int TAB_PADDING_GRID_VIEW = 24;



    private final ArrayList<MoTab> tabs;
    private Context context;
    private boolean isInGrid;
    private ViewGroup rootView;


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

        public void removeView(){
            ((ViewGroup) itemView).removeAllViews();
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoTabRecyclerAdapter(ArrayList<MoTab> myDataset,Context c,boolean isInGrid) {
        this.tabs = myDataset;
        this.context = c;
        this.isInGrid = isInGrid;
    }

    public ViewGroup getRootView() {
        return rootView;
    }

    public MoTabRecyclerAdapter setRootView(ViewGroup rootView) {
        this.rootView = rootView;
        return this;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        View v = MoInflaterView.inflate(R.layout.tab_mode_list,parent.getContext());
        if(!this.isInGrid){
            v.setPadding(TAB_PADDING,TAB_PADDING,TAB_PADDING,TAB_PADDING);
        }else{
            v.setPadding(TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW);
        }
        return new TabViewHolder(v);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(TabViewHolder holder, int position) {
        MoTab tab = tabs.get(position);

        // going inside the tab
        holder.cardView.setOnClickListener(view -> {
            MoTabController.instance.setIndex(position,
                    tab.getType());
        });

        // updating the bitmap whenever it is possible
        tab.setOnBitmapUpdateListener(() -> {
            if(!tab.isUpToDate() || tab.getMoWebView().isPaused()){
                // only update if we are showing the bitmap currently
                notifyItemChanged(position);
            }
        });


        // if its web view is loaded, then we just use that
        // as the preview, if not we load
        // in the image preview that we saved
        holder.linearLayout.removeAllViews();
        if(tab.isUpToDate() && !tab.getMoWebView().isPaused()){
            MoTabUtils.transitionToListTabMode(context,tab.getMoWebView(),holder.linearLayout,rootView);
        }else{
            holder.background.setImageBitmap(tabs.get(position).getWebViewBitmap());
            holder.background.setVisibility(View.VISIBLE);
            holder.linearLayout.addView(holder.background);
        }

        // setting the url of the current tab
        holder.url.setText(tabs.get(position).getUrl());
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return tabs.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }




    private void onRemove(int position){
        synchronized (tabs){
            if(position < getItemCount()){
                MoTabsManager.remove(this.context,position,tabs.get(position).getType());
                notifyItemRemoved(position);
                notifyItemRangeChanged(position,getItemCount());
            }
        }
    }



}
