package com.moofficial.moweb.MoActivities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoPermissions.MoPermission;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoSwitchers.MoSectionViewManager;
import com.moofficial.moweb.MoActivities.MainMenu.MainMenuSection;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadManager;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebAppLoader.MoWebAppLoader;
import com.moofficial.moweb.R;

import java.security.Permission;

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
        handleLinkFromOthers(getIntent());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MoDownloadManager.onDestroy();
        // todo on destroy tab
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (sectionViewManager.getActiveSectionKey() == SECTION_TAB) {
            this.tabSection.updateBookmark();
        }
        this.tabSection.setActivity(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleLinkFromOthers(intent);
    }

    /**
     * this method handles links that are opened
     * with our app from other sources
     * @param intent that is sent to open this app
     */
    private void handleLinkFromOthers(Intent intent) {
        Uri startIntentData = intent.getData();
        if(startIntentData!=null) {
            String intentUrl = startIntentData.toString();
            if (intentUrl.contains("http://")||intentUrl.contains("https://")) {
                MoTabsManager.addTab(this, intentUrl, false);
                moveToTabFragment();
            }
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
        this.tabSection.setMoveToMainMenu(this::moveToMainMenuFragment)
                .setActivity(this)
                .init();
    }


    void moveToTabFragment() {
        sectionViewManager.setActiveSection(SECTION_TAB);
    }

    void moveToMainMenuFragment() {
        mainMenuFragment.onResume();
        sectionViewManager.setActiveSection(SECTION_MAIN_MENU);
    }

    void failedDownloadPermission() {
        Toast.makeText(this, R.string.download_activity_failed_permission, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (MoTabSection.DOWNLOAD_PERMISSION_REQUEST == requestCode) {
            if (MoPermission.allGranted(grantResults)) {
                Toast.makeText(this, R.string.download_activity_success_permission, Toast.LENGTH_LONG).show();
            } else {
                failedDownloadPermission();
            }
        }
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