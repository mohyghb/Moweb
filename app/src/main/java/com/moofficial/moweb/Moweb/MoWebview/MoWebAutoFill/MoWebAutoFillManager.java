package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoDir;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// manages the auto fills
public class MoWebAutoFillManager {

    public static final String DIR_NAME = "auto_fills";
    private static List<MoWebAutoFills> autoFills = new ArrayList<>();


    public static void add(Context context,MoWebAutoFills a) throws IOException {
        autoFills.add(a);
        a.save(context);
    }

    public static void remove(Context c,int index) {
        autoFills.get(index).delete(c);
        autoFills.remove(index);
    }


    public static void load(Context context) {
        MoWebAutoFills[] temp = new MoWebAutoFills[MoDir.getFilesSizeDir(context,DIR_NAME)];
        MoFileManager.readAllDirFilesAsync(context, DIR_NAME, (s, i) -> {
            MoWebAutoFills autoFill = new MoWebAutoFills(s,context);
            synchronized (temp) {
                temp[i] = autoFill;
            }
        });
        Collections.addAll(autoFills,temp);
    }



}
