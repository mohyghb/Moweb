package com.moofficial.moweb.MoActivities;

import android.content.Context;
import android.content.SharedPreferences;
import android.webkit.CookieManager;

import androidx.preference.PreferenceManager;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.MoSettingsEssentials.MoTheme.MoTheme;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoWebFeatures.MoWebFeatures;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;
import com.moofficial.moweb.R;

public class MoSettingsSection {



    private Context context;
    private SharedPreferences sharedPreferences;

    private static MoSettingsSection instance;

    public static void init(Context c){
        instance = new MoSettingsSection(c);
    }

    public static MoSettingsSection getInstance(){
        return instance;
    }


    public MoSettingsSection(Context c){
        this.context = c;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        init();
    }

    private void init() {
        updateAll();
    }

    private void updateAll(){
        initSearchEngine();
        initTheme();
        initAutoComplete();
        initHistorySettings();
        initPasswords();
        initCookies();
    }




    private void initSearchEngine(){
        MoSearchEngine.updateSearchEngine(this.context);
    }

    private void initTheme(){
        MoTheme.updateTheme(this.context);
    }

    private void initAutoComplete(){
        MoSearchAutoComplete.updateSearchAutoComplete(this.context);
    }

    private void initHistorySettings(){
        MoHistoryManager.updateSharedPref(context);
    }

    private void initPasswords() {
        MoUserPassManager.updatePreference(this.context);
    }

    private void initCookies() {
        MoWebUtils.updateCookies(this.context);
    }

    private void initFeatures() {
        MoWebFeatures.updateSnapSearch(this.context);
        MoWebFeatures.updateOneHand(this.context);
    }




}
