package com.moofficial.moweb.MoActivities;

import android.os.Bundle;
import android.transition.Slide;

import androidx.appcompat.app.AppCompatActivity;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoSwitchers.MoSectionViewManager;
import com.moofficial.moweb.MoActivities.MainMenu.MainMenuTabSection;
import com.moofficial.moweb.Moweb.MoWebAppLoader.MoWebAppLoader;
import com.moofficial.moweb.R;

public class MainActivity extends AppCompatActivity {

    public static final int HISTORY_FROM_MAIN_MENU_REQUEST = 2;

    private final int SECTION_TAB = 0;
    private final int SECTION_MAIN_MENU = 1;


    private MoTabSection tabSection;
    private MainMenuTabSection mainMenuFragment;
    private MoSectionViewManager sectionViewManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MoWebAppLoader.loadApp(this);
        init();

    }

    private void init() {
        // mo load the app completely
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
        sectionViewManager.setActiveSection(SECTION_MAIN_MENU);
        mainMenuFragment.notifyDataSetChanged();
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