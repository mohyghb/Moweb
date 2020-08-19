package com.moofficial.moweb;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryRecyclerAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends MoSmartActivity {


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
        linearNested.addView(cardRecyclerView);
    }



    private void setupToolbars() {
        syncTitle(moToolBar.getTitle(),selectBar.getTitle());
        setupMultipleToolbars(moToolBar,moToolBar,selectBar,searchBar);
    }

    private void initSearchBar(){
        this.searchBar = new MoSearchBar(this);
        this.searchBar.setSearchHint(R.string.history_search_hint)
                .getMaterialCardView()
                .makeTransparent();
    }

    private void initSelectBar() {
        selectBar = new MoToolBar(this)
                .hideLeft()
                .showCheckBox()
                .setMiddleIcon(R.drawable.ic_baseline_delete_24)
                .setMiddleOnClickListener(view -> performDeleteHistory());
        selectBar.getCardView().makeTransparent();
    }

    /**
     * deletes the selected histories from
     * the array adapter and performs various
     * updates
     * toasts the error message if something goes wrong
     */
    private void performDeleteHistory() {
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
        initSync();
    }




    private void initHistory() {
        allHistories = MoHistoryManager.getAllHistories();
    }

    private void initAdapter(){
        this.historyRecyclerAdapter = new MoHistoryRecyclerAdapter(allHistories,this);
    }

    private void initRecyclerView(){
        this.historyRecyclerView = MoRecyclerUtils.get(this.cardRecyclerView.getRecyclerView(),this.historyRecyclerAdapter)
                .setMaxHeight(getHeightPixels());
        this.historyRecyclerView.show();
    }

    private void initSelectable(){
        this.selectable = new MoSelectable<>(this,getGroupRootView(),this.historyRecyclerAdapter)
                .setCounterView(title)
                .setSelectAllCheckBox(selectBar.getCheckBox())
                .addUnNormalViews(selectBar)
                .setAllItemsAreSelectable(false);
    }


    private void initSearchable(){
        this.searchable = new MoSearchable(this, getGroupRootView(), new MoSearchableList() {
            @Override
            public List<? extends MoSearchableItem> getSearchableItems() {
                return allHistories;
            }

            @Override
            public void notifyItemChanged(int i) {

            }

            @Override
            public void notifyDataSetChanged() {

            }
        })
                .setOnSearchFinished(list -> updateAdapter((List<MoHistory>) list))
                .setOnSearchCanceled(() -> updateAdapter(allHistories))
                .setSearchOnTextChanged(true)
                .setActivity(this)
                .setAppBarLayout(appBarLayout)
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
     * @param c
     */
    public static void launch(Context c){
        Intent i = new Intent(c,HistoryActivity.class);
        c.startActivity(i);
    }



}
