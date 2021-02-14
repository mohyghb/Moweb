package com.moofficial.moweb.Moweb.MoDownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.tonyodev.fetch2.Download;

import java.io.File;

public class MoDownloadReceiver extends BroadcastReceiver {

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        MoLog.print("download receiver " + action);

        if (action == null) {
            return;
        }

        Download download = intent.getParcelableExtra(MoRequestListener.EXTRA_DOWNLOAD);

        switch (action) {
            case MoDownloadManager.ACTION_DELETE_NOTIFICATION:
                MoDownloadManager.remove(context, download);
                break;
            case MoDownloadManager.ACTION_OPEN_NOTIFICATION:
                MoDownloadUtils.openDownloadFromNotification(context, download);
                MoDownloadManager.remove(context, download);
                break;
            case MoDownloadManager.ACTION_PAUSE_DOWNLOAD:
                MoDownloadManager.pause(download.getId());
                break;
            case MoDownloadManager.ACTION_CANCEL_DOWNLOAD:
                MoDownloadManager.cancel(context, download);
                break;
            case MoDownloadManager.ACTION_RESUME_DOWNLOAD:
                MoDownloadManager.resume(download.getId());
                break;
        }


    }
}