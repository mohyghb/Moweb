package com.moofficial.moweb.MoActivities.Download;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.webkit.MimeTypeMap;

import androidx.core.content.FileProvider;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileExtension;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoPageProgress;
import com.moofficial.moweb.Moweb.MoDownload.MoDownload;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadAdapter;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadManager;
import com.moofficial.moweb.Moweb.MoWebManifest;
import com.moofficial.moweb.R;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2core.DownloadBlock;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MoDownloadActivity extends MoSmartActivity implements
        MoDownloadAdapter.OnDownloadClickedListener {


    private MoCardRecyclerView cardRecyclerView;
    private MoRecyclerView recyclerView;
    private MoDownloadAdapter adapter;
    private MoToolBar mainToolbar;

    private MoToolBar selectableToolbar;
    private MoSelectable<MoDownload> selectable;
    private MoSearchBar searchableToolbar;
    private MoSearchable searchable;
    private MoListViewSync sync;

    @Override
    protected void init() {
        setTitle(R.string.downloads);
        initToolbars();
        initRecycler();
        initSelectable();
        initSearchable();
        initSync();
        syncTitle(this.mainToolbar.getTitle(), this.selectableToolbar.getTitle());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!sync.hasAction()) {
            MoLog.print("onResume download activity");
            this.adapter.update(this, MoDownloadManager.getDownloads(), getGroupRootView());
        }
    }


    private void initToolbars() {
        this.mainToolbar = new MoToolBar(this)
                .setRightIcon(R.drawable.ic_baseline_settings_24)
                .setRightOnClickListener((v)-> {
                    // todo start download settings activity
                })
                .setLeftOnClickListener((v)->onBackPressed());
        this.selectableToolbar = new MoToolBar(this)
                .showCheckBox()
                .hideLeft()
                .showRight()
                .hideMiddle()
                .setRightIcon(R.drawable.ic_baseline_delete_outline_24)
                .setRightOnClickListener((v)-> {
                    MoDownloadManager.delete(this.adapter.getSelectedItems(),
                            () -> {
                                this.adapter.update(this, MoDownloadManager.getDownloads(), getGroupRootView());
                                runOnUiThread(()->this.sync.removeAction());
                            });
                });
        this.searchableToolbar = new MoSearchBar(this);

        setupMultipleToolbars(this.mainToolbar, this.mainToolbar,
                this.selectableToolbar, this.searchableToolbar);
    }

    private void initRecycler() {
        adapter = new MoDownloadAdapter(this, new ArrayList<>())
                .setOnDownloadClickedListener(this);
        this.cardRecyclerView = new MoCardRecyclerView(this);
        recyclerView = MoRecyclerUtils.get(this.cardRecyclerView.getRecyclerView(), this.adapter);

        l.linearNested.addView(this.cardRecyclerView);
        recyclerView.show();
    }

    private void initSelectable() {
        this.selectable = new MoSelectable<>(this, getGroupRootView(), this.adapter);
        this.selectable.setCounterView(this.selectableToolbar.getTitle())
                .setSelectAllCheckBox(this.selectableToolbar.getCheckBox())
                .setCounterView(this.l.title)
                .addUnNormalViews(this.selectableToolbar)
                .setOnEmptySelectionListener(() -> this.sync.removeAction());
    }

    @SuppressWarnings("unchecked")
    private void initSearchable() {
        this.searchable = new MoSearchable(this, getGroupRootView(), MoDownloadManager::getDownloads);
        this.searchable.syncWith(this.searchableToolbar)
                .setSearchButton(this.mainToolbar.getMiddleButton())
                .setActivity(this)
                .setAppBarLayout(this.l.appBarLayout)
                .setSearchOnTextChanged(true)
                .setOnSearchFinished(list -> this.adapter.update(this,
                        (List<MoDownload>) list, getGroupRootView()));
    }

    private void initSync() {
        this.sync = new MoListViewSync(getGroupRootView(), this.selectable, this.searchable);
        this.sync.setPutOnHold(true)
                .setSharedElements(this.mainToolbar)
                .setOnEmptyOnHoldsListener(()-> this.adapter.update(this,
                        MoDownloadManager.getDownloads(), getGroupRootView()));
    }


    @Override
    public void onBackPressed() {
        if (this.sync.hasAction()) {
            this.sync.removeAction();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onDownloadClicked(MoDownload download) {
        // todo open the download item
        MoLog.print(download.getName() + " was clicked");
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            String mime = MoFileExtension.getMimeType(download.getFile());
            Uri uri = FileProvider.getUriForFile(this, MoWebManifest.FILE_PROVIDER_AUTHORITY, download.getFile());
            intent.setDataAndType(uri, mime);

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // no Activity to handle this kind of files
        }
    }


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MoDownloadActivity.class));
    }
}
