package com.moofficial.moweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoOriginalActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoPaddingBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoBottomBars.MoBottomDeleteBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoInputBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoDelete.MoListDelete;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableList;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSelectable.MoListSelectable;
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
    private MoListSelectable moListSelectable;
    private MoToolBar moListSelectableToolbar;
    private MoListViewSync listViewSync;
    private MoSearchBar searchBar;
    private MoToolBar moToolBar;

    private Stack<MoBookmark> folders = new Stack<>();
    private MoCardRecyclerView cardRecyclerView;
    private MoBottomDeleteBar bottomDeleteBar;



    @Override
    protected void init(){
        folders.add(MoBookmarkManager.getMainFolder());
        initUI();
        initClass();
    }

    private void initUI() {
        initSearchBar();
        initToolBar();
        initListSelectableToolbar();
        // when we have more than one bars
        // then we need to disable the toolbar animation
        setupMultipleToolbars(moToolBar, moToolBar,searchBar,moListSelectableToolbar);
        disableToolbarAnimation();
        bottomDeleteBar = new MoBottomDeleteBar(this);
        linearBottom.addView(bottomDeleteBar.goGone());
        cardRecyclerView = new MoCardRecyclerView(this).makeCardRound();
        linearNested.addView(cardRecyclerView);
    }

    private void initSearchBar(){
        this.searchBar = new MoSearchBar(this);
        this.searchBar.getMaterialCardView().makeTransparent();
    }

    private void initToolBar(){
        this.moToolBar = new MoToolBar(this);
        this.moToolBar.getCardView().makeTransparent();
    }

    private void initListSelectableToolbar(){
        this.moListSelectableToolbar = new MoToolBar(this)
                .hideLeft()
                .setRightIcon(R.drawable.ic_baseline_folder_24).setRightOnClickListener(view -> {

                }).setMiddleIcon(R.drawable.ic_baseline_delete_24)
                .setMiddleOnClickListener(view -> {

                });
        this.moListSelectableToolbar.getCardView().makeTransparent();
    }


    private void initClass(){
        removeUnsavableBookmarks();
        initCreateNewFile();
        initTitle();
        initRecyclerAdapter();
        initRecyclerView();
        initMoSearchable();
        initMoListDelete();
        initMoListSelectable();
        initListViewSync();
        syncTitle(moToolBar.getTitle(),moListSelectableToolbar.getTitle());
    }



    /**
     * gets the current folder bookmarks
     * @return
     */
    private ArrayList<MoBookmark> getCurrentFolderBookmarks(){
        return folders.peek().getSubs();
    }

    /**
     * returns the current title of the folder
     * or the main title if the folders in are empty
     * @return
     */
    private String getCurrentTitle(){
        return folders.peek().getName();
    }








    private void removeUnsavableBookmarks(){
        List<MoBookmark> bookmarks = getCurrentFolderBookmarks();
        for(int i = bookmarks.size()-1;i>=0;i--){
            if(!bookmarks.get(i).isSavable()){
                bookmarks.remove(i);
            }
        }
    }

    /**
     * shows a dialog to create a new folder
     */
    private void initCreateNewFile() {
        floatingActionButton.setIcon(R.drawable.ic_baseline_create_new_folder_24);
        floatingActionButton.setBackgroundColor(R.color.colorAccent);
        floatingActionButton.setOnClickListener(view -> {
            //making the input bar look good
            MoInputBar folderInput = new MoInputBar(this)
                    .setHint(R.string.add_folder_in_bookmark_hint_dialog)
                    .hideTitle();
            folderInput.getConstraintLayout().setLayoutParams(MoMarginBuilder.
                    getCardLayoutParams(12,4,4,4));
            new MoPaddingBuilder(16)
                    .convertValuesToDp()
                    .apply(folderInput);

            // making the alert dialog
            AlertDialog ad = new AlertDialog.Builder(BookmarkActivity.this)
                    .setTitle(R.string.new_folder)
                    .setMessage(R.string.new_folder_message)
                    .setView(folderInput)
                    .setPositiveButton(R.string.done, null)
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {})
                    .create();
            ad.setOnShowListener(dialogInterface -> {
                Button button = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view1 -> createFolder(folderInput,dialogInterface));
            });
            ad.show();
        });
        floatingActionButton.show();
    }

    private void createFolder(MoInputBar folderInput, DialogInterface dialogInterface) {
        String s = folderInput.getInputText().trim();
        if(s.isEmpty()){
            Toast.makeText(this,"Name can not be empty",Toast.LENGTH_SHORT).show();
        }
        if(MoBookmarkManager.hasFolder(s)){
            Toast.makeText(this,"Folder " +s + " already exits",Toast.LENGTH_SHORT).show();
            return;
        }

        MoBookmarkManager.createFolder(this,s,folders.peek());
        recyclerView.notifyItemAddedLastPosition();
        Toast.makeText(this,String.format("Folder %s was created!",s),Toast.LENGTH_SHORT).show();
        dialogInterface.dismiss();
    }

    private void initTitle(){
        setTitle(getCurrentTitle());
    }

    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoBookmarkRecyclerAdapter(this, getCurrentFolderBookmarks())
                .setOpenBookmarkListener(this);
    }

    private void initRecyclerView(){
        this.recyclerView = new MoRecyclerView(this,this.cardRecyclerView.getRecyclerView(),this.recyclerAdapter);
        this.recyclerView.show();
    }

    private void initMoSearchable() {
        ArrayList<MoBookmark> everything = MoBookmarkManager.getEverything();
        this.moSearchable = new MoSearchable(this, getRootView(), new MoSearchableList() {
            @Override
            public List<? extends MoSearchableItem> getSearchableItems() {
                return everything;
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
                .setSearchButton(moToolBar.MId())
                .addNormalViews(moToolBar,floatingActionButton.getView())
                .addUnNormalViews(searchBar)
                .setCancelButton(searchBar.LBId())
                .setClearSearch(searchBar.RBId())
                .setActivity(this)
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    updateRecyclerAdapter((ArrayList<MoBookmark>) list);
                }).setOnSearchCanceled(() -> {
//                    if(!moListSelectable.isInActionMode()){
                       // updateRecyclerAdapter(getCurrentFolderBookmarks());
                    //}

                })
        ;
    }

    private void updateRecyclerAdapter(ArrayList<MoBookmark> list) {
        recyclerAdapter.setDataSet(list);
        runOnUiThread(() -> recyclerAdapter.notifyDataSetChanged());
    }

    private void initMoListDelete(){
//        this.moListDelete = new MoListDelete<MoBookmark>(this,getRootView(),this.recyclerAdapter);
//        this.moListDelete.setCounterView(title.getId()," Selected")
//                         .setOnDeleteFinished(() -> updateRecyclerAdapter(getCurrentFolderBookmarks()))
//                         .setOnCanceledListener(() -> updateRecyclerAdapter(getCurrentFolderBookmarks()))
//                         .setLoadTitleAfter(true)
//                         .addNormalViews(moToolBar.LId(), moToolBar.MId(), moToolBar.RId(),floatingActionButton.getView().getId())
//                         .addUnNormalViews(bottomDeleteBar)
//                         .setCancelButton(bottomDeleteBar.CBId())
//                         .setConfirmButton(bottomDeleteBar.DBId())
//        ;
//        this.moListDelete.setInvisible(View.GONE);
    }

    /**
     * so we can select multiple bookmarks or folders
     * and then move it to another folder
     */
    private void initMoListSelectable(){
        this.moListSelectable = new MoListSelectable<MoBookmark>(this,getRootView(),this.recyclerAdapter)
                .addNormalViews(this.moToolBar,this.floatingActionButton.getView())
                .addUnNormalViews(this.moListSelectableToolbar)
                .setCounterView(this.title)
                .setConfirmImageButton(this.moListSelectableToolbar.getRightButton())
                .setOnCanceledListener(() -> updateRecyclerAdapter(getCurrentFolderBookmarks()))
                .setOnSelectFinishedListener(list -> {
                    Toast.makeText(this,list.size()+"",Toast.LENGTH_SHORT).show();
                });
    }

    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(this.moSearchable,this.moListSelectable)
                .setPutOnHold(true);
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
        }else if(folders.size()>1){
            // when we get to the main folder, the size of stack is 1
            // and we want to close the activity when we are on that folder
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