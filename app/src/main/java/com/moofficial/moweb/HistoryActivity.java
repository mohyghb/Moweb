package com.moofficial.moweb;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.os.Build;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageButton;

import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;

import com.moofficial.moweb.MoAnimation.MoAnimation;
import com.moofficial.moweb.MoPopUpMenu.MoPopUpMenu;
import com.moofficial.moweb.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.MoSearch.MoSearchViewFormatter;
import com.moofficial.moweb.Moweb.MoHistory.MoHistory;
import com.moofficial.moweb.Moweb.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoHistory.MoHistoryRecyclerAdapter;

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
        new MoSearchViewFormatter()
                .setSearchBackGroundResource(R.drawable.curved_top_day_night_background)
                .setSearchHintText("search history")
                .setSearchIconResource(R.drawable.ic_search_black_24dp,false,true)
                .format(searchView);
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
        if(history!=null){
            firstDateTile = findViewById(R.id.date_tile);
            firstDateTile.setVisibility(View.VISIBLE);
            firstDateTile.setText(history.getDate());
        }else{
            firstDateTile.setVisibility(View.GONE);
        }
    }


    private void initRecyclerView(){
        historyRecyclerView = new MoRecyclerView(HistoryActivity.this,R.id.history_recycler_view,
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




















}
