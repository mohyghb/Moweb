package com.moofficial.moweb.Moweb.MoTab.MoTabs;

import android.content.Context;


// removes the mo web view from the tab view
// then that webview can be used to show stuff on other screens
// any search is added to history
// can take screen shots
public class MoPopUpTab extends MoTab {


    public MoPopUpTab(String searchText, Context context) {
        super(searchText, context);
        // monote this is not working
//        MoViewUtils.removeView(super.getMoWebView());
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
//                (LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
        //super.getMoWebView().setLayoutParams(layoutParams);
    }


    @Override
    public void saveTab() {
        // not saving the tab, just using it as a pop up
    }
}
