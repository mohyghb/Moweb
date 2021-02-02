package com.moofficial.moweb.MoActivities.Download;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.transition.TransitionManager;
import android.webkit.MimeTypeMap;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileExtension;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoPermissions.MoPermission;
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
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadUtils;
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
        MoDownloadAdapter.OnDownloadClickedListener,
        MoDownloadAdapter.OnDownloadCancelled {


    private MoCardRecyclerView cardRecyclerView;
    private MoRecyclerView recyclerView;
    private MoDownloadAdapter adapter;
    private MoToolBar mainToolbar;

    private MoToolBar selectableToolbar;
    private MoSelectable<MoDownload> selectable;
    private MoSearchBar searchableToolbar;
    private MoSearchable searchable;
    private MoListViewSync sync;
    private MoPermission permission;

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
        setupPermission();
        if (permission.checkAndRequestPermissions()) {
            if (!sync.hasAction()) {
                this.adapter.update(this, MoDownloadManager.getDownloads(), getGroupRootView());
            }
            MoDownloadManager.registerListener(this.adapter);
            MoLog.print("(listener was ADDED HERE)");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        MoDownloadManager.unregisterListener(this.adapter);
        MoLog.print("(listener was REMOVED here)");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MoPermission.MULTIPLE_PERMISSIONS_REQUEST_ID) {
            if (!MoPermission.allGranted(grantResults)) {
                failedPermission();
            }
        }
    }

    /**
     * when we fail to get access for
     * write or read memory access
     */
    private void failedPermission() {
        Toast.makeText(this, R.string.download_activity_failed_permission, Toast.LENGTH_LONG).show();
        finish();
    }

    private void setupPermission() {
        this.permission = new MoPermission(this)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }


    private void initToolbars() {
        this.mainToolbar = new MoToolBar(this)
                .hideMiddle()
                .setRightIcon(R.drawable.ic_baseline_search_24)
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
                                this.adapter.getDataSet().removeAll(this.adapter.getSelectedItems());
                                runOnUiThread(()-> {
                                    TransitionManager.beginDelayedTransition(getGroupRootView());
                                    this.adapter.notifyDataSetChanged();
                                    this.sync.removeAction();
                                });
                            });
                });
        this.searchableToolbar = new MoSearchBar(this);

        setupMultipleToolbars(this.mainToolbar, this.mainToolbar,
                this.selectableToolbar, this.searchableToolbar);
    }

    private void initRecycler() {
        adapter = new MoDownloadAdapter(this, new ArrayList<>())
                .setOnDownloadClickedListener(this)
                .setOnDownloadCancelled(this);
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
                .setSearchButton(this.mainToolbar.getRightButton())
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
        MoLog.print(download.getName() + " was clicked");
        MoDownloadUtils.openDownload(this, download.getFile());
    }

    @Override
    public void onDownloadCancelled(int position) {
        recyclerView.post(()-> {
            adapter.getDataSet().remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, adapter.getItemCount());
        });
        MoLog.print("on cancelled called on " + position);
    }


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MoDownloadActivity.class));
    }


}
