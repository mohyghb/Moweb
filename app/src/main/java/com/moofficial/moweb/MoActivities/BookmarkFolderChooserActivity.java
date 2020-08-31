package com.moofficial.moweb.MoActivities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkUtils;
import com.moofficial.moweb.Moweb.MoBookmark.MoOnOpenBookmarkListener;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.Objects;

public class BookmarkFolderChooserActivity extends MoSmartActivity implements MoOnOpenBookmarkListener {


    public static final String CHOSEN_FOLDER_TAG = "chosen_folder";
    public static final String EXTRA_FOLDER_NAME = "extrafoldername";

    private MoRecyclerView recyclerView;
    private MoBookmarkRecyclerAdapter recyclerAdapter;
    private MoCardRecyclerView cardRecyclerView;
    private MoSearchable moSearchable;
    private MoSearchBar searchBar;
    private MoToolBar moToolBar;
    private MoBookmark[] currentBookmark;
    private ArrayList<MoBookmark> allPossibleFolders,showingFolders = new ArrayList<>();

    @Override
    protected void init() {
        currentBookmark = MoBookmarkUtils.decodeBookmarks(Objects.requireNonNull(getIntent().getExtras()).
                getStringArray(EXTRA_FOLDER_NAME));
        initUI();
        initClass();
    }

    private void initUI(){
        setTitle(R.string.folder_chooser_title);
        initMoToolbar();
        initMoSearchbar();

        layout.toolbar.addToolbar(moToolBar);
        layout.toolbar.addToolbar(searchBar);

        cardRecyclerView = new MoCardRecyclerView(this);
        layout.linearNested.addView(cardRecyclerView, MoMarginBuilder.getLinearParams(0,8,0,0));

        syncTitle(moToolBar.getTitle());
    }

    private void initMoSearchbar() {
        searchBar = new MoSearchBar(this);
        searchBar.getCardView().makeTransparent();
        searchBar.setVisibility(View.GONE);
    }

    private void initMoToolbar() {
        moToolBar = new MoToolBar(this)
                .hideMiddle()
                .setLeftOnClickListener(view -> onBackPressed())
                .setRightIcon(R.drawable.ic_baseline_search_24);
        moToolBar.getCardView().makeTransparent();
        ;
    }


    private void initClass(){
        allPossibleFolders = MoBookmarkManager.getFolders(this.currentBookmark);
        MoBookmarkManager.sortAlphabetically(allPossibleFolders);
        showingFolders.addAll(allPossibleFolders);
        initRecyclerView();
        initSearch();
    }


    private void initSearch() {
        moSearchable = new MoSearchable(this, getGroupRootView(), () -> allPossibleFolders)
                .addNormalViews(this.moToolBar)
                .addUnNormalViews(this.searchBar)
                .setActivity(this)
                .setSearchOnTextChanged(true)
                .setAppBarLayout(layout.appBarLayout)
                .setSearchButton(this.moToolBar.RId())
                .setSearchTextView(searchBar.ETId())
                .setClearSearch(searchBar.RBId())
                .setCancelButton(searchBar.LBId())
                .setOnSearchCanceled(() -> updateAdapter(allPossibleFolders))
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    runOnUiThread(() -> updateAdapter((ArrayList<MoBookmark>)list));
                })
        ;
        moSearchable.setInvisible(View.GONE);
    }

    private void updateAdapter(ArrayList<MoBookmark> l){
        recyclerAdapter.setDataSet(l);
        recyclerAdapter.notifyDataSetChanged();
    }

    private void initRecyclerView() {

        recyclerAdapter = new MoBookmarkRecyclerAdapter(this, showingFolders)
                .setOpenBookmarkListener(this)
                .setDisableLongClick(true)
        ;
        recyclerView = MoRecyclerUtils.get(cardRecyclerView.getRecyclerView(),recyclerAdapter);
        recyclerView.show();
    }




    @Override
    public void openFolder(MoBookmark folder) {
        // return the folder name as data
        Intent data = new Intent();
        data.putExtra(CHOSEN_FOLDER_TAG,folder.getName());
        setResult(RESULT_OK,data);
        finish();
    }

    @Override
    public void openBookmark(MoBookmark bookmark) {
        // ignore since we are choosing a folder
    }

    @Override
    public void onBackPressed() {
        if(moSearchable.hasAction()){
            moSearchable.removeAction();
        }else {
            super.onBackPressed();
        }
    }



    public static String getChosenFolder(Bundle extras) {
        return extras.getString(CHOSEN_FOLDER_TAG);
    }

    public static void startActivityForResult(Activity a,int code,MoBookmark ... b){
        Intent i = new Intent(a,BookmarkFolderChooserActivity.class);
        i.putExtra(EXTRA_FOLDER_NAME, MoBookmarkUtils.encodeBookmarks(b));
        a.startActivityForResult(i,code);
    }

}