package com.moofficial.moweb;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.MoSettingsEssentials.MoTheme.MoTheme;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;

public class MoSettingsSection {



    private Context context;
    private SharedPreferences sharedPreferences;

    public MoSettingsSection(Context c){
        this.context = c;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(c);
        init();
    }

    private void init(){
        initLoadAll();
        updateAll();
    }

    private void updateAll(){
        initSearchEngine();
        initTheme();
        initAutoComplete();
        initHistorySettings();
    }

    // loads all the shared prefs inside a hash map
    private void initLoadAll(){
        MoSharedPref.loadAll(context);
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






}
