package com.moofficial.moweb.MoActivities;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Slide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoSwitchers.MoSectionViewManager;
import com.moofficial.moweb.MoActivities.MainMenu.MainMenuSection;
import com.moofficial.moweb.Moweb.MoWebAppLoader.MoWebAppLoader;
import com.moofficial.moweb.R;

public class MainActivity extends AppCompatActivity {

    public static final int HISTORY_FROM_MAIN_MENU_REQUEST = 2;

    private final int SECTION_TAB = 0;
    private final int SECTION_MAIN_MENU = 1;


    private MoTabSection tabSection;
    private MainMenuSection mainMenuFragment;
    private MoSectionViewManager sectionViewManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MoSettingsSection.init(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoLog.printRunTime("App loader",() -> MoWebAppLoader.loadApp(this));
        init();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (sectionViewManager.getActiveSectionKey() == SECTION_TAB) {
            this.tabSection.updateBookmark();
        }
    }

    private void init() {
        initTabSection();
        initMainMenuSection();
        initSectionManager();
    }





    private void initSectionManager() {
        this.sectionViewManager = new MoSectionViewManager(findViewById(R.id.main_activity_frame))
                .addSection(SECTION_TAB,this.tabSection)
                .addSection(SECTION_MAIN_MENU,mainMenuFragment)
                .setActiveSection(SECTION_TAB)
                .setTransitionIn(new Slide())
                .setTransitionOut(new Slide());
    }

    private void initMainMenuSection() {
        this.mainMenuFragment = findViewById(R.id.main_main_menu);
        this.mainMenuFragment.setActivity(this)
                .setFragmentManager(getSupportFragmentManager())
                .setLifecycle(getLifecycle())
                .setTransitionToTab(this::moveToTabFragment)
                .init();
    }

    private void initTabSection() {
        this.tabSection = findViewById(R.id.main_tab_section);
        this.tabSection.setMoveToMainMenu(this::moveToMainMenuFragment).init();
    }


    void moveToTabFragment() {
        sectionViewManager.setActiveSection(SECTION_TAB);
    }

    void moveToMainMenuFragment() {
        mainMenuFragment.onResume();
        sectionViewManager.setActiveSection(SECTION_MAIN_MENU);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == HISTORY_FROM_MAIN_MENU_REQUEST && resultCode == MoTabSection.GO_TO_TAB_ACTIVITY_REQUEST){
            // the user has opened something from the history activity
            // therefore, we need to transition to the tab
            // we need to call update because if we don't
            // it captures the wrong image from coordinator layout
            // when we are going back to the main menu
            tabSection.update();
            moveToTabFragment();
        }
    }

    @Override
    public void onBackPressed() {
        boolean consumed = false;
        switch (sectionViewManager.getActiveSectionKey()){
            case SECTION_TAB:
                consumed = tabSection.onBackPressed();
                break;
            case SECTION_MAIN_MENU:
                consumed = mainMenuFragment.onBackPressed();
                break;
        }
        if(!consumed){
            super.onBackPressed();
        }
    }
}