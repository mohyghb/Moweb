package com.moofficial.moweb.Moweb.MoDownload;

import android.os.Environment;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;

import java.io.File;
import java.util.Objects;

public class MoDownloadManager {

    public static void fin() {
        File f = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        for (File files : Objects.requireNonNull(f.listFiles())) {
            MoLog.print(files.getName());
        }
        int i = 0;
    }

}
