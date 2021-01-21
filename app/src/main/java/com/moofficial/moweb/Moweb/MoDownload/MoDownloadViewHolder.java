package com.moofficial.moweb.Moweb.MoDownload;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoLogo;
import com.moofficial.moweb.R;

public class MoDownloadViewHolder extends RecyclerView.ViewHolder {
    public MoLogo logo;
    public TextView name,description;
    public MoCardView card;
    public MoDownloadViewHolder(@NonNull View itemView) {
        super(itemView);
        this.logo = itemView.findViewById(R.id.download_holder_logo);
        this.logo.filledCircle();
        this.name = itemView.findViewById(R.id.download_holder_title);
        this.description = itemView.findViewById(R.id.download_holder_description);
        this.card = itemView.findViewById(R.id.download_holder_card);
    }
}
