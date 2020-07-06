package com.moofficial.moweb.Moweb;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.moofficial.moweb.MainActivity;

public class MoWebUtils {

    public static final int WRITE_EXTERNAL_PERMISSION_CODE = 1;
    public static final String SOME_FEATURES_DISABLED = "Some features are disabled";

    public static void requestWritePermission(Activity activity){
        // if we dont already have the permission
        if (!hasWritePermission(activity)) {
            //requesting permission
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_PERMISSION_CODE);
        }
    }


    public static boolean hasWritePermission(Context context) {
        int permissionCheck = ActivityCompat.checkSelfPermission(context, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissionCheck == PackageManager.PERMISSION_GRANTED;
    }




}
