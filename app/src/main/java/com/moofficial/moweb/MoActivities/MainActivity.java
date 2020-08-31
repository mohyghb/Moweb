package com.moofficial.moweb.MoActivities;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

//    private static final String CURRENT_FRAGMENT = "current_frag";
//
//    private MoTabActivity tabActivity;
//    private MainMenuActivity mainMenuActivity;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // load the app
//        MoWebAppLoader.loadApp(this);
//
//        tabActivity = new MoTabActivity(this::changeToAnother);
//        mainMenuActivity = new MainMenuActivity();
//
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction().replace(R.id.activity_main_frame_layout,
//                tabActivity,CURRENT_FRAGMENT).commit();
//    }
//
//    public void changeToAnother(){
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        fragmentManager.beginTransaction()
//                .addToBackStack(null)
//                .replace(R.id.activity_main_frame_layout, mainMenuActivity, CURRENT_FRAGMENT)
//                .commit();
//    }
//
//    @Override
//    public void onBackPressed() {
//        MoFragment f = (MoFragment) getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
//
//        if(f!=null && !f.onBackPressed()) {
//            if(getSupportFragmentManager().getBackStackEntryCount()>0) {
//                getSupportFragmentManager().popBackStack();
//            } else {
//                super.onBackPressed();
//            }
//        }else{
//            super.onBackPressed();
//        }
//    }
}
