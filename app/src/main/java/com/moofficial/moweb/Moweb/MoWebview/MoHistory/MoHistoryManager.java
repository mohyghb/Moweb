package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class MoHistoryManager {


    public static final String HISTORY_DIR = "history_dir";



    public static HashMap<String,MoHistoryBundle> historyBundleHashMap = new HashMap<>();
    public static ArrayList<MoHistoryBundle> sortedBundles = new ArrayList<>();
    public static boolean historyEnabled = true;

    /**
     * adds the mo history to the bundle
     * only if the history is enabled
     * by the user
     * @param context of the app
     * @param history to be added
     */
    public synchronized static void add(Context context,MoHistory history){
        if(!historyEnabled)
            return;

        String key = history.getDateTime().getDateAsKey(MoHistoryBundle.KEY_SEPARATOR);
        try {
            handleAddingNewHistoryToBundle(context,history, key);
        } catch (IOException e) {
            MoLog.print("Error saving history line 27");
        }
    }

    /**
     * adds the url and title as a new
     * history to the histories file
     * @param context of the app
     * @param url of history
     * @param title of history
     */
    public static void add(Context context,String url,String title){
        add(context,new MoHistory(url,title));
    }

    /**
     * adds the url and title of the web view
     * to the history
     * @param webView to add history for
     */
    public static void add(WebView webView){
        add(webView.getContext(),webView.getUrl(),webView.getTitle());
    }

    /**
     * adds the history to a bundle if the
     * bundle already exists, or creates a new
     * bundle and stores the history inside there
     * @param history to be added
     * @param key unique date which is used as a key
     */
    private static void handleAddingNewHistoryToBundle(Context context,MoHistory history, String key) throws IOException {
        MoHistoryBundle bundle;
        if(historyBundleHashMap.containsKey(key)){
            bundle = historyBundleHashMap.get(key);
        }else{
            bundle = createHistoryBundle(key);
        }
        if (bundle != null) {
            bundle.add(history);
            bundle.save(context);
        }
    }

    /**
     * creates a history bundle to save all the histories of that
     * day inside there
     * @param key unique date used as hash map key
     */
    private static MoHistoryBundle createHistoryBundle(String key) {
        MoHistoryBundle historyBundle = new MoHistoryBundle();
        historyBundleHashMap.put(key,historyBundle);
        sortedBundles.add(historyBundle);
        return historyBundle;
    }

    /**
     * loads all the directory files of
     * history bundles and saves it inside the
     * hash map on the RAM
     * (only do it if the hash map is empty)
     * @param context
     * @throws IOException
     */
    public static void load(Context context) throws IOException {
        if(historyBundleHashMap.isEmpty()){
            MoFileManager.readAllDirFiles(context, HISTORY_DIR, (s, i) -> {
                MoHistoryBundle historyBundle = new MoHistoryBundle();
                historyBundle.load(s,context);
                historyBundleHashMap.put(historyBundle.getKey(),historyBundle);
            });
            // when we are done loading all the histories
            sortBundles();
        }
    }

    /**
     * sorts all the values of the hash map bundles
     * based on their date in ascending order
     * inside the sortedBundles array list
     */
    public static void sortBundles(){
        sortedBundles = new ArrayList<>(historyBundleHashMap.values());
        Collections.sort(sortedBundles, (moHistoryBundle, t1) ->
                Long.compare(moHistoryBundle.getTimeInMillis(),t1.getTimeInMillis()));
    }

    /**
     * returns all the histories with
     * history date tile inserted inside them
     * @return list of mo history
     */
    public static ArrayList<MoHistory> getAllHistories(){
        ArrayList<MoHistory> all = new ArrayList<>();
        for(int i = sortedBundles.size()-1;i>=0;i--){
            MoHistoryBundle b = sortedBundles.get(i);
            all.addAll(b.getHistories());
            all.add(b.getBundleDate());
        }
        Collections.reverse(all);
        return all;
    }

//    /**
//     * returns a history as a date tile
//     * needed to show to the user
//     * when we are showing which history belongs to what date
//     * @param b
//     * @return
//     */
//    private static MoHistory getDateHistoryForBundle(MoHistoryBundle b) {
//        MoHistory dateHistory = new MoHistory(MoHistory.TYPE_DATE);
//        dateHistory.setDateTime(b.getDate());
//        return dateHistory;
//    }


    /**
     * adds the appropriate history to suggestion list
     * to be shown when the user is typing
     * @param search what user has typed so far
     * @param suggestion suggestions that we have created for user
     */
    public static void addSuggestionsFromHistory(String search, MoSuggestions suggestion){
        for(MoHistoryBundle b: historyBundleHashMap.values()){
            b.addSuggestions(search,suggestion);
        }
    }

    /**
     * removes the list of MoHistory
     * passed in
     * @param context of the app
     * @param selected items inside a list
     * @throws IOException
     */
    public static void remove(Context context,List<MoHistory> selected,List<MoHistory> all) throws IOException {
        HashSet<MoHistoryBundle> toBeUpdatedBundles = new HashSet<>();
        removeHistories(selected, toBeUpdatedBundles,all);
        updateBundles(context,toBeUpdatedBundles,all);
    }

    /**
     * sets the savable field to false,
     * so the app does not save it anymore
     * @param selected selected histories to be removed
     * @param toBeUpdatedBundles history bundles to update
     *                           after we are done removing
     */
    private static void removeHistories(List<MoHistory> selected, HashSet<MoHistoryBundle> toBeUpdatedBundles,List<MoHistory> all) {
        for(MoHistory h:selected) {
            if(h.isTypeHistory()){
                toBeUpdatedBundles.add(h.getParentBundle());
                h.setSavable(false);
            }
        }
        all.removeAll(selected);
    }

    /**
     * updates the bundles by saving their history one more time inside
     * the phone's internal storage
     * also removes the bundle date from the list
     * if it is empty
     * @param context of the app
     * @param bundles bundles to be updated
     * @throws IOException
     */
    public static void updateBundles(Context context,Iterable<MoHistoryBundle> bundles,List<MoHistory> all) throws IOException {
        for(MoHistoryBundle b : bundles){
            b.update(context);
            removeBundleIfEmpty(b,all);
        }
    }


    /**
     * removes the bundle
     * @param all all of the histories
     * @param b bundle to be removed if it is empty
     */
    public static void removeBundleIfEmpty(MoHistoryBundle b,List<MoHistory> all){
        if(b.isEmpty()){
            all.remove(b.getBundleDate());
            sortedBundles.remove(b);
            historyBundleHashMap.remove(b.getKey());
        }
    }


    /**
     * updates whether the history is enabled or
     * not, this should be called when the user changes
     * the shared pref inside their setting
     * @param c
     */
    public static void updateSharedPref(Context c){
        historyEnabled = MoSharedPref.get(c.getString(R.string.history_enabled),true);
    }


//
//    private static void addDateTileIfNeeded(MoHistory h1,MoHistory h2){
//        if(h1!=null && h2!=null && needDateTile(h1,h2)){
//            // h1 has happened before h2
//            // we are just checking to see
//            // if they happened on different dates or not
//            histories.add(new MoHistory(h1.getDateTime()));
//        }
//    }
//
//
//    /**
//     * removes the history at [index]
//     * @param index
//     * @param c
//     */
//    public static void remove(int index,Context c){
//        histories.remove(index);
//        save(c);
//    }
//
//
//    /**
//     * saves the web histories of the user
//     * @param context
//     */
//    public static void save(Context context){
//        MoReadWrite.saveFile(HISTORY_FILE, MoFile.getData(histories),context);
//    }
//
//
//
//
//    /**
//     * loads the histories inside a list
//     * @param c
//     */
//    public static void load(Context c){
//        // avoids overloading it
//        if(!histories.isEmpty()){
//            return;
//        }
//        String[] com = MoFile.loadable(MoReadWrite.readFile(HISTORY_FILE,c));
//        if(MoFile.isValidData(com)){
//            String[] his = MoFile.loadable(com[0]);
//            // load them in backwards
//            for(String s: his){
//                if(!s.isEmpty()){
//                    MoHistory h = new MoHistory();
//                    h.load(s,c);
//                    histories.add(h);
//                }
//            }
//        }
//    }
//
//    /**
//     * clears all the history with no exception
//     */
//    public static void clearHistory(Context context){
//        histories.clear();
//        save(context);
//    }
//
//
//    /**
//     *
//     * @returns the history of their search
//     */
//    public static ArrayList<MoHistory> getHistories() {
//        return histories;
//    }
//
//    /**
//     *
//     * @return history array list with added
//     * date tiles to distinguish between the different
//     * histories of the user by their criteria
//     */
//    public static ArrayList<MoHistory> getHistoriesWithDateTiles(int count) {
//        ArrayList<MoHistory> list = new ArrayList<>();
//        if(histories.size() <= count){
//            return histories;
//        }
//        for(int i = histories.size()-count; i < histories.size();i++){
//            list.add(histories.get(i));
//        }
//        return list;
//    }
//
//

//
//
//    /**
//     *
//     * @return the last history inside the array list
//     */
//    public static MoHistory getLastHistory(){
//        if(histories.isEmpty()){
//            // no last history
//            return null;
//        }
//        boolean b = histories.get(histories.size()-1).isDateTile();
//        if(!b){
//            // last one is not a date tile
//            return histories.get(histories.size()-1);
//        }
//        if(histories.size()<=1){
//            // if the last one is a date tile
//            // and the size is only one
//            // then there is no last history
//            return null;
//        }
//        return histories.get(histories.size()-2);
//    }
//
//
//    /**
//     *
//     * @param s what the user is searching for
//     * @return a list of histories that are applicable to what
//     * the user was searching for
//     */
//    public static ArrayList<MoHistory> search(String s){
//        ArrayList<MoHistory> list = new ArrayList<>();
//        for(MoHistory h: histories){
//            if(h.isRelatedTo(s)){
//                list.add(h);
//            }
//        }
//        return list;
//    }
//
//    public static int size(){
//        return histories.size();
//    }
//
//
//
//

}
