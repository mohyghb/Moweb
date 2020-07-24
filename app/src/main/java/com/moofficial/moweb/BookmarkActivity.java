package com.moofficial.moweb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.moofficial.moessentials.MoEssentials.MoDelete.MoListDelete;
import com.moofficial.moessentials.MoEssentials.MoDelete.MoOnDeleteFinished;
import com.moofficial.moessentials.MoEssentials.MoDialog.MoDialogBuilder;
import com.moofficial.moessentials.MoEssentials.MoDialog.MoOnDialogTextInputListener;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoSearchable.MoOnSearchFinished;
import com.moofficial.moessentials.MoEssentials.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoSearchable.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoSearchable.MoSearchableList;
import com.moofficial.moessentials.MoEssentials.MoViews.MoListViewSync;
import com.moofficial.moweb.MoAppbar.MoAppbarUtils;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoOnOpenFolderListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class BookmarkActivity extends AppCompatActivity implements MoOnOpenFolderListener {


    private MoRecyclerView recyclerView;
    private MoBookmarkRecyclerAdapter recyclerAdapter;
    private MoSearchable moSearchable;
    private MoListDelete moListDelete;
    private MoListViewSync listViewSync;
    private TextView title;
    private FloatingActionButton createNewFileButton;
    private Stack<MoBookmark> folders = new Stack<>();
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        init();
    }

    /**
     * gets the current folder bookmarks
     * @return
     */
    private ArrayList<MoBookmark> getCurrentFolderBookmarks(){
        return folders.isEmpty()?MoBookmarkManager.getBookmarks():folders.peek().getSubBookmarks();
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


    private void init(){
        parentView = findViewById(R.id.root_bookmark);
        removeUnsavableBookmarks();
        initCreateNewFile();
        initTitle();
        initRecyclerAdapter();
        initRecyclerView();
        initMoSearchable();
        initMoListDelete();
        initListViewSync();
        MoAppbarUtils.sync(parentView,R.id.mo_appbar,
                R.id.mo_collapsing_toolbar,
                R.id.title_book_mark_activity,
                R.id.menu_app_bar_title);
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
        this.createNewFileButton = findViewById(R.id.add_folder_floating_button);
        this.createNewFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MoDialogBuilder.showUserInputTextLayoutDialog(BookmarkActivity.this, "New Folder",
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
                );
            }
        });
    }

    private void initTitle(){
        this.title = findViewById(R.id.title_book_mark_activity);
        this.title.setText(getCurrentTitle());
    }

    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoBookmarkRecyclerAdapter(this, getCurrentFolderBookmarks())
                .setOpenFolderListener(this);
    }

    private void initRecyclerView(){
        this.recyclerView = new MoRecyclerView(this,R.id.bookmark_recycler_view,this.recyclerAdapter);
        this.recyclerView.show();
    }

    private void initMoSearchable() {
        this.moSearchable = new MoSearchable(this, parentView, new MoSearchableList() {
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
                .setSearchTextView(R.id.search_edit_text)
                .setSearchButton(R.id.menu_app_bar_search_button)
                .setNormalViews(R.id.include_app_bar,R.id.add_folder_floating_button)
                .setUnNormalViews(R.id.include_search_bar)
                .setCancelButton(R.id.close_search_bar)
                .setClearSearch(R.id.clear_search_bar)
                .setActivity(this)
                .setOnSearchFinished(list -> {
                    //noinspection unchecked
                    updateRecyclerAdapter((ArrayList<MoBookmark>) list);
                })
        ;
    }

    private void updateRecyclerAdapter(ArrayList<MoBookmark> list) {
        recyclerAdapter.setDataSet(list);
        runOnUiThread(() -> recyclerAdapter.notifyDataSetChanged());
    }

    private void initMoListDelete(){
        this.moListDelete = new MoListDelete(this,parentView,this.recyclerAdapter);
        this.moListDelete.setCounterView(R.id.title_book_mark_activity," Selected")
                         .setOnDeleteFinished(new MoOnDeleteFinished() {
                             @Override
                             public void onDeleteFinished() {
                                 updateRecyclerAdapter(getCurrentFolderBookmarks());
                             }
                         })
                         .setLoadTitleAfter(true)
                         .setNormalViews(R.id.add_folder_floating_button,R.id.include_combination)
                         .setUnNormalViews(R.id.include_bottom_delete)
                         .setCancelButton(R.id.cancel_delete_mode)
                         .setConfirmButton(R.id.delete_button_layout)
        ;
        this.moListDelete.setInvisible(View.GONE);
    }


    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(this.moListDelete,this.moSearchable);
    }






    public static void startActivity(Context context){
        Intent i = new Intent(context,BookmarkActivity.class);
        context.startActivity(i);
    }

    @Override
    public void openFolder(MoBookmark folder) {
        folders.add(folder);
        init();
    }


    @Override
    public void onBackPressed() {
        if(listViewSync.hasAction()){
            listViewSync.removeAction();
        }else if(!folders.isEmpty()){
            folders.pop();
            init();
        }else{
            super.onBackPressed();
        }
    }
}