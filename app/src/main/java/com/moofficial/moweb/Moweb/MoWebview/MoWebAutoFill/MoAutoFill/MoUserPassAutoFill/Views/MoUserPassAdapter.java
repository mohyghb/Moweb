package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoUserPassHolderView;

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
                .setOnSelectListener((v)-> {
                    if(isSelecting()) {
                        onSelect(position);
                    } else {
                        // open for view in another activity
                    }
                })
                .setOnLongSelectListener(v -> {
                    if(isNotSelecting()) {
                        // todo not working currently
                        startSelecting(position);
                        return true;
                    }
                    return false;
                });
        MoSelectableUtils.applySelectedColor(context,holder.layout,a);
    }
}
