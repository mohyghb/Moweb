package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.content.Context;
import android.webkit.WebHistoryItem;

import com.moofficial.moessentials.MoEssentials.MoDate.MoDate;
import com.moofficial.moessentials.MoEssentials.MoDate.MoTimeUtils;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSwitchSavable;
import com.moofficial.moessentials.MoEssentials.MoShare.MoTextShareable;
import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestion;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoTabOpenable;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;

import java.util.List;

public class MoHistory implements MoSwitchSavable, MoLoadable,
        MoSelectableItem, MoSearchableItem, MoTextShareable, MoTabOpenable {



    private final float SUGGESTION_TOLERANCE = 0.7f;
    // if the user keeps loading the same web site, we consider that
    // to be the same and we optimize it for better history management
    private final long HISTORY_ACCEPTANCE_LIMIT = 60 * 1000 * 10;
    private final int TEXT_COUNT = 20;
    public static final int TYPE_HISTORY = 0;
    public static final int TYPE_DATE = 1;





    private MoURL url = new MoURL("");
    private MoDate dateTime = new MoDate();
    private String title = "";
    private MoHistoryBundle parentBundle;
    private int type = TYPE_HISTORY;
    // how many times this is called in a row
    private int count = 1;
    private boolean isSavable = true;
    private boolean isSelected = false;
    private boolean isSearched = true;

    public MoHistory(String url,String t) {
        this.url = new MoURL(url);
        this.title = t;
    }

    public MoHistory(){}

    public MoHistory(int type){
        this.type = type;
    }


    public MoHistory(Context c,String d){
        this.load(d,c);
    }


    public int getCount() {
        return count;
    }

    public MoHistory setCount(int count) {
        this.count = count;
        return this;
    }

    public MoHistory setUrl(MoURL url) {
        this.url = url;
        return this;
    }

    public MoHistory setTitle(String title) {
        this.title = title;
        return this;
    }

    public int getType() {
        return type;
    }

    public MoHistory setType(int type) {
        this.type = type;
        return this;
    }

    public MoHistoryBundle getParentBundle() {
        return parentBundle;
    }

    public MoHistory setParentBundle(MoHistoryBundle parentBundle) {
        this.parentBundle = parentBundle;
        return this;
    }

    public String getUrl() {
        return url.getUrlString();
    }



    public String getLimitedUrl() {
        return MoString.getLimitedCount(url.getUrlString(),TEXT_COUNT);
    }

    public MoDate getDateTime() {
        return dateTime;
    }

    public void setDateTime(MoDate dateTime) {
        this.dateTime = dateTime;
    }

    public String getDate(){
        return this.dateTime.getReadableDate();
    }

    public String getTitle() {
        return title;
    }



    public String getLimitedTitle() {
        return MoString.getLimitedCount(title,TEXT_COUNT);
    }



    public long getTimeInMillis(){
        return this.dateTime.getTimeInMillis();
    }


    /**
     * increase the count by 1
     */
    public void increment(){
        this.count++;
    }

    /**
     * adds the field to the suggestion if it is a
     * good suggestion:
     * we have not added a similar suggestion
     * and search is inside one
     * part of the field
     * @param suggestions list of suggestions
     * @param search user search
     */
    void addToSuggestionIfApplicable(String search, MoSuggestions suggestions) {
        float similarity = MoString.getSimilarity(title,search,true);
        if (similarity == 0f) {
            // look inside the url for similarity
            similarity = MoString.getSimilarity(getUrl(),search,true);
        }
        if(similarity>0f) {
            suggestions.add(new MoSuggestion(this,similarity));
        }
    }



    /**
     *
     * @param s
     * @return true if this history contains
     * something in the search that user is doing
     */
    public boolean isRelatedTo(String s){
        return this.getDate().contains(s) || this.title.contains(s) || this.url.getUrlString().contains(s);
    }

    /**
     *
     * @param url provided
     * @param title provided
     * @return true if the provided url and title are the
     * same as this url and title
     */
    public boolean isSame(String url,String title){
        return this.url.getUrlString().toLowerCase().equals(url.toLowerCase()) &&
                this.title.toLowerCase().equals(title.toLowerCase());
    }

    /**
     *
     * @param h
     * @return true if they are approximately the same
     */
    public boolean isApproximateSame(MoHistory h){
        if(h.getUrl().equals(this.getUrl())){
            // the urls are the same, check how different the
            // time difference is
            return !MoTimeUtils.isDifferenceMoreThanLimit(HISTORY_ACCEPTANCE_LIMIT,
                    this.getDateTime(),h.getDateTime());
        }
        return false;
    }


    /**
     * copies a web history item into
     * itself so it can be savable
     * @param item
     */
    public MoHistory copy(WebHistoryItem item){
        if(item == null)
            return null;
        this.title = item.getTitle();
        this.url = new MoURL(item.getUrl());
        // we can also get the fav icon from here
        return this;
    }

    public boolean isTypeHistory(){
        return this.type == TYPE_HISTORY;
    }

    public boolean isTypeDate(){
        return this.type == TYPE_DATE;
    }

    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] com = MoFile.loadable(data);
        this.url = new MoURL(com[0],context);
        this.dateTime = new MoDate(com[1],context);
        this.title = com[2];
        this.count = Integer.parseInt(com[3]);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(url,dateTime,title,count);
    }

    /**
     * adds all the data to a list of history
     * @param c
     * @param list
     * @param data
     */
    public static void addAll(Context c,List<MoHistory> list, String[] data){
        for(String d:data){
            list.add(new MoHistory(c,d));
        }
    }

    @Override
    public boolean isSavable() {
        return this.isSavable;
    }

    @Override
    public void setSavable(boolean b) {
        this.isSavable = b;
    }


    // mo selectable

    @Override
    public void setSelected(boolean b) {
        this.isSelected = b;
    }

    @Override
    public boolean isSelected() {
        return this.isSelected;
    }

    @Override
    public boolean isSelectable() {
        return this.isTypeHistory();
    }

    // mo searchable

    @Override
    public boolean updateSearchable(Object... objects) {
        this.isSearched = MoSearchableUtils.isSearchable(true,objects,
                getTitle(),getUrl(),getDate());
        return this.isSearched;
    }

    @Override
    public boolean isSearchable() {
        return this.isSearched;
    }

    @Override
    public void setSearchable(boolean b) {
        this.isSearched = b;
    }

    @Override
    public String getTextToShare() {
        return getUrl();
    }

    @Override
    public String getSearchString() {
        return getUrl();
    }
}
