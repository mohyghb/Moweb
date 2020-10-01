package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MoUserPassManager {

    public static final String DIR_NAME = "user_pass_dir";
    // <host, list of auto-fills>
    private static final HashMap<String, List<MoUserPassAutoFill>> userPasses = new HashMap<>();

    /**
     * creates a new username and password
     * auto-fill based on the params that
     * are passed for the given host
     * @param c
     * @param username
     * @param password
     * @param host
     * @throws IOException
     */
    @SuppressWarnings("ConstantConditions")
    public static void add (Context c,  MoWebAutoFill username, MoWebAutoFill password, String host) throws IOException {
        MoUserPassAutoFill autoFill = new MoUserPassAutoFill(username,password,host);
        putToMap(autoFill);
        autoFill.save(c);
    }

    /**
     * puts the auto-fill inside a map
     * @param autoFill
     */
    private static void putToMap(MoUserPassAutoFill autoFill) {
        if (userPasses.containsKey(autoFill.getHost())) {
            userPasses.get(autoFill.getHost()).add(autoFill);
        } else {
            List<MoUserPassAutoFill> p = new ArrayList<>();
            p.add(autoFill);
            userPasses.put(autoFill.getHost(),p);
        }
    }

    /**
     * load the user name and passwords
     * of the user inside the map
     * or return if it has already something inside it
     * @param c context
     */
    public static void load(Context c) {
        if (!userPasses.isEmpty())
            return;
        MoFileManager.readAllDirFilesAsync(c, DIR_NAME, (s, i) -> {
            MoUserPassAutoFill userPassAutoFill = new MoUserPassAutoFill();
            userPassAutoFill.load(s,c);
            synchronized (userPasses) {
                putToMap(userPassAutoFill);
            }
        });
    }

}
