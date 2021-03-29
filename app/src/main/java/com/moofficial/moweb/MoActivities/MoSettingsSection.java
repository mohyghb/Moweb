package com.moofficial.moweb.MoActivities;

import android.content.Context;
import android.content.SharedPreferences;

import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.MoSettingsEssentials.MoTheme.MoTheme;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoWebFeatures.MoWebFeatures;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;
import com.moofficial.moweb.R;

public class MoSettingsSection {


    private static MoSettingsSection instance;

    public static void init(Context c) {
        initDefaults(c);
        instance = new MoSettingsSection(c);
    }

    public static MoSettingsSection getInstance() {
        return instance;
    }


    public MoSettingsSection(Context c) {
        updateAll(c);
    }

    private void updateAll(Context context) {
        initSearchEngine(context);
        initTheme(context);
        initAutoComplete(context);
        initHistorySettings(context);
        initCookies(context);
    }

    private void initSearchEngine(Context context) {
        MoSearchEngine.updateSearchEngine(context);
    }

    private void initTheme(Context context) {
        MoTheme.updateTheme(context);
    }

    private void initAutoComplete(Context context) {
        MoSearchAutoComplete.updateSearchAutoComplete(context);
    }

    private void initHistorySettings(Context context) {
        MoHistoryManager.updateSharedPref(context);
    }

    private void initCookies(Context context) {
        MoWebUtils.updateCookies(context);
    }

    public void initFeatures(Context context) {
        MoWebFeatures.updateSnapSearch(context);
        MoWebFeatures.updateOneHand(context);
    }

    /**
     * initializes the default values for the settings if it has not been initialized
     */
    private static void initDefaults(Context c) {
        boolean alreadyInitDefaults = MoSharedPref.get(c).getBoolean(
                c.getString(R.string.sharedPref_alreadySetDefault),
                false
        );
        if (alreadyInitDefaults)
            return;
        SharedPreferences.Editor editor = MoSharedPref.get(c).edit();
        editor.putString(c.getString(R.string.search_engine), MoSearchEngine.GOOGLE + "");
        editor.putString(c.getString(R.string.theme_version), MoTheme.FOLLOW_SYSTEM_THEME + "");
        editor.putBoolean(c.getString(R.string.sharedPref_alreadySetDefault), true);
        editor.apply();
    }

}
