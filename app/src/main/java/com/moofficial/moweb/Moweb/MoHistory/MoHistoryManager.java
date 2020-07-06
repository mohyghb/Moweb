package com.moofficial.moweb.Moweb.MoHistory;

import android.content.Context;

import com.moofficial.moweb.MoIO.MoFile;
import com.moofficial.moweb.MoLog.MoLog;
import com.moofficial.moweb.MoReadWrite.MoReadWrite;
import com.moofficial.moweb.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.Calendar;

public class MoHistoryManager {




    private static final String HISTORY_FILE = "historys&a4ve&d";


    private static ArrayList<MoHistory> histories = new ArrayList<>();
    public static boolean historyEnabled = true;


    /**
     * adds a history if history adding is enabled
     * and the previous history is not same as the current one
     * that we are trying to add
     * @param url
     * @param title
     * @param c
     */
    public static void add(String url,String title,Context c){
        // return if the user has turned off the
        // history saving
        if(!historyEnabled)
            return;

        MoHistory last = getLastHistory();
        if(last!=null && last.isSame(url,title)){
            // if the provided url and title
            // have already been inside the history
            // then just increase the count
            histories.get(histories.size()-1).increment();
        }else{
            MoHistory h = new MoHistory(url,title);
            // before we add it, we need to see
            // if we need to add a new date tile
            addDateTileIfNeeded(last,h);
            histories.add(h);
            save(c);
        }
    }


    /**
     * returns true if h1 and h2 need a date tile between them
     * based on if they have a different DATE (Or day)
     * @param h1
     * @param h2
     * @return
     */
    private static boolean needDateTile(MoHistory h1,MoHistory h2){
        return h1.getDateTime().isDifferent(h2.getDateTime(), Calendar.DATE,Calendar.MONTH,Calendar.YEAR);
    }

    private static void addDateTileIfNeeded(MoHistory h1,MoHistory h2){
        if(h1!=null && h2!=null && needDateTile(h1,h2)){
            // h1 has happened before h2
            // we are just checking to see
            // if they happened on different dates or not
            histories.add(new MoHistory(h1.getDateTime()));
        }
    }


    /**
     * removes the history at [index]
     * @param index
     * @param c
     */
    public static void remove(int index,Context c){
        histories.remove(index);
        save(c);
    }


    /**
     * saves the web histories of the user
     * @param context
     */
    public static void save(Context context){
        MoReadWrite.saveFile(HISTORY_FILE, MoFile.getData(histories),context);
    }




    /**
     * loads the histories inside a list
     * @param c
     */
    public static void load(Context c){
        // avoids overloading it
        if(!histories.isEmpty()){
            return;
        }
        String[] com = MoFile.loadable(MoReadWrite.readFile(HISTORY_FILE,c));
        // load them in backwards
        for(String s: com){
            if(!s.isEmpty()){
                MoHistory h = new MoHistory();
                h.load(s,c);
                histories.add(h);
            }
        }
    }

    /**
     * clears all the history with no exception
     */
    public static void clearHistory(Context context){
        histories.clear();
        save(context);
    }


    /**
     *
     * @returns the history of their search
     */
    public static ArrayList<MoHistory> getHistories() {
        return histories;
    }

    /**
     *
     * @return history array list with added
     * date tiles to distinguish between the different
     * histories of the user by their criteria
     */
    public static ArrayList<MoHistory> getHistoriesWithDateTiles(int count) {
        ArrayList<MoHistory> list = new ArrayList<>();
        if(histories.size() <= count){
            return histories;
        }
        for(int i = histories.size()-count; i < histories.size();i++){
            list.add(histories.get(i));
        }
        return list;
    }


    /**
     * adds the appropriate history to suggestion list
     * to be shown when the user is typing
     * @param search what user has typed so far
     * @param suggestion suggestions that we have created for user
     */
    public static void addSuggestionsFromHistory(String search, MoSuggestions suggestion){
        for(MoHistory h: histories){
            h.addToSuggestionIfApplicable(search,suggestion);
        }
    }


    /**
     *
     * @return the last history inside the array list
     */
    public static MoHistory getLastHistory(){
        if(histories.isEmpty()){
            // no last history
            return null;
        }
        boolean b = histories.get(histories.size()-1).isDateTile();
        if(!b){
            // last one is not a date tile
            return histories.get(histories.size()-1);
        }
        if(histories.size()<=1){
            // if the last one is a date tile
            // and the size is only one
            // then there is no last history
            return null;
        }
        return histories.get(histories.size()-2);
    }


    /**
     *
     * @param s what the user is searching for
     * @return a list of histories that are applicable to what
     * the user was searching for
     */
    public static ArrayList<MoHistory> search(String s){
        ArrayList<MoHistory> list = new ArrayList<>();
        for(MoHistory h: histories){
            if(h.isRelatedTo(s)){
                list.add(h);
            }
        }
        return list;
    }



    public static void updateSharedPref(Context c){
        historyEnabled = MoSharedPref.get(c.getString(R.string.history_enabled),true);
    }

}
