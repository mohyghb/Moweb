package com.moofficial.moweb.MoActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoShare.MoShareUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moweb.Moweb.MoTab.MoOpenTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoOnHistoryClicked;
import com.moofficial.moweb.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends MoSmartActivity implements MoOnHistoryClicked {


    // history
    private MoRecyclerView historyRecyclerView;
    private MoHistoryRecyclerAdapter historyRecyclerAdapter;
    private MoCardRecyclerView cardRecyclerView;
    private MoToolBar moToolBar;

    private MoSelectable<MoHistory> selectable;
    private MoToolBar selectBar;
    private MoListViewSync sync;
    private MoSearchBar searchBar;
    private MoSearchable searchable;

    // toolbar
    private MoPopUpMenu morePopUp;
    private MoPopUpMenu selectedPopup;



    private ArrayList<MoHistory> allHistories;

    @Override
    protected void init() {
        initUI();
        initClass();
    }

    private void initUI(){
        setTitle(R.string.history);
        initRecyclerCardView();
        initSearchBar();
        initMoToolbar();
        initSelectBar();
        setupToolbars();

    }

    private void initRecyclerCardView() {
        cardRecyclerView = new MoCardRecyclerView(this);
        cardRecyclerView.getCardView().makeCardRecRound();
        l.linearNested.addView(cardRecyclerView);
    }



    private void setupToolbars() {
        syncTitle(moToolBar.getTitle(),selectBar.getTitle());
        setupMultipleToolbars(moToolBar,moToolBar,selectBar,searchBar);
    }

    private void initSearchBar(){
        this.searchBar = new MoSearchBar(this);
        this.searchBar.setSearchHint(R.string.history_search_hint)
                .getCardView()
                .makeTransparent();
    }

    private void initSelectBar() {
        selectBar = new MoToolBar(this)
                .hideLeft()
                .showCheckBox()
                .showExtraButton()
                .setMiddleIcon(R.drawable.ic_baseline_share_24)
                .setMiddleOnClickListener(view -> performShareSelected())
                .setMiddleOnClickListener(view -> performShareSelected())
                .setExtraIcon(R.drawable.ic_baseline_delete_24)
                .setExtraOnClickListener(view -> performDeleteHistory())
                .setRightOnClickListener(view -> {
                    if(nothingHasBeenSelected()) return;
                    selectedPopup.show(view);
                });
        selectBar.getCardView().makeTransparent();
    }

    /**
     * shares all the selected items
     */
    private void performShareSelected() {
        if (nothingHasBeenSelected()) return;
        MoShareUtils.share(HistoryActivity.this,
                historyRecyclerAdapter.getSelectedItems(),true);
    }

    private boolean nothingHasBeenSelected() {
        if(historyRecyclerAdapter.selectionIsEmpty()) {
            Toast.makeText(this,R.string.empty_selection_message,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    /**
     * deletes the selected histories from
     * the array adapter and performs various
     * updates
     * toasts the error message if something goes wrong
     */
    private void performDeleteHistory() {
        if(nothingHasBeenSelected()) return;
        try {
            // remove it from files, and the data set shown inside the recycler adapter
            MoHistoryManager.remove(this,historyRecyclerAdapter.getSelectedItems(),historyRecyclerAdapter.getDataSet());
            // notify the adapter that we have changed the data set
            historyRecyclerAdapter.notifyDataSetChanged();
            // init history to make sure we have the correct history list
            initHistory();
            // if selectable is in action mode, we remove the action
            if(selectable.isInActionMode()){
                sync.removeAction();
            }
        } catch (IOException e) {
            Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void initMoToolbar() {
        moToolBar = new MoToolBar(this)
                .setLeftOnClickListener(view -> onBackPressed());
        moToolBar.getCardView().makeTransparent();
    }

    private void initClass(){
        initHistory();
        initAdapter();
        initRecyclerView();
        initSelectable();
        initSearchable();
        initSelectedPopup();
        initSync();
    }


    /**
     * open in new tabs
     * open in new private tabs
     */
    private void initSelectedPopup(){
        this.selectedPopup = new MoPopUpMenu(this).setEntries(
                new Pair<>(getString(R.string.open_in_tab), menuItem -> {
                    MoOpenTab.openInNewTabs(HistoryActivity.this,historyRecyclerAdapter.getSelectedItems());
                    goBackToTabActivity();
                    return false;
                }),
                new Pair<>(getString(R.string.open_in_private_tab), menuItem -> {
                    MoOpenTab.openInNewPrivateTabs(HistoryActivity.this,historyRecyclerAdapter.getSelectedItems());
                    goBackToTabActivity();
                    return false;
                })
        );
    }


    private void initHistory() {
        allHistories = MoHistoryManager.getAllHistories();
    }

    private void initAdapter(){
        this.historyRecyclerAdapter = new MoHistoryRecyclerAdapter(allHistories,this)
                .setOnHistoryClicked(this);
    }

    private void initRecyclerView(){
        this.historyRecyclerView = MoRecyclerUtils.get(this.cardRecyclerView.getRecyclerView(),this.historyRecyclerAdapter)
                .setMaxHeight(getHeightPixels());
        this.historyRecyclerView.show();
    }

    private void initSelectable(){
        this.selectable = new MoSelectable<>(this,getGroupRootView(),this.historyRecyclerAdapter)
                .setCounterView(l.title)
                .setSelectAllCheckBox(selectBar.getCheckBox())
                .addUnNormalViews(selectBar)
                .setAllItemsAreSelectable(false);
    }


    private void initSearchable(){
        this.searchable = new MoSearchable(this, getGroupRootView(), () -> allHistories)
                .setOnSearchFinished(list -> updateAdapter((List<MoHistory>) list))
                .setOnSearchCanceled(() -> updateAdapter(allHistories))
                .setSearchOnTextChanged(true)
                .setActivity(this)
                .setAppBarLayout(l.appBarLayout)
                .setCancelSearch(searchBar.getLeftButton())
                .setSearchButton(moToolBar.getMiddleButton())
                .setClearSearch(searchBar.getRightButton())
                .setSearchTextView(searchBar.getEditText())
                .addUnNormalViews(searchBar);
    }

    private void updateAdapter(List<MoHistory> histories){
        historyRecyclerAdapter.setDataSet(histories);
        runOnUiThread( ()-> historyRecyclerAdapter.notifyDataSetChanged());
    }


    private void initSync(){
        this.sync = new MoListViewSync(getGroupRootView(),this.selectable,this.searchable)
                .setPutOnHold(true)
                .setSharedElements(moToolBar);
    }

    @Override
    public void onBackPressed() {
        if(this.sync.hasAction()){
            this.sync.removeAction();
        }else{
            super.onBackPressed();
        }
    }

    /**
     * launches the activity for
     * @param c context
     */
    public static void launch(Context c){
        Intent i = new Intent(c,HistoryActivity.class);
        c.startActivity(i);
    }

    /**
     * launches the activity for result
     * @param c activity
     */
    public static void launch(Activity c, int requestCode){
        Intent i = new Intent(c,HistoryActivity.class);
        c.startActivityForResult(i,requestCode);
    }


    @Override
    public void onHistoryClicked(MoHistory h, int position) {
        // we need to go back to tab activity in case we are inside another activity
        MoTabController.instance.openUrlInCurrentTab(this,h.getUrl(),true);
        goBackToTabActivity();
    }

    private void goBackToTabActivity() {
        historyRecyclerAdapter.clearSelection();
        setResult(MoTabSection.GO_TO_TAB_ACTIVITY_REQUEST);
        finish();
    }


}
