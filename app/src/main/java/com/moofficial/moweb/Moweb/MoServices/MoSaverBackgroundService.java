package com.moofficial.moweb.Moweb.MoServices;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;

public class MoSaverBackgroundService extends Service {



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        AsyncTask.execute(() -> MoLog.printRunTime("saveTabsRunTime", () -> {
//            MoLog.print("saving tabs ... ");
//            MoTabsManager.save(MoSaverBackgroundService.this);
//            MoTabController.instance.save(MoSaverBackgroundService.this);
//        }));

//        Handler handler = new Handler();
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                MoLog.printRunTime("saveTabsRunTime", () -> {
//                            MoLog.print("saving tabs ... ");
//                            MoTabsManager.save(MoSaverBackgroundService.this);
//                            MoTabController.instance.save(MoSaverBackgroundService.this);
//                        });
////                MoAsyncTask asyncTask = new MoAsyncTask().setAsyncDoInBackground(objects -> {
////
////                });
////                asyncTask.setAsyncOnTaskFinished(o -> MoLog.print("saving just finished async task"));
////                asyncTask.execute();
//            }
//        });

        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        MoLog.print("on destroy saver background service");
        super.onDestroy();
    }
}
