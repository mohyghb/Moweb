package com.moofficial.moweb.MoActivities.History;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views.MoUserPassAdapter;
import com.moofficial.moweb.R;

public class SavedPasswordsActivity extends MoSmartActivity {


    private MoRecyclerView passRecycler;
    private MoUserPassAdapter passAdapter;
    private MoToolBar moToolBar;

    private MoSelectable<MoUserPassAutoFill> selectable;
    private MoToolBar selectToolbar;
    private MoListViewSync sync;

    @Override
    protected void init() {
        setTitle(R.string.passwords);
        initPassAdapter();
        initPassRecycler();

        initSelectToolbar();
        initMoToolbar();
        initSelectable();


        initSync();

    }

    private void initSync() {
        sync = new MoListViewSync(getGroupRootView(),selectable)
                .setPutOnHold(true)
                .setSharedElements(moToolBar);
    }

    private void initSelectToolbar() {
        this.selectToolbar = new MoToolBar(this);
        this.selectToolbar
                .showCheckBox()
                .hideMiddle()
                .hideRight()
                .hideLeft()
                .getCardView()
                .makeTransparent();
    }

    private void initPassAdapter() {
        this.passAdapter = new MoUserPassAdapter(this, MoUserPassManager.get());
    }

    private void initPassRecycler() {
        this.passRecycler = new MoRecyclerView(this);
        this.passRecycler = MoRecyclerUtils.get(passRecycler,passAdapter)
                .setMaxHeight(getHeightPixels())
                .show();
        l.linearNested.addView(passRecycler);
    }

    private void initMoToolbar() {
        this.moToolBar = new MoToolBar(this);
        this.moToolBar.setLeftOnClickListener((v)->onBackPressed())
                .getCardView()
                .makeTransparent();

        setupMultipleToolbars(moToolBar,moToolBar,selectToolbar);
        syncTitle(moToolBar.getTitle(),selectToolbar.getTitle());
    }

    private void initSelectable() {
        this.selectable = new MoSelectable<>(this, getGroupRootView(), passAdapter);
        this.selectable.setCounterView(l.title)
                .setSelectAllCheckBox(selectToolbar.getCheckBox())
                .addUnNormalViews(selectToolbar);
    }

    @Override
    public void onBackPressed() {
        if (sync.hasAction()) {
            sync.removeAction();
        } else {
            super.onBackPressed();
        }
    }
}