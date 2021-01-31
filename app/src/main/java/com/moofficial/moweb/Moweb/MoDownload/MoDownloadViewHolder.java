package com.moofficial.moweb.Moweb.MoDownload;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoImageButton;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoLogo;
import com.moofficial.moweb.R;

public class MoDownloadViewHolder extends RecyclerView.ViewHolder {
    public MoLogo logo;
    public TextView name,description, speed;
    public MoCardView card;
    public ConstraintLayout downloadLayout;
    public ProgressBar progressBar;
    public MoImageButton pause, cancel;

    public MoDownloadViewHolder(@NonNull View itemView) {
        super(itemView);
        this.logo = itemView.findViewById(R.id.download_holder_logo);
        this.logo.filledCircle();
        this.name = itemView.findViewById(R.id.download_holder_title);
        this.description = itemView.findViewById(R.id.download_holder_description);
        this.card = itemView.findViewById(R.id.download_holder_card);
        this.downloadLayout = itemView.findViewById(R.id.download_holder_download_layout);
        this.progressBar = itemView.findViewById(R.id.download_holder_progress);
        this.progressBar.setMax(100);
        this.pause = itemView.findViewById(R.id.download_holder_pause);
        this.cancel = itemView.findViewById(R.id.download_holder_cancel);
        this.speed = itemView.findViewById(R.id.download_holder_speed);
    }

    public void showDownloadLayout() {
        this.downloadLayout.setVisibility(View.VISIBLE);
        this.description.setVisibility(View.GONE);
    }
    public void hideDownloadLayout() {
        this.downloadLayout.setVisibility(View.GONE);
        this.description.setVisibility(View.VISIBLE);
    }
}
