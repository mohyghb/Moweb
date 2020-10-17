package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views;

import android.content.Context;
import android.util.Pair;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoClipboard.MoClipboardUtils;
import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoUserPassHolderView;
import com.moofficial.moweb.R;

import java.util.List;

public class MoUserPassAdapter extends MoSelectableAdapter<MoUserPassHolder, MoUserPassAutoFill> {

    public MoUserPassAdapter(Context c, List<MoUserPassAutoFill> dataSet) {
        super(c, dataSet);
    }

    @NonNull
    @Override
    public MoUserPassHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MoUserPassHolderView layout = new MoUserPassHolderView(parent.getContext());
        layout.setLayoutParams(getMatchWrapParams());
        return new MoUserPassHolder(layout);
    }

    @Override
    public void onBindViewHolder(@NonNull MoUserPassHolder holder, int position) {
        MoUserPassAutoFill a = dataSet.get(position);
        int passwordLength = a.getPassword().getValue().length();
        holder.layout
                .setUsername(a.getUsername().getValue())
                .setPassword(MoString.repeat("*", passwordLength))
                .setHost(a.getHost())
                .hideCopies()
                .setOnSelectListener((v)-> {
                    if(isSelecting()) {
                        onSelect(position);
                    } else {
                        // create a menu and let them copy stuff
                        showCopyMenu(a, v);
                    }
                })
                .setOnLongSelectListener(v -> {
                    if(isNotSelecting()) {
                        startSelecting(position);
                        return true;
                    }
                    return false;
                });

        MoSelectableUtils.applySelectedColor(context,holder.layout.getCoverLayout(),a);
    }

    /**
     * deletes the selected items and updates the adapter
     */
    public void performDelete() {
        MoUserPassManager.deleteSelected(context,getSelectedItems());
        dataSet.removeAll(getSelectedItems());
        clearSelection();
    }

    /**
     * if they can see this view, that means that they have been authorized already
     * since we don't launch the activity without asking them about credentials
     * @param a auto fill to copy the values from
     * @param v view to show the menu on top of
     */
    private void showCopyMenu(MoUserPassAutoFill a, View v) {
        MoPopUpMenu copyMenu = new MoPopUpMenu(context)
                .setEntries(
                        new Pair<>(context.getString(R.string.copy_password), item -> {
                            MoClipboardUtils.add(context,a.getPassword().getValue());
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.copy_username), item -> {
                            MoClipboardUtils.add(context,a.getUsername().getValue());
                            return false;
                        }),
                        new Pair<>(context.getString(R.string.copy_host), item -> {
                            MoClipboardUtils.add(context,a.getHost());
                            return false;
                        })
                );
        copyMenu.show(v);
    }
}
