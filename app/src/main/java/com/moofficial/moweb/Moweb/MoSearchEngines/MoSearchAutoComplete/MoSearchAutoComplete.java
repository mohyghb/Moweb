package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import com.moofficial.moweb.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.R;

public class MoSearchAutoComplete {

    public static boolean enabled = true;


    public static void updateSearchAutoComplete(Context c){
        enabled = MoSharedPref.get(c.getString(R.string.auto_complete),true);
    }

}
