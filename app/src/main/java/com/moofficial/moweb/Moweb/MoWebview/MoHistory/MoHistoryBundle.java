package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManagerUtils;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
    a class which stores multiple histories which belong to one
    exact date like 02/02/2020
 */
public class MoHistoryBundle implements MoFileSavable, MoLoadable {

    public static final String KEY_SEPARATOR = ",";

    private MoHistory bundleDate = new MoHistory(MoHistory.TYPE_DATE);
    private List<MoHistory> histories = new ArrayList<>();
    private String key;


    public MoHistory getBundleDate() {
        return bundleDate;
    }

    public MoHistoryBundle setBundleDate(MoHistory bundleDate) {
        this.bundleDate = bundleDate;
        return this;
    }

    public boolean isEmpty(){
        return this.histories.isEmpty();
    }

    public List<MoHistory> getHistories() {
        return histories;
    }

    public MoHistoryBundle setHistories(List<MoHistory> histories) {
        this.histories = histories;
        return this;
    }

    /**
     * adds histories to the user suggestion search
     * when they are typing inside the box if
     * the suggestion is a good one
     * @param search search text typed by the user
     * @param suggestion a list of suggestions
     */
    public void addSuggestions(String search, MoSuggestions suggestion){
        for(MoHistory h: histories) {
            if(suggestion.reachedLimit()) break;
            h.addToSuggestionIfApplicable(search,suggestion);
        }
    }


    public long getTimeInMillis(){
        return this.bundleDate.getTimeInMillis();
    }

    public String getKey(){
        if(key==null){
            this.key = this.bundleDate.getDateTime().getDateAsKey(KEY_SEPARATOR);
        }
        return this.key;
    }

    /**
     * adds the history to the list if the last
     * history is not the same as the one that is passed
     * if it is, we just increment the last history added
     * if not we just add it to the list
     * @param h history to be added
     */
    public void add(MoHistory h){
        if(histories.isEmpty()){
            // if the histories is empty just add it
            addToHistory(h);
        }else {
            // if not, check to see if the last
            // history added is the same as this one
            // if it is, don't add it, just increment the history
            MoHistory last = lastHistory();
            if(last.isApproximateSame(h)){
                last.increment();
            }else{
                addToHistory(h);
            }
        }
    }

    /**
     * just add the history to the list
     * without any checking
     * @param h history to be added
     */
    private void addToHistory(MoHistory h){
        h.setParentBundle(this);
        histories.add(h);
    }

    /**
     * saves the history bundle inside the internal
     * directory to be loaded back in later
     * @param context
     * @throws IOException
     */
    public void save(Context context,boolean removeUnSavableAfter) throws IOException {
        MoFileManagerUtils.write(context,this);
        removeUnSavableFromList(removeUnSavableAfter);
    }

    /**
     * overloaded method for saving
     * @param context
     * @throws IOException
     */
    public void save(Context context) throws IOException {
        save(context,false);
    }

    /**
     * deletes the file for this bundle
     * @param context
     */
    public void delete(Context context){
        MoFileManagerUtils.delete(context,this);
    }

    /**
     * updates the history after we remove
     * all the UnSavable from the list
     * if this bundle holds any other histories
     * or deletes the bundle since it does
     * not hold any more histories
     * @param context of the app
     * @throws IOException
     */
    public void update(Context context) throws IOException {
        removeUnSavableFromList(true);
        if(this.histories.isEmpty()){
            // delete the bundle, since it holds no history anymore
            delete(context);
        }else{
            save(context);
        }
    }


    /**
     * removes all the histories of which satisfy the requirement
     * @param threshold if the history milli
     */
    public boolean removeHistoriesFrom(Context context,long threshold) throws IOException {
        for(int i = histories.size()-1;i>=0;i--){
            MoHistory h = histories.get(i);
            if(h.getTimeInMillis()>=threshold) {
                // this history should be removed
                histories.remove(i);
            }else{
                // we reached a point inside the bundle
                // where the current history is below the threshold
                // therefore, since the histories are sorted already
                // we can assume that the rest are going to be the same
                // so return false, so the operation should not be continued
                update(context);
                return false;
            }
        }
        // because we traversed through all of them and we still need to go through other bundles
        update(context);
        return true;
    }


    /**
     *
     * @param removeUnSavableAfter if true, we remove the histories which are not
     *                             savable
     */
    private void removeUnSavableFromList(boolean removeUnSavableAfter) {
        if(removeUnSavableAfter){
            for(int i = histories.size()-1;i>=0;i--){
                if(!histories.get(i).isSavable()){
                    histories.remove(i);
                }
            }
        }
    }

    /**
     * removes the bundle date from the list of
     * all histories if the bundle is empty
     * @param allHistories
     */
    public void removeFromListIfEmpty(List<MoHistory> allHistories){
        if(this.isEmpty()){
            allHistories.remove(bundleDate);
        }
    }


    /**
     * the histories list can not be empty
     * @return the last history inside the list
     */
    public MoHistory lastHistory() {
        return this.histories.get(this.histories.size()-1);
    }


    @Override
    public String getDirName() {
        return MoHistoryManager.HISTORY_DIR;
    }

    @Override
    public String getFileName() {
        return getKey();
    }

    @Override
    public void load(String s, Context context) {
        String[] com = MoFile.loadable(s);
        this.bundleDate.load(com[0],context);
        MoFile.setData(context, com[1], histories, (context1, s1) -> {
            MoHistory h = new MoHistory();
            h.load(s1, context1);
            h.setParentBundle(this);
            return h;
        });

    }

    @Override
    public String getData() {
        return MoFile.getData(this.bundleDate,this.histories);
    }
}
