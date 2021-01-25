package com.moofficial.moweb.Moweb.MoDownload;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoSelectableAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkViewHolder;
import com.moofficial.moweb.R;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Status;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoDownloadAdapter extends MoSelectableAdapter<MoDownloadViewHolder,MoDownload>
    implements FetchListener {

    private final int NOT_FOUND_INDEX = -1;
    private OnDownloadClickedListener onDownloadClickedListener = download -> {};
    private OnDownloadCancelled onDownloadCancelled = position -> {};

    public MoDownloadAdapter(Context c, List<MoDownload> dataSet) {
        super(c, dataSet);
    }

    public MoDownloadAdapter setOnDownloadClickedListener(OnDownloadClickedListener onDownloadClickedListener) {
        this.onDownloadClickedListener = onDownloadClickedListener;
        return this;
    }

    public MoDownloadAdapter setOnDownloadCancelled(OnDownloadCancelled onDownloadCancelled) {
        this.onDownloadCancelled = onDownloadCancelled;
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
        holder.hideDownloadLayout();
        updateDownloadingHolder(holder,position, download.getDownload());
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

    private void updateDownloadingHolder(@NonNull MoDownloadViewHolder holder,
                                         int position, Download download) {
        if (download != null ) {
            updateDownloadingHolder(holder,position,download, download.getStatus());
        }
    }

    private void updateDownloadingHolder(@NonNull MoDownloadViewHolder holder,
                                         int position,Download download, Status status) {
        if (download == null)
            return;

        if (download.getProgress() == 100) {
            status = Status.COMPLETED;
        }
        MoLog.print("on update download: " + download.getStatus().toString()
                + "normal status = " + status.toString());
        switch (status) {
            case ADDED:
                holder.progressBar.setIndeterminate(true);
                break;
            case QUEUED:
            case PAUSED:
            case DOWNLOADING:
                holder.showDownloadLayout();
                holder.progressBar.setIndeterminate(false);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.progressBar.setProgress(download.getProgress(), true);
                } else {
                    holder.progressBar.setProgress(download.getProgress());
                }
                holder.description.setText(download.getDownloadedBytesPerSecond()/1024 + "KB/s");
                if (status == Status.PAUSED) {
                    holder.pause.setIcon(R.drawable.ic_baseline_play_arrow_24);
                    holder.pause.setOnClickListener((v)-> MoDownloadManager.resume(download.getId()));
                } else {
                    holder.pause.setIcon(R.drawable.ic_baseline_pause_24);
                    holder.pause.setOnClickListener((v)-> MoDownloadManager.pause(download.getId()));
                }
                break;
            case COMPLETED:
                holder.hideDownloadLayout();
                break;
            case CANCELLED:
                onDownloadCancelled.onDownloadCancelled(position);
                break;
        }
        holder.cancel.setOnClickListener((v)-> {
            MoDownloadManager.cancel(this.context, download);
            onDownloadCancelled.onDownloadCancelled(position);
        });
    }

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(@NonNull MoDownloadViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads);
        } else {
            Object o = payloads.get(0);
            if (o instanceof Object[]) {
                Object[] loads = (Object[]) o;
                updateDownloadingHolder(holder,position, (Download) loads[0], (Status)loads[1]);
            } else {
                addSelectColorToHolder(holder, dataSet.get(position));
            }
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

    public int getCorrespondingPosition(Download download) {
        String path = download.getFile();
        for (int i =0; i < this.dataSet.size(); i++) {
            if (this.dataSet.get(i).getFile().getPath().equals(path)) {
                return i;
            }
        }
        return NOT_FOUND_INDEX;
    }

    public void onUpdateDownload(Download download, Status status) {

        int index = getCorrespondingPosition(download);
        if (index == NOT_FOUND_INDEX) {
            MoLog.print("Not found index, download adapter");
        } else {
            notifyItemChanged(index, new Object[]{download, status});
        }
    }

    @Override
    public void onAdded(@NotNull Download download) {
        onUpdateDownload(download, Status.ADDED);
    }

    @Override
    public void onCancelled(@NotNull Download download) {
        onUpdateDownload(download, Status.CANCELLED);
    }

    @Override
    public void onCompleted(@NotNull Download download) {
        onUpdateDownload(download, Status.COMPLETED);
    }

    @Override
    public void onDeleted(@NotNull Download download) {

    }

    @Override
    public void onDownloadBlockUpdated(@NotNull Download download, @NotNull DownloadBlock downloadBlock, int i) {

    }

    @Override
    public void onError(@NotNull Download download, @NotNull Error error, @Nullable Throwable throwable) {

    }

    @Override
    public void onPaused(@NotNull Download download) {
        onUpdateDownload(download, Status.PAUSED);
    }

    @Override
    public void onProgress(@NotNull Download download, long l, long l1) {
        onUpdateDownload(download, Status.DOWNLOADING);
    }

    @Override
    public void onQueued(@NotNull Download download, boolean b) {

    }

    @Override
    public void onRemoved(@NotNull Download download) {

    }

    @Override
    public void onResumed(@NotNull Download download) {
        onUpdateDownload(download, Status.DOWNLOADING);
    }

    @Override
    public void onStarted(@NotNull Download download, @NotNull List<? extends DownloadBlock> list, int i) {

    }

    @Override
    public void onWaitingNetwork(@NotNull Download download) {

    }

    public interface OnDownloadClickedListener {
        void onDownloadClicked(MoDownload download);
    }

    public interface OnDownloadCancelled {
        void onDownloadCancelled(int position);
    }
}
