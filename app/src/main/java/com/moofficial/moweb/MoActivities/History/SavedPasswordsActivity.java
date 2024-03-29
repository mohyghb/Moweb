package com.moofficial.moweb.MoActivities.History;

import android.transition.TransitionManager;

import androidx.preference.PreferenceManager;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoDrawable.MoDrawableBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoSwitchButton;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views.MoUserPassAdapter;
import com.moofficial.moweb.R;

import java.util.List;

public class SavedPasswordsActivity extends MoSmartActivity {


    private MoCardRecyclerView cardRecyclerView;
    private MoRecyclerView passRecycler;
    private MoUserPassAdapter passAdapter;
    private MoToolBar moToolBar;

    private MoSelectable<MoUserPassAutoFill> selectable;
    private MoToolBar selectToolbar;

    private MoSearchBar searchToolbar;
    private MoSearchable searchable;
    private List<MoUserPassAutoFill> allAutoFills;

    private MoSwitchButton switchButton;

    private MoListViewSync sync;

    @Override
    protected void init() {
        allAutoFills = MoUserPassManager.get();
        setTitle(R.string.passwords);
        initTurnOffButton();
        initPassAdapter();
        initPassRecycler();
        initSelectToolbar();
        initSearchToolbar();
        initMoToolbar();
        initSelectable();
        initSearchable();
        initSync();
    }

    private void initTurnOffButton() {
        this.switchButton = new MoSwitchButton(this)
                .setOnSwitchChanged(v -> {
                    PreferenceManager.getDefaultSharedPreferences(this)
                            .edit()
                            .putBoolean(getString(R.string.passwords_enabled), this.switchButton.isChecked())
                            .apply();
                    MoUserPassManager.updatePreference(this);
                    updateSwitchText();
                });
        this.switchButton.setChecked(MoUserPassManager.enabled);
        updateSwitchText();
        this.switchButton.getCardView().makeCardRound().removeElevation();
        this.l.linearNested.addView(this.switchButton, MoMarginBuilder.getLinearParams(this,0,8,0,8));
    }

    private void updateSwitchText() {
        TransitionManager.beginDelayedTransition(switchButton.getCardView());
        this.switchButton.setText(switchButton.isChecked()?"On":"Off");
        if(switchButton.isChecked()) {
            this.switchButton.getLayout().setBackground(new MoDrawableBuilder(this)
                    .rectangle()
                    .roundRadius()
                    .strokeColor(R.color.colorPrimary)
                    .strokeWidth(6)
                    .withColor(R.color.transparent)
                    .build());
        } else {
            this.switchButton.getLayout().setBackground(null);
        }
    }

    private void initCardRecyclerView() {
        this.cardRecyclerView = new MoCardRecyclerView(this).makeCardRound();
    }



    private void initSelectToolbar() {
        this.selectToolbar = new MoToolBar(this);
        this.selectToolbar
                .showCheckBox()
                .hideMiddle()
                .hideLeft()
                .setRightIcon(R.drawable.ic_baseline_delete_outline_24)
                .setRightOnClickListener((v) -> {

                    TransitionManager.beginDelayedTransition(getGroupRootView());
                    passAdapter.performDelete();
                    passRecycler.post(()->passAdapter.notifyDataSetChanged());
                    sync.removeAction();
                });
    }



    private void initPassAdapter() {
        this.passAdapter = new MoUserPassAdapter(this, allAutoFills);
    }

    private void initPassRecycler() {
        initCardRecyclerView();
        this.passRecycler = MoRecyclerUtils.get(cardRecyclerView.getRecyclerView(),passAdapter)
                .setMaxHeight(getHeightPixels())
                .show();
        this.l.linearNested.addView(cardRecyclerView, MoMarginBuilder.getLinearParams(this,0,8,0,0));
    }

    private void initMoToolbar() {
        this.moToolBar = new MoToolBar(this);
        this.moToolBar.setLeftOnClickListener((v)->onBackPressed())
                .setMiddleOnClickListener((v) -> searchable.activateSearch());

        setupMultipleToolbars(moToolBar,moToolBar,selectToolbar,searchToolbar);
        syncTitle(moToolBar.getTitle(),selectToolbar.getTitle());
    }

    private void initSelectable() {
        this.selectable = new MoSelectable<>(this, getGroupRootView(), passAdapter);
        this.selectable.setCounterView(l.title)
                .setOnEmptySelectionListener(() -> {
                    // cancel selection when empty
                    sync.removeAction();
                })
                .setSelectAllCheckBox(selectToolbar.getCheckBox())
                .addUnNormalViews(selectToolbar);
    }

    private void initSearchToolbar() {
        this.searchToolbar = new MoSearchBar(this).setSearchHint(R.string.search_username_host);
    }

    private void initSearchable() {
        this.searchable = new MoSearchable(this, getGroupRootView(), () -> allAutoFills);
        this.searchable.setActivity(this)
                .setAppBarLayout(l.appBarLayout)
                .addUnNormalViews(searchToolbar)
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    updateAdapter((List<MoUserPassAutoFill>) list);
                })
                .setSearchOnTextChanged(true)
                .setSearchTextView(searchToolbar.getEditText())
                .setClearSearch(searchToolbar.getRightButton())
                .setCancelButton(searchToolbar.getLeftButton());
    }

    private void updateAdapter(List<MoUserPassAutoFill> list) {
        passAdapter.setDataSet(list);
        passRecycler.post(()-> {
            TransitionManager.beginDelayedTransition(getGroupRootView());
            passAdapter.notifyDataSetChanged();
        });
    }

    private void initSync() {
        sync = new MoListViewSync(getGroupRootView(),selectable,searchable)
                .setPutOnHold(true)
                .setSharedElements(moToolBar, this.switchButton);
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