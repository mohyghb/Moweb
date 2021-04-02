package com.moofficial.moweb.Moweb.MoHomePage;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;
import com.moofficial.moessentials.MoEssentials.MoValidate.MoTextValidate;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

import java.util.ArrayList;

public class MoHomePageManager {

    private static final int NONE_ACTIVATED = -1;
    private static final String FILE_NAME = "homepagefile";
    private static ArrayList<MoHomePage> homePages = new ArrayList<>();
    private static Integer activeHomePageIndex = NONE_ACTIVATED;

    /**
     * @param url
     * @return true if this was added, false if it already was in the list
     */
    public static boolean add(Context context, String url) {
        MoHomePage p = new MoHomePage(url);
        if (!homePages.contains(p)) {
            homePages.add(p);
            activateOneIfNoneIsActivated(context);
            save(context);
            return true;
        }
        return false;
    }

    /**
     * activates the home page at [index]
     *
     * @param index
     */
    public static void activate(Context c, int index) {
        activeHomePageIndex = index;
        for (int i = 0; i < homePages.size(); i++) {
            if (i == index) {
                homePages.get(i).setActivated(true);
            } else {
                homePages.get(i).setActivated(false);
            }
        }
        save(c);
    }

    /**
     *
     */
    public static void activateOneIfNoneIsActivated(Context c) {
        if (activeHomePageIndex == NONE_ACTIVATED || activeHomePageIndex >= homePages.size()) {
            if (homePages.isEmpty()) {
                activeHomePageIndex = NONE_ACTIVATED;
            } else {
                activate(c, homePages.size() - 1);
            }
        }
    }

    /**
     * deletes all the selected home pages
     *
     * @param context
     */
    public static void deleteAllSelected(Context context) {
        for (int i = homePages.size() - 1; i >= 0; i--) {
            if (homePages.get(i).isSelected()) {
                homePages.remove(i);
                if (i == activeHomePageIndex) {
                    activeHomePageIndex = NONE_ACTIVATED;
                }
            }
        }
        activateOneIfNoneIsActivated(context);
        save(context);
    }

    /**
     * @param context
     * @param position
     */
    public static void remove(Context context, int position) {
        homePages.remove(position);
        save(context);
    }

    public static void clear(Context context) {
        homePages.clear();
        save(context);
    }

    /**
     * @param context
     */
    public static void save(Context context) {
        MoReadWrite.saveFile(FILE_NAME, MoFile.getData(homePages, activeHomePageIndex), context);
    }

    /**
     * loads back in the active index
     *
     * @param context
     */
    public static void load(Context context) {
        if (homePages.isEmpty()) {
            String[] data = MoFile.loadable(MoReadWrite.readFile(FILE_NAME, context));
            if (MoFile.isValidData(data)) {
                activeHomePageIndex = Integer.parseInt(data[1]);
                String[] hps = MoFile.loadable(data[0]);
                for (String h : hps) {
                    if (!h.isEmpty()) {
                        try {
                            homePages.add(new MoHomePage(h, context));
                        } catch (Exception ignore) {
                        }
                    }
                }
                activateOneIfNoneIsActivated(context);
            }
        }
    }


    /**
     * @return
     */
    public static ArrayList<MoHomePage> get() {
        return homePages;
    }


    /**
     * returns the current activated home page
     */
    private static MoHomePage getCurrentActivatedHomePage() {
        return homePages.get(activeHomePageIndex);
    }

    /**
     * returns the home page url that the user has set up or
     * the home page of the search engine of their choice
     *
     * @return
     */
    public static String getCurrentActivatedURL() {
        if (activeHomePageIndex == NONE_ACTIVATED || activeHomePageIndex >= homePages.size()) {
            return MoSearchEngine.instance.homePage();
        } else {
            return getCurrentActivatedHomePage().getUrl();
        }
    }


    /**
     * validates whether the input is a valid
     * home page or not
     *
     * @param c
     * @param url
     * @return
     */
    public static MoTextValidate validate(Context c, String url) {
        MoTextValidate textValidate = new MoTextValidate().setValidate(false);
        MoHomePage homePage = new MoHomePage(url);
        if (url.isEmpty()) {
            textValidate.setErrorMessage(c.getString(R.string.error_bookmark_empty_url));
        } else if (homePages.contains(homePage)) {
            textValidate.setErrorMessage(c.getString(R.string.already_exist_home_page));
        } else {
            textValidate.setValidate(true);
        }
        return textValidate;
    }

    /**
     * applies the home page to the tab
     *
     * @param t tab to go to the home page on
     */
    public static void homePage(MoTab t) {
        t.search(getCurrentActivatedURL());
    }

}
