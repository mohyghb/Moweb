package com.moofficial.moweb.Moweb.MoDownload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkViewHolder;
import com.moofficial.moweb.R;

import java.util.List;

public class MoDownloadAdapter extends MoSelectableAdapter<MoDownloadViewHolder,MoDownload> {

    private OnDownloadClickedListener onDownloadClickedListener = download -> {};

    public MoDownloadAdapter(Context c, List<MoDownload> dataSet) {
        super(c, dataSet);
    }

    public MoDownloadAdapter setOnDownloadClickedListener(OnDownloadClickedListener onDownloadClickedListener) {
        this.onDownloadClickedListener = onDownloadClickedListener;
        return this;
    }

    @NonNull
    @Override
    public MoDownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v =  MoInflaterView.inflate(R.layout.download_view_holder,parent.getContext());
        v.setLayoutParams(getMatchWrapParams());
        return new MoDownloadViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MoDownloadViewHolder holder, int position) {
        MoDownload download = dataSet.get(position);
        handleLogo(holder, download);
        holder.name.setText(download.getName());
        holder.description.setText(download.getDescription());
        onClickListener(holder, position, download);
        onLongClickListener(holder, position);
        addSelectColorToHolder(holder,download);
    }

    private void onClickListener(@NonNull MoDownloadViewHolder holder, int position, MoDownload download) {
        holder.card.setOnClickListener(v -> {
            if (isSelecting()) {
                onSelect(position);
            } else {
                onDownloadClickedListener.onDownloadClicked(download);
            }
        });
    }

    private void onLongClickListener(@NonNull MoDownloadViewHolder holder, int position) {
        holder.card.setOnLongClickListener(v -> {
            if (isNotSelecting()) {
                startSelecting(position);
                return true;
            } else {
                return false;
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull MoDownloadViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            addSelectColorToHolder(holder, dataSet.get(position));
        }
    }

    private void addSelectColorToHolder(@NonNull MoDownloadViewHolder h, MoDownload download) {
        h.logo.onSelectFill(download, true);
    }

    private void handleLogo(@NonNull MoDownloadViewHolder holder, MoDownload download) {
        if (download.getType() != null) {
            Drawable logoDrawable;
            switch (download.getType()) {
                case VIDEO:
                    logoDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_videocam_24);
                    break;
                case MUSIC:
                    logoDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_music_note_24);
                    break;
                case IMAGE:
                    logoDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_image_24);
                    break;
                default:
                    logoDrawable = ContextCompat.getDrawable(context, R.drawable.ic_baseline_insert_drive_file_24);
                    break;
            }
            holder.logo.setInner(logoDrawable).showLogoHideText();
        } else {
            holder.logo.hideLogo().showText();
        }
        holder.logo.setText(MoString.getSignature(download.getName()));
    }

    public interface OnDownloadClickedListener {
        void onDownloadClicked(MoDownload download);
    }
}
