package com.moofficial.moweb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.moofficial.moessentials.MoEssentials.MoUI.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;

import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryRecyclerAdapter;

public class HistoryActivity extends AppCompatActivity {

    private static final int HISTORY_COUNT_PER_REFRESH = 10;

    // history
    private MoRecyclerView historyRecyclerView;

    private TextView firstDateTile;
    private Button loadMoreHistory;
    private int count = HISTORY_COUNT_PER_REFRESH;

    // toolbar
    private MoPopUpMenu morePopUp;
    private Toolbar toolbar;
    private Menu menu;
    private SearchView searchView;
    private ImageButton moreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        MoHistoryManager.load(this);
        init();

    }

    private void init() {
        initToolbar();
        initRecyclerView();
        initMorePopUpMenu();
        initMoreButton();
        initBackButton();
        initFirstDateTile();
        initLoadMoreButton();
        initSearchView();
    }

    private void initSearchView(){
        MenuItem searchItem = menu.findItem(R.id.app_bar_search);
        searchView = (SearchView) searchItem.getActionView();
//        new MoSearchViewFormatter()
//                .setSearchBackGroundResource(R.drawable.curved_top_day_night_background)
//                .setSearchHintText("search history")
//                .setSearchIconResource(R.drawable.ic_search_black_24dp,false,true)
//                .format(searchView);
    }

    private void initToolbar(){
        this.toolbar = findViewById(R.id.history_toolbar);
        this.menu = this.toolbar.getMenu();
    }

    private void initLoadMoreButton(){
        this.loadMoreHistory = findViewById(R.id.load_more_history_button);
        this.loadMoreHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count+=HISTORY_COUNT_PER_REFRESH;
                initRecyclerView();
            }
        });
    }

    private void initFirstDateTile(){
        MoHistory history = MoHistoryManager.getLastHistory();
        firstDateTile = findViewById(R.id.date_tile);
        if(history!=null){
            firstDateTile.setVisibility(View.VISIBLE);
            firstDateTile.setText(history.getDate());
        }else{
            firstDateTile.setVisibility(View.GONE);
        }
    }


    private void initRecyclerView(){
        historyRecyclerView = new MoRecyclerView(HistoryActivity.this,findViewById(R.id.history_recycler_view),
                new MoHistoryRecyclerAdapter(MoHistoryManager.getHistoriesWithDateTiles(count),
                        HistoryActivity.this));
        historyRecyclerView.setReverseLayout(true);
        historyRecyclerView.show();
    }

    private void initMorePopUpMenu(){
        this.morePopUp = new MoPopUpMenu(this).setEntries(
                new Pair<>("Clear all", menuItem -> {
                    MoHistoryManager.clearHistory(HistoryActivity.this);
                    initRecyclerView();
                    initFirstDateTile();
                    return false;
                })
        );
    }

    private void initMoreButton(){
        this.moreButton = (ImageButton)menu.findItem(R.id.more_menu_item).getActionView();
        this.moreButton.setOnClickListener(view -> morePopUp.show(view));
    }

    private void initBackButton(){
        toolbar.setNavigationOnClickListener(view -> finish());
    }



    public static void launch(Context c){
        Intent i = new Intent(c,HistoryActivity.class);
        c.startActivity(i);
    }

















}
