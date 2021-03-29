package com.moofficial.moweb.MoSettingsEssentials.MoSharedPref;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

public class MoSharedPref {

    /**
     * returns the shared pref
     */
    public static SharedPreferences get(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


}
