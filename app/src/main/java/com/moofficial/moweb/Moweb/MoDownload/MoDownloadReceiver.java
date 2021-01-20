package com.moofficial.moweb.Moweb.MoDownload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;

public class MoDownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        MoLog.print("download receiver ");
        String action = intent.getAction();

        if (action == null) {
            return;
        }

        switch (action) {
            case MoDownloadManager.ACTION_DELETE_NOTIFICATION:
                MoDownloadManager.remove(context,
                        intent.getIntExtra(MoDownloadItem.EXTRA_ID,0));
                break;
            case MoDownloadManager.ACTION_OPEN_NOTIFICATION:

                break;
        }


    }
}