package com.moofficial.moweb.MoActivities.Bookmark;

import android.content.Context;
import android.content.Intent;
import android.util.Pair;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.transition.TransitionManager;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoDialog.MoDialogs;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoOnOpenBookmarkListener;
import com.moofficial.moweb.Moweb.MoTab.MoOpenTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

// TODO when removing, the bookmarks are still there
public class BookmarkActivity extends MoSmartActivity implements MoOnOpenBookmarkListener {


    public static final int EDIT_BOOKMARK_REQUEST = 3;
    public static final int CHOOSE_FOLDER_REQUEST = 4;
    public static final int ADD_FOLDER_REQUEST = 5;

    private MoRecyclerView recyclerView;
    private MoBookmarkRecyclerAdapter recyclerAdapter;

    private MoSearchable moSearchable;
    private MoSelectable<MoBookmark> moListSelectable;
    private MoToolBar moListSelectableToolbar;
    private MoListViewSync listViewSync;
    private MoSearchBar searchBar;
    private MoToolBar moToolBar;

    private Stack<MoBookmark> folders = new Stack<>();
    private MoCardRecyclerView cardRecyclerView;


    private int selectedFolderCount = 0;


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
        initCardRecyclerView();
    }

    private void initCardRecyclerView() {
        cardRecyclerView = new MoCardRecyclerView(this).makeCardRound();
        l.linearNested.addView(cardRecyclerView);

    }

    private void initSearchBar(){
        this.searchBar = new MoSearchBar(this);
        this.searchBar.getCardView().makeTransparent();
    }

    private void initToolBar(){
        this.moToolBar = new MoToolBar(this)
                .setRightIcon(R.drawable.ic_baseline_delete_outline_24)
                .setRightOnClickListener(view -> performClearAll())
                .setLeftOnClickListener(view -> onBackPressed());
        this.moToolBar.getCardView().makeTransparent();
    }

    private void initListSelectableToolbar(){
        this.moListSelectableToolbar = new MoToolBar(this)
                .showCheckBox()
                .showExtraButton()
                .hideLeft()
                .setRightIcon(R.drawable.ic_baseline_more_vert_24)
                .setRightOnClickListener(view -> showSelectionPopupMenu())
                .setMiddleIcon(R.drawable.ic_baseline_folder_24)
                .setMiddleOnClickListener(view -> performMoveToAnotherFolder())
                .setExtraIcon(R.drawable.ic_baseline_delete_24)
                .setExtraOnClickListener(view -> performDelete())
                .setForthIcon(R.drawable.ic_baseline_edit_24)
                .setForthOnClickListener(view -> performEdit());
        this.moListSelectableToolbar.getCardView().makeTransparent();
    }

    private void performEdit() {
        EditBookmarkActivity.startActivityForResult(this,recyclerAdapter.getSelectedItems().get(0),
                BookmarkActivity.EDIT_BOOKMARK_REQUEST);
    }


    /**
     * shows a menu of what things you can do
     * to the selected bookmarks/folders
     */
    private void showSelectionPopupMenu(){
        MoPopUpMenu p = new MoPopUpMenu(this).setEntries(
                new Pair<>(getString(R.string.share), menuItem -> performSelectionShare())
        );
        if(noFolderHasBeenSelected()){
            p.setEntries(
                    new Pair<>(getString(R.string.open_in_tab), menuItem -> {
                        if(nothingIsSelected()) return false;
                        MoOpenTab.openInNewTabs(this,recyclerAdapter.getSelectedItems());
                        finish();
                        return false;
                    }),
                    new Pair<>(getString(R.string.open_in_private_tab), menuItem -> {
                        if(nothingIsSelected()) return false;
                        MoOpenTab.openInNewPrivateTabs(this,recyclerAdapter.getSelectedItems());
                        finish();
                        return false;
                    })
            );
        }
        p.show(moListSelectableToolbar.getRightButton());
    }


    /**
     * shares all the selected bookmarks
     * and the bookmarks inside the folders that
     * are selected
     */
    private boolean performSelectionShare() {
        if (nothingIsSelected()) return false;
        MoBookmarkManager.shareBookmark(this,recyclerAdapter.getSelectedItems(),true);
        return false;
    }

    /**
     * launches an activity to find a new folder
     * for the selected bookmarks/folders
     * and if they select a new folder,
     * we ask them if they want to do that
     * and move all the selected bookmarks/folders
     * to the new folder
     */
    private void performMoveToAnotherFolder() {
        if (nothingIsSelected()) return;
        MoBookmark[] m = new MoBookmark[recyclerAdapter.getSelectedItems().size()];
        recyclerAdapter.getSelectedItems().toArray(m);
        BookmarkFolderChooserActivity.startActivityForResult(this,CHOOSE_FOLDER_REQUEST, m);
    }

    /**
     * deletes all the selected
     * bookmarks/folders
     */
    private void performDelete() {
        if (nothingIsSelected()) return;
        MoDialogs.showAlertDialog(this,
                "Delete " + recyclerAdapter.getSelectedItems().size() + " items",
                "Do you want to delete these bookmarks/folders?",
                (dialogInterface, i) -> {
                    recyclerAdapter.deleteSelected();
                    listViewSync.removeAction();
                });
    }

    /**
     * clears all the items inside the current folder
     */
    private void performClearAll() {
        if(recyclerAdapter.getItemCount() == 0) {
            Toast.makeText(this,"There is nothing to delete",Toast.LENGTH_SHORT).show();
            return;
        }
        MoDialogs.showAlertDialog(this,
                "Clear All",
                "Do you want to clear everything in " + getCurrentTitle(),
                (dialogInterface, i) -> {
                    MoBookmarkManager.deleteSelectedBookmarks(this,new ArrayList<>(recyclerAdapter.getDataSet()));
                    recyclerAdapter.setDataSet(new ArrayList<>());
                    TransitionManager.beginDelayedTransition(getGroupRootView());
                    recyclerAdapter.notifyDataSetChanged();
                });
    }

    /**
     *
     * @return true if the selection array list is empty
     */
    private boolean nothingIsSelected() {
        if(recyclerAdapter.getSelectedItems().isEmpty()){
            Toast.makeText(this,R.string.empty_selection_message,Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

    private boolean noFolderHasBeenSelected(){
        return selectedFolderCount == 0;
    }


    private void initClass() {
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
     * when we want to open or close a folder
     * we perform this method
     */
    private void openCloseFolder(){
        recyclerAdapter.setDataSet(getCurrentFolderBookmarks());
        recyclerAdapter.notifyDataSetChanged();
        setTitle(getCurrentTitle());
    }

    private void openCloseFolderWithAnimation() {
        TransitionManager.beginDelayedTransition(getGroupRootView());
        openCloseFolder();
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










    /**
     * shows a dialog to create a new folder
     */
    private void initCreateNewFile() {
        l.floatingActionButton.setIcon(R.drawable.ic_baseline_create_new_folder_24)
        .setBackgroundColor(R.color.colorAccent)
        .setOnClickListener(view -> {
            AddFolderBookmarkActivity.launch(this,ADD_FOLDER_REQUEST);
        }).show();
    }



    private void initTitle(){
        setTitle(getCurrentTitle());
    }

    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoBookmarkRecyclerAdapter(this, getCurrentFolderBookmarks())
                .setOpenBookmarkListener(this);
    }

    /**
     * we set the max height to the device height
     * in pixel, since recycler views inside a nested scroll view
     * have infinite height, and they load all the items at once
     * if they don't have a definitive height
     */
    private void initRecyclerView(){
        this.recyclerView = MoRecyclerUtils.get(this.cardRecyclerView.getRecyclerView(),this.recyclerAdapter)
                .setMaxHeight(getHeightPixels());
        this.recyclerView.show();
    }

    private void initMoSearchable() {
        this.moSearchable = new MoSearchable(this, getGroupRootView(), MoBookmarkManager::getEverything);
        this.moSearchable.setShowNothingWhenSearchEmpty(false)
                .setSearchOnTextChanged(true)
                .setSearchTextView(searchBar.ETId())
                .setAppBarLayout(l.appBarLayout)
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
        recyclerView.post(() -> {
            TransitionManager.beginDelayedTransition(getGroupRootView());
            recyclerAdapter.notifyDataSetChanged();
        });
    }


    /**
     * so we can select multiple bookmarks or folders
     * and then move it to another folder
     */
    private void initMoListSelectable(){

        this.moListSelectable = new MoSelectable<>(this, getGroupRootView(), this.recyclerAdapter)
                .addUnNormalViews(this.moListSelectableToolbar)
                .setCounterView(l.title)
                .setSelectAllCheckBox(moListSelectableToolbar.getCheckBox())
                .setOnSelectListener(bookmark -> {
                    hideShowEdit();
                    if(bookmark.isFolder()) {
                        if(bookmark.isSelected()) {
                            selectedFolderCount++;
                        } else {
                            selectedFolderCount--;
                        }
                    }
                }).setOnEmptySelectionListener(() -> {
                    listViewSync.removeAction();
                })
                .setOnCanceledListener(() -> recyclerAdapter.getSelectedItems().clear())
                .setOnSelectFinishedListener(list -> {
                    Toast.makeText(this,list.size()+"",Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * hides or shows the edit button based
     * on the number of selected items
     */
    private void hideShowEdit() {
        if(recyclerAdapter.onlyOneIsSelected()) {
            // then we can show edit
            TransitionManager.beginDelayedTransition(getGroupRootView());
            moListSelectableToolbar.showForthButton();
        } else {
            // hide edit
            TransitionManager.beginDelayedTransition(getGroupRootView());
            moListSelectableToolbar.hideForthButton();
        }
    }

    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(getGroupRootView(),this.moSearchable,this.moListSelectable)
                .setPutOnHold(true)
                .setSharedElements(this.moToolBar,l.floatingActionButton.getView())
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
        openCloseFolderWithAnimation();
    }



    @Override
    public void openBookmark(MoBookmark bookmark) {
        MoTabController.instance.openUrlInCurrentTab(this,bookmark.getUrl(),true);
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
            openCloseFolderWithAnimation();
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
                    openCloseFolderWithAnimation();
                    Toast.makeText(this, "Successfully updated!", Toast.LENGTH_SHORT).show();
                    break;
                case CHOOSE_FOLDER_REQUEST:
                    if (data != null) {
                        String newFolderName = BookmarkFolderChooserActivity.getChosenFolder(
                            Objects.requireNonNull(data.getExtras()));
                        String limitedVersion = MoString.getLimitedCount(newFolderName,16);
                        MoDialogs.showAlertDialog(this, "Move to "  + limitedVersion,
                                "Do you want to move " +
                                        recyclerAdapter.getSelectedItems().size() +
                                        " items to " + limitedVersion +"?",
                                (dialogInterface, i) -> moveSelectedToNewFolder(newFolderName));
                    }
                    break;
                case ADD_FOLDER_REQUEST:
                    // show the new folder
                    TransitionManager.beginDelayedTransition(getGroupRootView());
                    recyclerAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void moveSelectedToNewFolder(String newFolderName) {
        MoBookmarkManager.moveToFolderAndSave(this,
                MoBookmarkManager.getFolder(newFolderName),
                recyclerAdapter.getSelectedItems());
        // giving them progress update
        Toast.makeText(BookmarkActivity.this,R.string.changes_saved,Toast.LENGTH_SHORT).show();
        // done selecting
        listViewSync.removeAction();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        while(listViewSync.hasAction()){
            listViewSync.removeAction();
        }
    }
}