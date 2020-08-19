package com.moofficial.moweb.Moweb.MoWebview.MoClient;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
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
    private boolean hideProgressBarWhenFinished = true;
    private ViewGroup.LayoutParams barParams;

    public MoChromeClient(Context context){
        this.context = context;
    }

    public MoChromeClient setProgressBar(ProgressBar bar){
        this.bar = bar;
        this.barParams = this.bar.getLayoutParams();
        return this;
    }


    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        updateProgressBar(newProgress);
    }

    private void updateProgressBar(int newProgress) {
        bar.post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bar.setProgress(newProgress,true);
            }else{
                bar.setProgress(newProgress);
            }
        });
        if(hideProgressBarWhenFinished && newProgress == 100){
            // then we are done so hide the progress bar
            bar.setVisibility(View.GONE);
        }else{
            bar.setVisibility(View.VISIBLE);
        }
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