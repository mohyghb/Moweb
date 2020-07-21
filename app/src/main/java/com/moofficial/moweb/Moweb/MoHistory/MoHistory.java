package com.moofficial.moweb.Moweb.MoHistory;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoDate.MoDate;
import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;


import com.moofficial.moweb.MoString.MoString;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;

public class MoHistory implements MoSavable, MoLoadable {


    private final String SEP_KEY = "&mohis&tory&";
    private final float SUGGESTION_TOLERANCE = 0.7f;

    private final int TEXT_COUNT = 20;

    public static final int TYPE_HISTORY = 0;
    // date tile is used inside the recycler view
    // to differentiate between different histories
    public static final int TYPE_DATE_TILE = 1;


    private MoURL url = new MoURL("");
    private MoDate dateTime = new MoDate();
    private String title = "";
    private int type = TYPE_HISTORY;
    // how many times this is called in a row
    private int count = 1;


    public MoHistory(String url,String t) {
        this.url = new MoURL(url);
        this.dateTime = new MoDate();
        this.title = t;
        this.type = TYPE_HISTORY;
    }

    public MoHistory(){}

    // constructor for a date tile
    public MoHistory(MoDate date){
        this.dateTime = date;
        this.type = TYPE_DATE_TILE;
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

    public String getSignatureLetter(){
        if(title==null || title.isEmpty()){
            return "";
        }
        return this.title.charAt(0)+"";
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }


    public boolean isDateTile(){
        return this.type == TYPE_DATE_TILE;
    }

    /**
     * increase the count by 1
     */
    public void increment(){
        this.count++;
    }

    void addToSuggestionIfApplicable(String search, MoSuggestions suggestions){
        if(isAGoodSuggestion(this.title,search)){
            suggestions.add(this.title);
        }
        if(isAGoodSuggestion(this.url.getUrlString(),search)){
            suggestions.add(this.url.getUrlString());
        }
    }

    // if a is a good suggestion for what b is going to be
    private boolean isAGoodSuggestion(String a, String b){
        return MoString.getSimilarity(a,b) >= SUGGESTION_TOLERANCE;
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
        return this.url.getUrlString().equals(url) && this.title.equals(title);
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
        this.type = Integer.parseInt(com[4]);
    }

    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(url.getData(),this.dateTime.getData(),title,count,type);
    }
}
