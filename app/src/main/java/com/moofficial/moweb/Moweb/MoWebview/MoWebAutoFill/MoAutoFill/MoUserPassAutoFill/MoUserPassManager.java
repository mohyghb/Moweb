package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MoUserPassManager {

    public static final String DIR_NAME = "user_pass_dir";
    public static final String NEVER_SAVE_FILE_NAME = "never_save_auto_fills";
    // <host, list of auto-fills>
    private static final HashMap<String, List<MoUserPassAutoFill>> userPasses = new HashMap<>();
    private static final List<MoUserPassAutoFill> allPasses = new ArrayList<>();
    // do not save any password for any host inside this set
    private static final HashSet<String> neverSaveSet = new HashSet<>();

    /**
     * creates a new username and password
     * auto-fill based on the params that
     * are passed for the given host
     * @param c
     * @throws IOException
     */
    @SuppressWarnings("ConstantConditions")
    public static void add (Context c, MoUserPassAutoFill a) throws IOException {
        putToMap(a);
        a.save(c);
    }

    /**
     * puts the auto-fill inside a map
     * @param autoFill
     */
    private static synchronized void putToMap(MoUserPassAutoFill autoFill) {
        if (userPasses.containsKey(autoFill.getHost())) {
            userPasses.get(autoFill.getHost()).add(autoFill);
        } else {
            List<MoUserPassAutoFill> p = new ArrayList<>();
            p.add(autoFill);
            userPasses.put(autoFill.getHost(),p);
        }
        allPasses.add(autoFill);
    }

    /**
     * removes the auto fill from map and list
     * @param autoFill to be removed from both databases
     */
    private static synchronized void removeFromMap(MoUserPassAutoFill autoFill) {
        userPasses.get(autoFill.getHost()).remove(autoFill);
        allPasses.remove(autoFill);
    }

    /**
     * saves the never save username and password
     * host set
     * @param c context for saving
     * @param host to never save the user pass for
     */
    public static void neverSave(Context c,String host) {
        neverSaveSet.add(host);
        saveNeverSaveSet(c);
    }

    /**
     * true if we should not save the user pass
     * for the given host
     * @param host to save or reject saving
     * @return true if we should save
     * user name password for given host
     */
    public static boolean neverSave(String host) {
        return neverSaveSet.contains(host);
    }

    /**
     * save the files into the disk
     * for later use
     * @param c context for saving
     */
    private static void saveNeverSaveSet(Context c) {
        try {
            MoFileManager.writeInternalFile(c,NEVER_SAVE_FILE_NAME, MoFile.getData(neverSaveSet));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * return true if we already
     * have this auto fill
     * already for the given host
     * @param a auto fill
     * @return true if we already saved
     * user and pass for this account
     */
    @SuppressWarnings("ConstantConditions")
    public static boolean has(MoUserPassAutoFill a){
        if (userPasses.containsKey(a.getHost())) {
            // then we MIGHT have the password already saved
            return userPasses.get(a.getHost()).contains(a);
        } else {
            // we don't have any password for the given host
            return false;
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
            putToMap(userPassAutoFill);
            //generateFakePasswordAutoFills(c);
        });
    }


    /**
     * get all the saved passwords inside one
     * list
     * @return list of all passwords
     */
    public static List<MoUserPassAutoFill> get() {
        Collections.sort(allPasses, (o1, o2) -> Long.compare(o1.getDateTime(),o2.getDateTime()));
        return allPasses;
    }

    /**
     * get all the user passes for the specific
     * host that is given
     * @return list of all passwords for the given host
     */
    public static List<MoUserPassAutoFill> get(String host) {
        return userPasses.get(host);
    }


    /**
     * delete all the selected password
     * auto fills from the database
     */
    public static void deleteSelected(Context context,List<MoUserPassAutoFill> selected) {
        for (MoUserPassAutoFill a: selected) {
            removeFromMap(a);
            a.delete(context);
        }
    }


    // generate fake auto completes for passwords
    private static void generateFakePasswordAutoFills(Context context) {
        String host = "accounts.google.com";
        List<MoUserPassAutoFill> list =  MoUserPassManager.get(host);
        MoUserPassAutoFill main = list.get(0);
        MoWebAutoFill pass = main.getPassword();
        MoWebAutoFill user = main.getUsername();
        for(int i = 0; i < 10; i++) {
            MoUserPassAutoFill autoFill = new MoUserPassAutoFill().setHost(main.getHost())
                    .setUsername(new MoWebAutoFill(user.getId(),i+"user",user.getFieldType(),user.getAutoCompleteType()))
                    .setPassword(new MoWebAutoFill(pass.getId(),i+"pass",pass.getFieldType(),pass.getAutoCompleteType()));
            try {
                MoUserPassManager.add(context,autoFill);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
