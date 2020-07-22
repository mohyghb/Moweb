package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoDate.MoDate;
import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSwitchSavable;
import com.moofficial.moessentials.MoEssentials.MoSearchable.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoSelectable.MoSelectableItem;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;

import java.util.ArrayList;
import java.util.Objects;

/**
 * a class which can be both a bookmark and a folder
 * based on the type that is given to it
 */
public class MoBookmark implements MoSwitchSavable, MoLoadable, MoSelectableItem, MoSearchableItem {


    public static final int FOLDER = 0;
    public static final int BOOKMARK = 1;

    private String name;
    private MoURL url;
    private MoDate date;
    private int type = BOOKMARK;
    private ArrayList<MoBookmark> subBookmarks = new ArrayList<>();
    private boolean isSelected;
    private boolean isSearched;
    private boolean isSavable = true;

    public MoBookmark(String url,String name){
        this.name = name;
        this.url = new MoURL(url);
        this.date = new MoDate();
    }

    public MoBookmark(String name,int type){
        this.name = name;
        this.type = type;
    }

    public MoBookmark(String d,Context c){
        this.load(d,c);
    }


    public String getName() {
        return name;
    }

    public MoBookmark setName(String name) {
        this.name = name;
        return this;
    }

    public int getType() {
        return type;
    }

    public MoBookmark setType(int type) {
        this.type = type;
        return this;
    }

    public ArrayList<MoBookmark> getSubBookmarks() {
        return subBookmarks;
    }

    public MoBookmark setSubBookmarks(ArrayList<MoBookmark> subBookmarks) {
        this.subBookmarks = subBookmarks;
        return this;
    }

    public String getUrl() {
        return url.getUrlString();
    }

    public MoBookmark setUrl(MoURL url) {
        this.url = url;
        return this;
    }

    public MoDate getDate() {
        return date;
    }

    public MoBookmark setDate(MoDate date) {
        this.date = date;
        return this;
    }



    public int subBookMarkSize(){
        return this.subBookmarks.size();
    }


    public void addBookmark(String url,String folder){
        this.subBookmarks.add(MoBookmarkManager.buildBookmark(url,folder));
    }

    public void addFolder(String title){
        this.subBookmarks.add(MoBookmarkManager.buildFolder(title));
    }





    public boolean isFolder(){
        return this.type == FOLDER;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoBookmark)) return false;
        MoBookmark that = (MoBookmark) o;
        return getUrl().equals(that.getUrl()) &&
                this.name.equals(that.name) && this.type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(),this.name,this.type);
    }

    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        url = new MoURL(c[0],context);
        date = new MoDate(c[1],context);
        name = c[2];
        type = Integer.parseInt(c[3]);
        loadSubBookmarks(context, c[4]);
    }

    private void loadSubBookmarks(Context context, String data) {
        String[] d = MoFile.loadable(data);
        if(MoFile.isValidData(d)){
            MoBookmarkManager.loadInto(context,d,subBookmarks);
        }
    }

    @Override
    public String getData() {
        return MoFile.getData(url,date,name,type,subBookmarks);
    }



    // selectable

    @Override
    public boolean onSelect() {
        this.isSelected = !this.isSelected;
        return this.isSelected;
    }

    @Override
    public void setSelected(boolean b) {
        this.isSelected = b;
    }

    @Override
    public boolean isSelected() {
        return this.isSelected;
    }

    @Override
    public Object getItem() {
        return this;
    }

    // searchable

    @Override
    public boolean updateSearchable(Object... objects) {
        // TODO implement this
        return false;
    }

    @Override
    public boolean isSearchable() {
        return this.isSearched;
    }

    @Override
    public void setSearchable(boolean b) {
        this.isSearched = b;
    }

    // savable

    @Override
    public boolean isSavable() {
        return this.isSavable;
    }

    @Override
    public void setSavable(boolean b) {
        this.isSavable = b;
    }
}
