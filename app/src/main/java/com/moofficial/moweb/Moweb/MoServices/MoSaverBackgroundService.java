package com.moofficial.moweb.Moweb.MoServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoAsyncTask.MoAsyncTask;
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
        MoAsyncTask asyncTask = new MoAsyncTask().setAsyncDoInBackground(objects -> {
            MoLog.print("saving tabs ... ");
            MoTabsManager.save(MoSaverBackgroundService.this);
            MoTabController.instance.save(MoSaverBackgroundService.this);
        });
        asyncTask.execute();
        return android.app.Service.START_STICKY;
    }



}
