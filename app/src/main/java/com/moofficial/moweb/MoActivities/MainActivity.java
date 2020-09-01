package com.moofficial.moweb.MoActivities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.moofficial.moessentials.MoEssentials.MoUI.MoAnimation.MoTransitions.MoCircularTransition;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;
import com.moofficial.moweb.R;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        WebView.enableSlowWholeDocumentDraw();


        MoCircularTransition t = new MoCircularTransition();
       // t.setSlideEdge(Gravity.START);
        //t.setDuration(2000);
        Window w = getWindow();
        w.setEnterTransition(t);
        w.setExitTransition(t);
//        w.setAllowReturnTransitionOverlap(true);
//        w.setAllowEnterTransitionOverlap(true);




        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //init();




    }


//    private void init() {
//        MoLog.printRunTime("home pages",()-> MoHomePageManager.load(this));
//        MoLog.printRunTime("bookmarks",()-> MoBookmarkManager.load(this));
//
//        MoAnimation.initAllAnimations(this);
//        rootView = findViewById(R.id.root_view);
//        MoLog.printRunTime("load state ", this::loadState);
//        this.moSettingsSection = new MoSettingsSection(this);
//        this.moTabSectionManager = new MoTabSectionManager(
//                this,this::changeContentView);
//        this.linearLayout = findViewById(R.id.activity_main_linear_layout);
//        this.moSectionViewManager = new MoSectionViewManager(rootView)
//                .addSection(TABS_VIEW,moTabSectionManager.getMainView())
//                .addSection(IN_TAB_VIEW,linearLayout)
//                .setTransitionIn(new Slide())
//                .setTransitionOut(new Slide());
//
//        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                changeContentView();
//                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//            }
//        });
//
//
//        MoLog.printRunTime("history", () -> {
//            try {
//                MoHistoryManager.load(this);
////                for(int i = 0; i < 300;i++){
////                    MoHistoryManager.add(this, MoSearchEngine.instance.getURL(i+""),i+"");
////                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//
//    }
//
//
//    // for controller
//    private void onTabsButtonPressed() {
//        moTabSectionManager.onTabsButtonPressed();
//        changeContentView();
//    }
//
//
//
//
//
//
//
//    // set the content view to a tab controller's index
//    // it is for the controller
//    // do not call it inside main
//    private void changeContentView(){
//        switch (MoSectionManager.getInstance().getSection()){
//            case TABS_VIEW:
//                moTabSectionManager.update();
//                moSectionViewManager.setActiveSection(TABS_VIEW);
//                break;
//            case IN_TAB_VIEW:
//                if(MoTabController.instance.isNotOutOfOptions()){
//                    startActivity(new Intent(this,MoTabActivity.class),
//                            ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//                    //overridePendingTransition(MoAnimation.LEFT_TO_RIGHT,MoAnimation.RIGHT_TO_LEFT);
//
////                    MoTab t = MoTabController.instance.getCurrent();
////                    // removing everything from linear layout
////                    linearLayout.removeAllViews();
////                    // adding the current tab inside
////                    linearLayout.addView(t.getZeroPaddingView(rootView),
////                            new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
////                                    ViewGroup.LayoutParams.MATCH_PARENT));
////                    // showing in tab view
////                    moSectionViewManager.setActiveSection(IN_TAB_VIEW);
////                    // applying the window rules of the tab
////                    t.applyWindowRules(this);
//                }else{
//                    MoSectionManager.getInstance().setSection(TABS_VIEW);
//                    changeContentView();
//                }
//                break;
//        }
//    }



//    @Override
//    protected void onResume() {
//        super.onResume();
//        MoTabController.instance.onResume();
//    }
//
//    @Override
//    public void onBackPressed() {
//        switch (MoSectionManager.getInstance().getSection()){
//            case TABS_VIEW:
//                if(MoTabsManager.isNotEmpty()) {
//                  // if there is a tab inside
//                  // we navigate back to tab controller index
//                    MoSectionManager.getInstance().setSection(IN_TAB_VIEW);
//                    //changeContentView();
//                } else {
//                    super.onBackPressed();
//                }
//                break;
//            case IN_TAB_VIEW:
//                MoTabController.instance.getCurrent().onBackPressed(MainActivity.super::onBackPressed);
//                break;
//        }
//    }
//
//
//    // save the tabs before leaving the app
//    @Override
//    protected void onStop() {
//        super.onStop();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        MoTabController.instance.onDestroy();
//        MoTabsManager.onDestroy();
//    }

//    @Override
//    protected void onPause() {
//        super.onPause();
//        MoTabController.instance.onPause();
//    }



//    /**
//     * load tabs
//     * load the tab controller
//     */
//    private void  loadState() {
//        MoLog.print("loading tabs ... ");
//        // create a tab controller
//        MoTabController.init(this);
//        MoTabController.instance.load("",this);
//        // load back all the tabs
//        MoLog.printRunTime("tabs",()->MoTabsManager.load(this));
//    }


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



}
