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
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoViews.MoListViewSync;
import com.moofficial.moweb.MoDialog.MoDialogBuilder;
import com.moofficial.moweb.MoDialog.MoOnDialogTextInputListener;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;
import com.moofficial.moweb.Moweb.MoBookmark.MoOnOpenFolderListener;

import java.util.ArrayList;
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
        initCreateNewFile();
        initTitle();
        initRecyclerAdapter();
        initRecyclerView();
        //initMoSearchable();
        initMoListDelete();
        initListViewSync();
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

//    private void initMoSearchable() {
//        this.moSearchable = new MoSearchable(this,findViewById(R.id.root_bookmark),this.recyclerAdapter);
//    }

    private void initMoListDelete(){
        this.moListDelete = new MoListDelete(this,findViewById(R.id.root_bookmark),this.recyclerAdapter);
        this.moListDelete.setCounterView(R.id.title_book_mark_activity," Selected")
                         .setLoadTitleAfter(true)
                         .setNormalViews(R.id.add_folder_floating_button,R.id.include_search_bar)
                         .setUnNormalViews(R.id.include_bottom_delete)
                         .setCancelButton(R.id.cancel_delete_mode)
                         .setConfirmButton(R.id.delete_button_layout)
        ;
        this.moListDelete.setInvisible(View.GONE);
    }


    private void initListViewSync(){
        this.listViewSync = new MoListViewSync(this.moListDelete);
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