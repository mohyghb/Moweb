package com.moofficial.moweb;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.moofficial.moessentials.MoEssentials.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkRecyclerAdapter;

public class BookmarkActivity extends AppCompatActivity {


    private MoRecyclerView recyclerView;
    private MoBookmarkRecyclerAdapter recyclerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);
        init();
    }

    private void init(){
        initRecyclerAdapter();
        initRecyclerView();
    }

    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoBookmarkRecyclerAdapter(this, MoBookmarkManager.getBookmarks());
    }

    private void initRecyclerView(){
        this.recyclerView = new MoRecyclerView(this,R.id.bookmark_recycler_view,this.recyclerAdapter);
        this.recyclerView.show();
    }


    public static void startActivity(Context context){
        Intent i = new Intent(context,BookmarkActivity.class);
        context.startActivity(i);
    }

}