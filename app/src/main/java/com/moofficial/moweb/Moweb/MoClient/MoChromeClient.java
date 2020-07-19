package com.moofficial.moweb.Moweb.MoClient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.webkit.GeolocationPermissions;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;

import com.moofficial.moessentials.MoEssentials.MoPermissions.MoPermission;


public class MoChromeClient extends WebChromeClient {

    private static final int MAX_PROGRESS = 100;


    private Context context;
    private ProgressBar bar;

    public MoChromeClient(Context context){
        this.context = context;
    }

    public MoChromeClient setProgressBar(ProgressBar bar){
        this.bar = bar;
        return this;
    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        this.bar.setProgress(newProgress);
    }




    @Override
    public void onPermissionRequest(PermissionRequest request) {
        MoPermission permission = new MoPermission((Activity) context).setPermissions(request.getResources());
        if(permission.checkAndRequestPermissions()){
            request.grant(request.getResources());
        }
    }

    @Override
    public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
        MoPermission permission = new MoPermission((Activity)context)
                .setPermissions(Manifest.permission.ACCESS_FINE_LOCATION);
        if(permission.checkAndRequestPermissions()){
            callback.invoke(origin,true,false);
        }else{
            callback.invoke(origin,false,false);
        }
    }
}
