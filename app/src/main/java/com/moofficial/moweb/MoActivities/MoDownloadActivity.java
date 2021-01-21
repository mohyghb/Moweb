package com.moofficial.moweb.MoActivities;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoOnEmptySelectionListener;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moweb.Moweb.MoDownload.MoDownload;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadAdapter;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadManager;
import com.moofficial.moweb.R;

public class MoDownloadActivity extends MoSmartActivity implements MoDownloadAdapter.OnDownloadClickedListener {


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
//        initSearchable();
        initSync();
        syncTitle(this.mainToolbar.getTitle(), this.selectableToolbar.getTitle());
    }

    private void initToolbars() {
        this.mainToolbar = new MoToolBar(this)
                .setLeftOnClickListener((v)->onBackPressed());
        this.selectableToolbar = new MoToolBar(this)
                .showCheckBox()
                .hideLeft()
                .showRight().hideMiddle()
                .setRightIcon(R.drawable.ic_baseline_delete_outline_24)
                .setRightOnClickListener((v)-> {
                    //todo when they press delete after selecting a bunch of stuff
                });
        this.searchableToolbar = new MoSearchBar(this);

        setupMultipleToolbars(this.mainToolbar, this.mainToolbar,
                this.selectableToolbar, this.searchableToolbar);
    }

    private void initRecycler() {
        adapter = new MoDownloadAdapter(this, MoDownloadManager.getDownloads())
                .setOnDownloadClickedListener(this);
        recyclerView = new MoRecyclerView(this);
        MoRecyclerUtils.get(this.recyclerView, this.adapter);
        l.linearNested.addView(this.recyclerView);
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

    private void initSync() {
        this.sync = new MoListViewSync(getGroupRootView(), this.selectable);
        this.sync.setPutOnHold(true)
                .setSharedElements(this.mainToolbar);
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
    }


    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, MoDownloadActivity.class));
    }
}
