package com.moofficial.moweb.Moweb.MoTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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


    public static class TabViewHolder extends RecyclerView.ViewHolder {

        private View tab;
        private ImageView background;
        private CardView deleteButton;
        private TextView url;

        public TabViewHolder(View v) {
            super(v);
            background = v.findViewById(R.id.web_view_bitmap);
            deleteButton = v.findViewById(R.id.tab_delete_button);
            url = v.findViewById(R.id.tab_url_list_view);
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

    // Create new views (invoked by the layout manager)
    @Override
    public TabViewHolder onCreateViewHolder(ViewGroup parent,
                                            int viewType) {
        View v = MoInflaterView.inflate(R.layout.tab_mode_list,parent.getContext());
        if(!this.isInGrid){
//            RecyclerView.LayoutParams lp;
//            lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//            v.setLayoutParams(lp);
            v.setPadding(TAB_PADDING,TAB_PADDING,TAB_PADDING,TAB_PADDING);
        }else{
            v.setPadding(TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW,TAB_PADDING_GRID_VIEW);
        }
        return new TabViewHolder(v);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(TabViewHolder holder, int position) {
        // replacing the view inside tabViewHolder with the tab at position
//        MoViewUtils.replaceView(holder.tab,
//                tabs.get(position).getPaddingView(TAB_PADDING,position,holder, () -> onRemove(position)));
        holder.background.setImageBitmap(tabs.get(position).getWebViewBitmap());
        holder.background.setOnClickListener(view -> {
            // open up
            MoTabController.instance.setIndex(position,tabs.get(position).getType());
        });

        holder.deleteButton.setOnClickListener(view -> {
                onRemove(position);
        });
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
