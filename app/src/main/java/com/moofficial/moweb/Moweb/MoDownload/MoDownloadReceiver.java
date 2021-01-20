package com.moofficial.moweb.Moweb.MoDownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;

public class MoDownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MoLog.print("download receiver " + action);

        if (action == null) {
            return;
        }

        int id = intent.getIntExtra(MoRequestListener.EXTRA_ID,0);

        switch (action) {
            case MoDownloadManager.ACTION_DELETE_NOTIFICATION:
                MoDownloadManager.remove(context, id);
                break;
            case MoDownloadManager.ACTION_OPEN_NOTIFICATION:

                break;
            case MoDownloadManager.ACTION_PAUSE_DOWNLOAD:
                MoDownloadManager.pause(id);
                break;
            case MoDownloadManager.ACTION_CANCEL_DOWNLOAD:
                MoDownloadManager.cancel(context, id);
                break;
            case MoDownloadManager.ACTION_RESUME_DOWNLOAD:
                MoDownloadManager.resume(id);
                break;
        }


    }
}