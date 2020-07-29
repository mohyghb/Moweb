package com.moofficial.moweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoOriginalActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoDialog.MoDialogBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoDialog.MoOnDialogTextInputListener;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoCardBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoBottomBars.MoBottomDeleteBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoDelete.MoListDelete;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableList;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoOnOpenBookmarkListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BookmarkActivity extends MoOriginalActivity implements MoOnOpenBookmarkListener {


    public static final int EDIT_BOOKMARK_REQUEST = 3;

    private MoRecyclerView recyclerView;
    private MoBookmarkRecyclerAdapter recyclerAdapter;
    private MoSearchable moSearchable;
    private MoListDelete moListDelete;
    private MoListViewSync listViewSync;
    private Stack<MoBookmark> folders = new Stack<>();
    private MoSearchBar searchBar;
    private MoToolBar toolBar;
    private MoCardRecyclerView cardRecyclerView;
    private MoBottomDeleteBar bottomDeleteBar;
    private View combinationBar;



    @Override
    protected void init(){
        initUI();
        initClass();
    }

    private void initUI() {
        combinationBar = toolbar.addToolbar(R.layout.mo_combination_appbar_searchbar);
        bottomDeleteBar = new MoBottomDeleteBar(this);
        linearBottom.addView(bottomDeleteBar.goGone());
        cardRecyclerView = new MoCardRecyclerView(this).makeCardRound();
//        cardRecyclerView.customize(new MoCardBuilder(this)
//                .setBackgroundColorId(R.color.transparent),cardRecyclerView.CId());
        linearNested.addView(cardRecyclerView);
    }


    private void initClass(){
        initSearchBar();
        initToolBar();
        removeUnsavableBookmarks();
        initCreateNewFile();
        initTitle();
        initRecyclerAdapter();
        initRecyclerView();
        initMoSearchable();
        initMoListDelete();
        initListViewSync();
        syncTitle(findViewById(toolBar.TVId()));
    }



    /**
     * gets the current folder bookmarks
     * @return
     */
    private ArrayList<MoBookmark> getCurrentFolderBookmarks(){
        return folders.isEmpty()?MoBookmarkManager.getAll():folders.peek().getSubBookmarks();
    }

    /**
     * returns the current title of the folder
     * or the main title if the folders in are empty
     * @return
     */
    private String getCurrentTitle(){
        return folders.isEmpty()?getString(R.string.bookmark_title):folders.peek().getName();
    }


    private void createNewFolder(String s){
        getCurrentFolderBookmarks().add(MoBookmarkManager.buildFolder(s));
        MoBookmarkManager.save(this);
        recyclerView.notifyItemAddedLastPosition();
        Toast.makeText(this,String.format("Folder %s was created!",s),Toast.LENGTH_SHORT).show();
    }




    private void initSearchBar(){
        this.searchBar = findViewById(R.id.include_search_bar);
        searchBar.customize(new MoCardBuilder(this)
                .setBackgroundColorId(R.color.transparent),searchBar.CVId());
    }

    private void initToolBar(){
        this.toolBar = findViewById(R.id.include_tool_bar);
        this.toolBar.customize(new MoCardBuilder(this)
                .setBackgroundColorId(R.color.transparent),toolBar.CId());
    }

    private void removeUnsavableBookmarks(){
        List<MoBookmark> bookmarks = getCurrentFolderBookmarks();
        for(int i = bookmarks.size()-1;i>=0;i--){
            if(!bookmarks.get(i).isSavable()){
                bookmarks.remove(i);
            }
        }
    }

    private void initCreateNewFile(){
        floatingActionButton.setIcon(R.drawable.ic_baseline_create_new_folder_24);
        floatingActionButton.setBackgroundColor(R.color.colorAccent);
        floatingActionButton.setOnClickListener(view -> MoDialogBuilder.showUserInputTextLayoutDialog(BookmarkActivity.this, "New Folder",
                "Enter the name of the folder and press done",
                "Done",
                "cancel",
                new MoOnDialogTextInputListener() {
                    @Override
                    public void onPositiveButtonPressed(DialogInterface dialogInterface, String input) {
                        createNewFolder(input);
                    }

                    @Override
                    public void onNegativeButtonPressed(DialogInterface dialogInterface, String input) {

                    }
                }
        ));
        floatingActionButton.show();
    }

    private void initTitle(){
        setTitle(getCurrentTitle());
    }

    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoBookmarkRecyclerAdapter(this, getCurrentFolderBookmarks())
                .setOpenBookmarkListener(this);
    }

    private void initRecyclerView(){
        this.recyclerView = new MoRecyclerView(this,this.cardRecyclerView.RVId(),this.recyclerAdapter);
        this.recyclerView.show();
    }

    private void initMoSearchable() {
        this.moSearchable = new MoSearchable(this, getRootView(), new MoSearchableList() {
            @Override
            public List<? extends MoSearchableItem> getSearchableItems() {
                return getCurrentFolderBookmarks();
            }

            @Override
            public void notifyItemChanged(int i) {

            }

            @Override
            public void notifyDataSetChanged() {

            }
        });
        this.moSearchable.setShowNothingWhenSearchEmpty(false)
                .setSearchOnTextChanged(true)
                .setSearchTextView(searchBar.ETId())
                .setAppBarLayout(appBarLayout)
                .setSearchButton(toolBar.MId())
                .addNormalViews(R.id.include_tool_bar,floatingActionButton.getView().getId())
                .addUnNormalViews(R.id.include_search_bar)
                .setCancelButton(searchBar.LBId())
                .setClearSearch(searchBar.RBId())
                .setActivity(this)
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    updateRecyclerAdapter((ArrayList<MoBookmark>) list);
                }).setOnSearchCanceled(() -> {
                    updateRecyclerAdapter(getCurrentFolderBookmarks());
                })
        ;
    }

    private void updateRecyclerAdapter(ArrayList<MoBookmark> list) {
        recyclerAdapter.setDataSet(list);
        runOnUiThread(() -> recyclerAdapter.notifyDataSetChanged());
    }

    private void initMoListDelete(){
        this.moListDelete = new MoListDelete(this,getRootView(),this.recyclerAdapter);
        this.moListDelete.setCounterView(title.getId()," Selected")
                         .setOnDeleteFinished(() -> updateRecyclerAdapter(getCurrentFolderBookmarks()))
                         .setOnCanceledListener(() -> updateRecyclerAdapter(getCurrentFolderBookmarks()))
                         .setLoadTitleAfter(true)
                         .addNormalViews(toolBar.LId(),toolBar.MId(),toolBar.RId(),floatingActionButton.getView().getId())
                         .addUnNormalViews(bottomDeleteBar)
                         .setCancelButton(bottomDeleteBar.CBId())
                         .setConfirmButton(bottomDeleteBar.DBId())
        ;
        this.moListDelete.setInvisible(View.GONE);
    }


    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(this.moListDelete,this.moSearchable);
    }






    @Override
    public void openFolder(MoBookmark folder) {
        if(listViewSync.hasAction()){
            listViewSync.removeAction();
        }
        folders.add(folder);
        initClass();
    }

    @Override
    public void openBookmark(MoBookmark bookmark) {
        MoTabsManager.addTab(this,bookmark.getUrl(),false);
        finish();
    }


    @Override
    public void onBackPressed() {
        if(listViewSync.hasAction()){
            listViewSync.removeAction();
        }else if(!folders.isEmpty()){
            folders.pop();
            initClass();
        }else{
            super.onBackPressed();
        }
    }


    public static void startActivity(Context context){
        Intent i = new Intent(context,BookmarkActivity.class);
        context.startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case EDIT_BOOKMARK_REQUEST:
                if(resultCode == RESULT_OK){
                    initClass();
                    Toast.makeText(this,"Updated...",Toast.LENGTH_SHORT).show();
                    MoBookmarkManager.save(this);
                }
                break;
        }
    }
}