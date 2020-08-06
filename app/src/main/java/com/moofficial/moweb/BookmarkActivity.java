package com.moofficial.moweb;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
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
import java.util.Objects;
import java.util.Stack;

public class BookmarkActivity extends MoSmartActivity implements MoOnOpenBookmarkListener {


    public static final int EDIT_BOOKMARK_REQUEST = 3;
    public static final int CHOOSE_FOLDER_REQUEST = 4;

    private MoRecyclerView recyclerView;
    private MoBookmarkRecyclerAdapter recyclerAdapter;

    private MoSearchable moSearchable;
    private MoListDelete<MoBookmark> moListDelete;
    private MoListSelectable<MoBookmark> moListSelectable;
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
        this.moToolBar = new MoToolBar(this)
                .setLeftOnClickListener(view -> onBackPressed());
        this.moToolBar.getCardView().makeTransparent();
    }

    private void initListSelectableToolbar(){
        this.moListSelectableToolbar = new MoToolBar(this)
                .showCheckBox()
                .hideLeft()
                .setRightIcon(R.drawable.ic_baseline_folder_24).setRightOnClickListener(view -> {
                    // TODO TEST
                    MoBookmark[] m = new MoBookmark[recyclerAdapter.getSelectedItems().size()];
                    recyclerAdapter.getSelectedItems().toArray(m);
                    BookmarkFolderChooserActivity.startActivityForResult(this,CHOOSE_FOLDER_REQUEST, m);

                }).setMiddleIcon(R.drawable.ic_baseline_delete_24)
                .setMiddleOnClickListener(view -> {
//                    recyclerAdapter.deleteSelected();
//                    listViewSync.removeAction();
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
        MoBookmarkManager.createFolder(this, s, folders.peek(), () -> {
            recyclerView.notifyItemAddedLastPosition();
            Toast.makeText(BookmarkActivity.this,
                    String.format("Folder %s was created!",s),Toast.LENGTH_SHORT).show();
            dialogInterface.dismiss();
        });

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
        this.moSearchable = new MoSearchable(this, getRootView(), new MoSearchableList() {
            @Override
            public List<? extends MoSearchableItem> getSearchableItems() {
                return MoBookmarkManager.getEverything();
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
                .addUnNormalViews(searchBar)
                .setCancelButton(searchBar.LBId())
                .setClearSearch(searchBar.RBId())
                .setActivity(this)
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    updateRecyclerAdapter((ArrayList<MoBookmark>) list);
                });
    }

    private void updateRecyclerAdapter(ArrayList<MoBookmark> list) {
        recyclerAdapter.setDataSet(list);
        runOnUiThread(() -> recyclerAdapter.notifyDataSetChanged());
    }


    /**
     * so we can select multiple bookmarks or folders
     * and then move it to another folder
     */
    private void initMoListSelectable(){
        this.moListSelectable = new MoListSelectable<>(this, getRootView(), this.recyclerAdapter)
                .addUnNormalViews(this.moListSelectableToolbar)
                .setCounterView(this.title)
                .setOnCanceledListener(() -> recyclerAdapter.getSelectedItems().clear())
                .setOnSelectFinishedListener(list -> {
                    Toast.makeText(this,list.size()+"",Toast.LENGTH_SHORT).show();
                });
    }

    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(this.moSearchable,this.moListSelectable)
                .setPutOnHold(true)
                .setSharedElements(this.moToolBar,this.floatingActionButton.getView())
                .setOnEmptyOnHoldsListener(() -> {
                    updateRecyclerAdapter(getCurrentFolderBookmarks());
                });
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
        if(resultCode == RESULT_OK) {
            switch (requestCode) {
                case EDIT_BOOKMARK_REQUEST:
                    initClass();
                    Toast.makeText(this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                    break;
                case CHOOSE_FOLDER_REQUEST:
                    if (data != null) {
                        String newFolderName = BookmarkFolderChooserActivity.getChosenFolder(
                            Objects.requireNonNull(data.getExtras()));
                        String limitedVersion = MoString.getLimitedCount(newFolderName,16);
                         new AlertDialog.Builder(this)
                                .setTitle("Move to "  + limitedVersion)
                                .setMessage("Do you want to move " +
                                        recyclerAdapter.getSelectedItems().size() +
                                        " items to " + limitedVersion +"?")
                                .setPositiveButton(R.string.yes, (dialogInterface, i) -> {
                                    // move it to the new folder
                                    MoBookmarkManager.moveToFolderAndSave(this,
                                            MoBookmarkManager.getFolder(newFolderName),
                                            recyclerAdapter.getSelectedItems());
                                    // giving them progress update
                                    Toast.makeText(BookmarkActivity.this,"Updated!",Toast.LENGTH_SHORT).show();
                                    // done selecting
                                    listViewSync.removeAction();
                                }).setNegativeButton(R.string.cancel,null)
                                .create()
                                .show();
                    }
                    break;
            }
        }
    }
}