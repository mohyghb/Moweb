package com.moofficial.moweb.Moweb.MoDownload;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileExtension;
import com.moofficial.moweb.Moweb.MoWebManifest;
import com.moofficial.moweb.R;
import com.tonyodev.fetch2.Download;

import java.io.File;

public class MoDownloadUtils {

    public static String readableSpeed(long speed) {
        return readableSpeed(speed, "s");
    }

    /**
     * converts the speed given
     * @param speed in bytes
     * @param per unit of time that the speed is in
     * @return a readable format of speed
     */
    public static String readableSpeed(long speed, String per) {
        return formatSize(speed) + "/" + per;
    }

    /**
     * returns a readable size that is formatted
     * @param size
     * @return
     */
    public static String formatSize(long size) {
        long kb = size/1024;
        if (kb < 1000) {
            return kb + " KB";
        }
        long MB = kb / 1000;
        if (MB < 1000) {
            return MB + " MB";
        }
        long GB = MB/1000;

        return GB  + " GB";
    }

    /**
     * opens a download
     * @param context
     * @param download
     */
    public static void openDownload(Context context, File download) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String mime = MoFileExtension.getMimeType(download);
        Uri uri = FileProvider.getUriForFile(context, MoWebManifest.FILE_PROVIDER_AUTHORITY, download);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try { 
            context.startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(context, R.string.download_could_not_open_file, Toast.LENGTH_SHORT).show();
        }

    }

    public static void openDownloadFromNotification(Context context, Download d) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        File download = new File(d.getFile());
        String mime = MoFileExtension.getMimeType(download);
        Uri uri = FileProvider.getUriForFile(context, MoWebManifest.FILE_PROVIDER_AUTHORITY, download);
        intent.setDataAndType(uri, mime);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch(Exception e) {
            Toast.makeText(context, R.string.download_could_not_open_file, Toast.LENGTH_SHORT).show();
        }
    }

}
