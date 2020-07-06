package com.moofficial.moweb.MoTheme;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import com.moofficial.moweb.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.R;

public class MoTheme {

    public static final int LIGHT_THEME = 0;
    public static final int DARK_THEME = 1;
    public static final int FOLLOW_SYSTEM_THEME = -1;

    public static void updateTheme(Context context){
        int themeValue = Integer.parseInt(MoSharedPref.get(context.getString(R.string.theme_version)
                ,FOLLOW_SYSTEM_THEME+""));
        switch (themeValue){
            case LIGHT_THEME:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case DARK_THEME:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case FOLLOW_SYSTEM_THEME:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
    }

}
