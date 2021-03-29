package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoDynamicUnit.MoDynamicUnit;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewInterfaces.MoOnSizeChanged;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.util.List;

public class MoTabRecyclerAdapter extends MoSelectableAdapter<MoTabRecyclerAdapter.TabViewHolder, MoTab>
        implements MoSelectableList<MoTab> {

    private static final float TAB_PADDING = 8f;
    private static final float TAB_PADDING_GRID_VIEW = 8f;
    private final float SCREEN_TO_TAB_RATIO = 2.65f;


    private boolean isInGrid;

    private MoOnTabClickListener onTabClickListener = (t, v, index) -> {
    };

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
        private MoCardView outerCard;
        private View coverView;
        View border;

        public TabViewHolder(View v) {
            super(v);
            background = v.findViewById(R.id.web_view_bitmap);
            url = v.findViewById(R.id.tab_url_list_view);
            outerCard = v.findViewById(R.id.tab_mode_list_card_view);
            coverView = v.findViewById(R.id.tab_modE_list_cover_view);
            border = v.findViewById(R.id.tab_mode_list_border);
        }

    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoTabRecyclerAdapter(List<MoTab> dataSet, Context c, boolean isInGrid) {
        super(c, dataSet);

        this.isInGrid = isInGrid;
        setHasStableIds(true);
    }


    @NonNull
    @Override
    public TabViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = MoInflaterView.inflate(R.layout.tab_mode_list, parent.getContext());
        //new MoPaddingBuilder(this.isInGrid?TAB_PADDING_GRID_VIEW:TAB_PADDING).apply(v);
        v.setLayoutParams(getRecyclerParams(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT));
        return new TabViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position) {
        MoTab tab = dataSet.get(position);

        positionTab(holder);
        onTabClickListener(holder, position, tab);
        updateUI(holder, tab);
        MoSelectableUtils.applySelectedColor(context, holder.coverView, tab);
        onLongTabClickListener(holder, position);

        holder.background.setTransitionName(tab.getTransitionName());
    }

    private void positionTab(@NonNull TabViewHolder holder) {
        int height = (int) (context.getResources().getDisplayMetrics()
                .heightPixels / SCREEN_TO_TAB_RATIO);
//        int width = (int)(context.getResources().getDisplayMetrics()
//                .widthPixels/SCREEN_TO_TAB_RATIO);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                height);
        int padding = MoDynamicUnit.convertDpToPixels(this.context, TAB_PADDING_GRID_VIEW);
        p.setMargins(padding, padding, padding, padding);
        holder.outerCard.setLayoutParams(p);
    }

    @Override
    public void onBindViewHolder(@NonNull TabViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            // we run partial efficient update
            // the payload that is coming for sure we know it is
            // from the mo selectable class
            // so just update the selectable color
            String payload = (String) payloads.get(0);
            if (MoOnSizeChanged.ON_SIZE_CHANGED.equals(payload)) {
                positionTab(holder);
            } else {
                MoSelectableUtils.applySelectedColor(context, holder.coverView, dataSet.get(position));
            }
        }

    }

    /**
     * methods for updating the ui
     *
     * @param holder
     * @param tab
     */
    private void updateUI(@NonNull TabViewHolder holder, MoTab tab) {
        providePreview(holder, tab);
        setTabUrl(holder, tab);
        toggleBorder(holder, tab);
    }

    private void toggleBorder(@NonNull TabViewHolder holder, MoTab tab) {
        if (MoTabController.instance.currentIs(tab)) {
            holder.border.setVisibility(View.VISIBLE);
        } else {
            holder.border.setVisibility(View.GONE);
        }
    }

    private void onLongTabClickListener(@NonNull TabViewHolder holder, int position) {
        holder.outerCard.setOnLongClickListener(view -> {
            if (isNotSelecting()) {
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
            if (isSelecting()) {
                onSelect(position);
            } else {
                onTabClickListener.onTabClickListener(tab, holder.outerCard, position);
            }
        });
    }

    private void providePreview(TabViewHolder holder, MoTab tab) {
        if (tab.getWebViewBitmap() == null) {
            holder.background.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_baseline_public_24));
            holder.background.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            holder.background.setImageBitmap(tab.getWebViewBitmap());
            holder.background.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        holder.background.setVisibility(View.VISIBLE);
    }

    /**
     * removes the tabs from this adapter
     * and from the main manager class
     */
    public void deleteSelectedItems() {
        MoTabsManager.removeSelectedTabs(context, this.selectedItems);
        dataSet.removeAll(selectedItems);
        notifyEmptyState();
        notifyDataSetChanged();
    }
}
