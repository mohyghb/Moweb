package com.moofficial.moweb.Moweb.MoWebFeatures;

import android.content.Context;

import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.R;

public class MoWebFeatures {

    public static boolean snapSearchEnabled = true;
    public static boolean oneHandEnabled = true;


    public static void updateSnapSearch(Context context) {
        snapSearchEnabled = MoSharedPref.get(context)
                .getBoolean(context.getString(R.string.snap_search_enabled),true);
    }

    public static void updateOneHand(Context context) {
        oneHandEnabled = MoSharedPref.get(context)
                .getBoolean(context.getString(R.string.one_hand_web_enabled),true);
    }

}
