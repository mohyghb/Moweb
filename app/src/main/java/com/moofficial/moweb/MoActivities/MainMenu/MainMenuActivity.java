package com.moofficial.moweb.MoActivities.MainMenu;

import android.os.Bundle;
import android.transition.TransitionInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoFragment;
import com.moofficial.moweb.MoActivities.MainTransitionTo;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoOnTabClickListener;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

public class MainMenuActivity extends MoFragment implements MoOnTabClickListener {

    public static final int HISTORY_REQUEST_CODE = 0;


    private ConstraintLayout rootGroup;
    private MainMenuStateAdapter stateAdapter;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private MainTransitionTo moveToTabFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSharedElementEnterTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.shared_element));
        setSharedElementReturnTransition(TransitionInflater.from(getContext())
                .inflateTransition(R.transition.shared_element));
    }

    //    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        getWindow().setSharedElementEnterTransition(TransitionInflater.from(this)
//                .inflateTransition(R.transition.shared_element));
//        getWindow().setSharedElementExitTransition(TransitionInflater.from(this)
//                .inflateTransition(R.transition.shared_element));
//        postponeEnterTransition();
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_menu);
//
//
//
//        // mo init this activity
//        init();
//
//        // mo launch the tab activity, so the user is inside a tab
//        Intent intent = new Intent(this,MoTabActivity.class);
//        startActivity(intent);
//    }


    public MainMenuActivity setMoveToTabFragment(MainTransitionTo moveToTabFragment) {
        this.moveToTabFragment = moveToTabFragment;
        return this;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    protected void init(View v) {
        rootGroup = v.findViewById(R.id.main_menu_root);



        stateAdapter = new MainMenuStateAdapter(this)
                .setOnTabClickListener(this);
        viewPager2 = v.findViewById(R.id.main_menu_view_pager);
        tabLayout = v.findViewById(R.id.main_menu_tab_layout);

        viewPager2.setAdapter(stateAdapter);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    tab.setText("OBJECT " + (position + 1));
                }
        ).attach();
        viewPager2.setNestedScrollingEnabled(true);


    }


    @Override
    public void onTabClickListener(MoTab t, View sharedView, int index) {
        MoTabController.instance.setNewTab(getContext(),t);
        sharedView.setTransitionName(t.getTransitionName());
        moveToTabFragment.transition(sharedView);
    }


    public void notifyDataSetChanged() {
        stateAdapter.notifyFragments();
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if(requestCode == HISTORY_REQUEST_CODE && resultCode == MoTabActivity.GO_TO_TAB_ACTIVITY_REQUEST){
//            // we need to finish this activity and go to the tab activity
//            finish();
//        }
//    }

    @Override
    public boolean onBackPressed() {
        return false;
        // if there is no more tabs, leave the app
//        if(mainMenuTabFragment.viewSync.hasAction()){
//            mainMenuTabFragment.viewSync.removeAction();
//        }else if(MoTabController.instance.isOutOfOptions()){
//            finishAffinity();
//        }else{
//            super.onBackPressed();
//        }
    }

    @Override
    public int getLayoutResId() {
        return R.layout.activity_main_menu;
    }
}