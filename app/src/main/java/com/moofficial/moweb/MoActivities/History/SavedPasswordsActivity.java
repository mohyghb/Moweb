package com.moofficial.moweb.MoActivities.History;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views.MoUserPassAdapter;
import com.moofficial.moweb.R;

public class SavedPasswordsActivity extends MoSmartActivity {


    private MoRecyclerView passRecycler;
    private MoUserPassAdapter passAdapter;
    private MoToolBar moToolBar;

    @Override
    protected void init() {
        setTitle(R.string.passwords);
        initPassAdapter();
        initPassRecycler();
        initMoToolbar();
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
        this.moToolBar = new MoToolBar(this)
                .setLeftOnClickListener((v)->onBackPressed());
        setupMultipleToolbars(moToolBar,moToolBar);
        syncTitle(moToolBar.getTitle());
    }

}