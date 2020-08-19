package com.moofficial.moweb;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.Slide;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoAnimation;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoSwitchers.MoSectionViewManager;
import com.moofficial.moweb.MoSection.MoSectionManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoServices.MoSaverBackgroundService;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;

import java.io.IOException;

import static com.moofficial.moweb.MoSection.MoSectionManager.IN_TAB_VIEW;
import static com.moofficial.moweb.MoSection.MoSectionManager.TABS_VIEW;

public class MainActivity extends AppCompatActivity {


    private MoTabSectionManager moTabSectionManager;
    private MoSettingsSection moSettingsSection;
    private ConstraintLayout rootView;
    private MoSectionViewManager moSectionViewManager;


    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView.enableSlowWholeDocumentDraw();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        MoLog.printRunTime("home pages",()->MoHomePageManager.load(this));
        MoLog.printRunTime("bookmarks",()->MoBookmarkManager.load(this));

        init();

        MoLog.printRunTime("history", () -> {
            try {
                MoHistoryManager.load(this);
//                for(int i = 0; i < 300;i++){
//                    MoHistoryManager.add(this, MoSearchEngine.instance.getURL(i+""),i+"");
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }


    private void init() {
        MoAnimation.initAllAnimations(this);
        rootView = findViewById(R.id.root_view);
        MoLog.printRunTime("load state ", this::loadState);
        this.moSettingsSection = new MoSettingsSection(this);
        this.moTabSectionManager = new MoTabSectionManager(
                this,this::changeContentView);
        this.linearLayout = findViewById(R.id.activity_main_linear_layout);
        this.moSectionViewManager = new MoSectionViewManager(rootView)
                .addSection(TABS_VIEW,moTabSectionManager.getMainView())
                .addSection(IN_TAB_VIEW,linearLayout)
                .setTransitionIn(new Slide())
                .setTransitionOut(new Slide());
        changeContentView();
    }


    // for controller
    private void onTabsButtonPressed() {
        moTabSectionManager.onTabsButtonPressed();
        changeContentView();
    }







    // set the content view to a tab controller's index
    // it is for the controller
    // do not call it inside main
    private void changeContentView(){
        switch (MoSectionManager.getInstance().getSection()){
            case TABS_VIEW:
                moSectionViewManager.setActiveSection(TABS_VIEW);
                break;
            case IN_TAB_VIEW:
                if(!MoTabController.instance.isOutOfOptions()){
                    //MoTabActivity.startActivity(this);
                    linearLayout.removeAllViews();
                    linearLayout.addView(MoTabController.instance.getCurrent().getZeroPaddingView(rootView),
                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.MATCH_PARENT));
                    moSectionViewManager.setActiveSection(IN_TAB_VIEW);

                }else{
                    MoSectionManager.getInstance().setSection(TABS_VIEW);
                    changeContentView();
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        MoTabController.instance.onResume();
    }

    @Override
    public void onBackPressed() {
        switch (MoSectionManager.getInstance().getSection()){
            case TABS_VIEW:
                if(MoTabsManager.isNotEmpty()) {
                  // if there is a tab inside
                  // we navigate back to tab controller index
                    MoSectionManager.getInstance().setSection(IN_TAB_VIEW);
                    changeContentView();
                } else {
                    super.onBackPressed();
                }
                break;
            case IN_TAB_VIEW:
                MoTabController.instance.getCurrent().onBackPressed(MainActivity.super::onBackPressed);
                break;
        }
    }


    // save the tabs before leaving the app
    @Override
    protected void onStop() {
        saveState();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MoTabController.instance.onDestroy();
        MoTabsManager.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MoTabController.instance.onPause();
    }

    /**
     * saves tabs
     * saves the tab controller information
     */
    // TODO when the phone is sleep, this causes error
    private void saveState() {
        Intent saverIntent = new Intent(this, MoSaverBackgroundService.class);
        startService(saverIntent);
    }

    /**
     * load tabs
     * load the tab controller
     */
    private void loadState() {
        MoLog.print("loading tabs ... ");
        // create a tab controller
        MoTabController.init(this,this::changeContentView,this::onTabsButtonPressed);
        MoTabController.instance.load("",this);
        // load back all the tabs
        MoLog.printRunTime("tabs",()->MoTabsManager.load(this));
    }


    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case MoWebUtils
                    .WRITE_EXTERNAL_PERMISSION_CODE:
                if (grantResults.length <= 0
                        || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                            //Toast.makeText(this,MoWebUtils.SOME_FEATURES_DISABLED,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    public ConstraintLayout getRootView() {
        return rootView;
    }

}
