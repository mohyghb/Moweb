package com.moofficial.moweb.MoActivities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Pair;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.transition.TransitionManager;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoDialog.MoDialogs;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoListViewSync;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoPaddingBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoSearchBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoEditText.MoEditText;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoOnOpenBookmarkListener;
import com.moofficial.moweb.Moweb.MoTab.MoOpenTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

// TODO when removing, the bookmarks are still there
public class BookmarkActivity extends MoSmartActivity implements MoOnOpenBookmarkListener {


    public static final int EDIT_BOOKMARK_REQUEST = 3;
    public static final int CHOOSE_FOLDER_REQUEST = 4;

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
        linearNested.addView(cardRecyclerView);
    }

    private void initSearchBar(){
        this.searchBar = new MoSearchBar(this);
        this.searchBar.getCardView().makeTransparent();
    }

    private void initToolBar(){
        this.moToolBar = new MoToolBar(this)
                .setLeftOnClickListener(view -> onBackPressed());
        this.moToolBar.getCardView().makeTransparent();
    }

    private void initListSelectableToolbar(){
        this.moListSelectableToolbar = new MoToolBar(this)
                .showCheckBox()
                .showExtraButton()
                .hideLeft()
                .setRightIcon(R.drawable.ic_baseline_more_vert_24)
                .setRightOnClickListener(view -> {
                    showSelectionPopupMenu();
                })
                .setMiddleIcon(R.drawable.ic_baseline_folder_24)
                .setMiddleOnClickListener(view -> performMoveToAnotherFolder())
                .setExtraIcon(R.drawable.ic_baseline_delete_24)
                .setExtraOnClickListener(view -> {
                    performDelete();
                });
        this.moListSelectableToolbar.getCardView().makeTransparent();
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
            MoEditText editText = new MoEditText(this)
                    .setHint(R.string.add_folder_in_bookmark_hint_dialog)
                    .setBoxBackgroundColor(R.color.MoBackground);
            editText.getCardView().makeTransparent();

            new MoPaddingBuilder(16)
                    .convertValuesToDp()
                    .apply(editText);

            // making the alert dialog
            AlertDialog ad = new AlertDialog.Builder(BookmarkActivity.this)
                    .setTitle(R.string.new_folder)
                    .setMessage(R.string.new_folder_message)
                    .setView(editText)
                    .setPositiveButton(R.string.done, null)
                    .setNegativeButton(R.string.cancel, (dialogInterface, i) -> {})
                    .create();
            ad.setOnShowListener(dialogInterface -> {
                Button button = ad.getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view1 -> createFolder(editText,dialogInterface));
            });
            ad.show();
        });
        floatingActionButton.show();
    }

    private void createFolder(MoEditText folderInput, DialogInterface dialogInterface) {
        String s = folderInput.getInputText().trim();
        MoBookmarkManager.createFolder(this, s, folders.peek(), () -> {
            recyclerAdapter.notifyItemInserted(recyclerAdapter.getItemCount()-1);
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

        this.moListSelectable = new MoSelectable<>(this, getGroupRootView(), this.recyclerAdapter)
                .addUnNormalViews(this.moListSelectableToolbar)
                .setCounterView(this.title)
                .setSelectAllCheckBox(moListSelectableToolbar.getCheckBox())
                .setOnSelectListener(bookmark -> {
                    if(bookmark.isFolder()){
                        if(bookmark.isSelected()){
                            selectedFolderCount++;
                        }else{
                            selectedFolderCount--;
                        }
                    }
                })
                .setOnEmptySelectionListener(() -> {
                    listViewSync.removeAction();
                    int i = 0;
                })
                .setOnCanceledListener(() -> recyclerAdapter.getSelectedItems().clear())
                .setOnSelectFinishedListener(list -> {
                    Toast.makeText(this,list.size()+"",Toast.LENGTH_SHORT).show();
                });
    }

    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(getGroupRootView(),this.moSearchable,this.moListSelectable)
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
            }
        }
    }

    private void moveSelectedToNewFolder(String newFolderName) {
        MoBookmarkManager.moveToFolderAndSave(this,
                MoBookmarkManager.getFolder(newFolderName),
                recyclerAdapter.getSelectedItems());
        // giving them progress update
        Toast.makeText(BookmarkActivity.this,"Updated!",Toast.LENGTH_SHORT).show();
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