package com.moofficial.moweb.MoActivities;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.FragmentActivity;

import com.moofficial.moweb.MoActivities.MainMenu.MainMenuActivity;
import com.moofficial.moweb.Moweb.MoWebAppLoader.MoWebAppLoader;
import com.moofficial.moweb.R;

public class MainActivity extends FragmentActivity {

    private MoTabActivity tabFragment = new MoTabActivity()
            .setMoveToMainMenu(this::moveToMainMenuFragment);
    private MainMenuActivity mainMenuFragment = new MainMenuActivity()
            .setMoveToTabFragment(this::moveToTabFragment);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init() {
        // mo load the app completely
        MoWebAppLoader.loadApp(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.main_activity_frame,tabFragment)
                .add(R.id.main_activity_frame,mainMenuFragment)
                .hide(mainMenuFragment)
                .commitNow();
        //moveToTabFragment();
    }


    void moveToTabFragment(View sharedView) {

        getSupportFragmentManager()
                .beginTransaction()
                .addSharedElement(sharedView, sharedView.getTransitionName())
                .hide(mainMenuFragment)
                .show(tabFragment)
                .runOnCommit(() -> {
                    mainMenuFragment.onPause();
                    tabFragment.onResume();
                })
                .commit();

    }

    void moveToMainMenuFragment(View sharedView) {

        getSupportFragmentManager()
                .beginTransaction()

                .addSharedElement(sharedView,sharedView.getTransitionName())
                .hide(tabFragment)
                .show(mainMenuFragment)
                .runOnCommit(() -> {
                    mainMenuFragment.notifyDataSetChanged();
                    tabFragment.onPause();
                    mainMenuFragment.onResume();
                })
                .commit();

    }

}