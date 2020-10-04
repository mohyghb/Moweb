package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// manages the general auto fills
public class MoGeneralAutoFillManager {

    public static final String GENERAL_AUTO_FILL_DIR = "auto_fills_general";
    private static final List<MoLinkedAutoFills> autoFills = new ArrayList<>();


    /**
     * add the auto-fill to the
     * map and save it
     * @param context context of the app for saving
     * @param a auto-fill to be added
     * @throws IOException
     */
    public static synchronized void add(Context context,MoLinkedAutoFills a) throws IOException {
        if (a.isNotEmpty()) {
            addAutoFill(a);
            a.save(context);
        }

    }

    /**
     * adds the auto fill to list of
     * auto-fills inside the map
     * for the host of that auto-fill
     * @param linkedAutoFills to be added to our auto-fills
     */
    @SuppressWarnings("ConstantConditions")
    private static void addAutoFill(MoLinkedAutoFills linkedAutoFills) {
        autoFills.add(linkedAutoFills);
    }


    /**
     * loads all the auto-fills into
     * the map of auto-fills
     * @param context context
     */
    public static void load(Context context) {
        if (autoFills.isEmpty()) {
            // only load when it is empty
            MoFileManager.readAllDirFilesAsync(context, GENERAL_AUTO_FILL_DIR, (s, i) -> {
                MoLinkedAutoFills linked = new MoLinkedAutoFills();
                linked.load(s,context);
                addAutoFill(linked);
            });
        }
    }


    /**
     * return the auto-fill for the host
     * @return list of auto-fills for the given host
     */
    public static List<MoLinkedAutoFills> get() {
        return autoFills;
    }



}
