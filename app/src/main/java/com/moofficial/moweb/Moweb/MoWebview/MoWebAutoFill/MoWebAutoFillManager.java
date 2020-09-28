package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;

import java.io.IOException;
import java.util.HashMap;

// manages the auto fills
public class MoWebAutoFillManager {

    public static final String DIR_NAME = "auto_fills";
    private static final HashMap<String,MoWebAutoFills> autoFills = new HashMap<>();


    /**
     * add the auto-fill to the
     * map and save it
     * @param context context of the app
     * @param a auto-fill to be added
     * @throws IOException
     */
    public static synchronized void add(Context context,MoWebAutoFills a) throws IOException {
        addAutoFill(a);
        a.save(context);
    }

    /**
     * adds the auto fill to the map
     * @param a auto-fill to be added
     */
    private static void addAutoFill(MoWebAutoFills a) {
        autoFills.put(a.getHost(), a);
    }


    /**
     * loads all the auto-fills into
     * the map of auto-fills
     * @param context context
     */
    public static void load(Context context) {
        MoFileManager.readAllDirFilesAsync(context, DIR_NAME, (s, i) -> {
            MoWebAutoFills autoFill = new MoWebAutoFills(s,context);
            synchronized (autoFills) {
                addAutoFill(autoFill);
            }
        });
    }

    /**
     * return the auto-fill for the host
     * @param host of the web to return
     *            the auto-fill for
     * @return auto-fill for the given host
     */
    public static MoWebAutoFills get(String host) {
        return autoFills.get(host);
    }



}
