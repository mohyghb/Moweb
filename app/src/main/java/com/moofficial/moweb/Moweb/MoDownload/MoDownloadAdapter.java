package com.moofficial.moweb.Moweb.MoDownload;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;

import java.util.List;

public class MoDownloadAdapter extends MoSelectableAdapter<MoDownloadViewHolder,MoDownload> {

    public MoDownloadAdapter(Context c, List<MoDownload> dataSet) {
        super(c, dataSet);
    }

    @NonNull
    @Override
    public MoDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull MoDownloadViewHolder holder, int position) {

    }
}
