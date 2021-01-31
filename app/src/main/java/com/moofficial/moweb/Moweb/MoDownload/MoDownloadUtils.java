package com.moofficial.moweb.Moweb.MoDownload;

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

}
