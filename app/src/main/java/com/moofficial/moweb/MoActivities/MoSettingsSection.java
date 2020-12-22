package com.moofficial.moweb.MoActivities;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.moofficial.moweb.MoSettingsEssentials.MoTheme.MoTheme;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;

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






}
