package com.moofficial.moweb.Moweb.MoServices;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;


import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;

public class MoSaverBackgroundService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Handler handler = new Handler();
        handler.post(() -> {
            MoLog.print("saving tabs ... ");
            MoTabsManager.save(MoSaverBackgroundService.this);
            MoTabController.instance.save(MoSaverBackgroundService.this);
        });
        return android.app.Service.START_STICKY;
    }



}
