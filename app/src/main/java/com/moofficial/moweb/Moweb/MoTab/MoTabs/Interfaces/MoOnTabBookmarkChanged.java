package com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces;

// this was needed for the tab activity so that
// when we change the bookmark from the pop up menu
// we could listen for it and change it inside the main ui
public interface MoOnTabBookmarkChanged {

    // whether the tab is bookmarked after the change or not
    void onChanged(boolean isBookmarked);

}
