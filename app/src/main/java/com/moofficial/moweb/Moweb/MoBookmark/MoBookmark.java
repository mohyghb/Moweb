package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoDate.MoDate;
import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSwitchSavable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSearchable.MoSearchableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoViews.MoSelectable.MoSelectableItem;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;

import java.util.ArrayList;
import java.util.HashSet;
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
    private MoDate date = new MoDate();
    private int type = BOOKMARK;
    // mutual relationship
    private MoBookmark parent;
    // contains
    private ArrayList<MoBookmark> subs = new ArrayList<>();
    private ArrayList<MoBookmark> subFolders = new ArrayList<>();

    private boolean isSelected;
    private boolean isSearched = true;
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


    public MoBookmark setUrl(MoURL url) {
        this.url = url;
        return this;
    }

    public ArrayList<MoBookmark> getSubFolders() {
        return subFolders;
    }

    public MoBookmark setSubFolders(ArrayList<MoBookmark> subFolders) {
        this.subFolders = subFolders;
        return this;
    }

    public boolean containsSubFolder(MoBookmark b){
        return this.subFolders.contains(b);
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

    public ArrayList<MoBookmark> getSubs() {
        return subs;
    }

    public MoBookmark setSubs(ArrayList<MoBookmark> subs) {
        this.subs = subs;
        return this;
    }

    public String getUrl() {
        return url.getUrlString();
    }

    public MoBookmark setUrl(String url) {
        this.url = new MoURL(url);
        return this;
    }

    public MoDate getDate() {
        return date;
    }

    public MoBookmark setDate(MoDate date) {
        this.date = date;
        return this;
    }

    public MoBookmark getParent() {
        return parent;
    }

    public MoBookmark setParent(MoBookmark parent) {
        this.parent = parent;
        return this;
    }



    public boolean isSearched() {
        return isSearched;
    }

    public MoBookmark setSearched(boolean searched) {
        isSearched = searched;
        return this;
    }

    public int size(){
        return this.subs.size();
    }


    public void add(MoBookmark b){
        b.setParent(this);
        this.subs.add(b);
        addToSubFolder(b);
    }

    private void addToSubFolder(MoBookmark b) {
        if(b.isFolder()){
            subFolders.add(b);
        }
    }

    public void remove(MoBookmark b){
        b.setParent(null);
        subs.remove(b);
        removeFromSubFolder(b);
    }

    private void removeFromSubFolder(MoBookmark b) {
        if(b.isFolder()){
            subFolders.remove(b);
        }
    }


    public boolean isEmpty(){
        return subs.isEmpty();
    }




    public boolean hasParent(){ return this.parent!=null;}
    public boolean isFolder(){
        return this.type == FOLDER;
    }

    /**
     * removes all the sub folders of this folder
     * from this folder
     * @param set set of folders
     */
    public void removeAllTheSubFoldersRecursive(HashSet<MoBookmark> set){
        // base case
        if(subFolders.isEmpty() || !isFolder()){
            return;
        }
        for(MoBookmark folder:subFolders){
            folder.removeAllTheSubFoldersRecursive(set);
            set.remove(folder);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MoBookmark)) return false;
        MoBookmark that = (MoBookmark) o;
        if(that.type == this.type && this.name.equals(that.name)){
            if(type == BOOKMARK){
                return getUrl().equals(that.getUrl());
            }else{
                return true;
            }
        }else{
            return false;
        }

    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl(),this.name,this.type);
    }

    @Override
    public void load(String s, Context context) {
        try{
            String[] c = MoFile.loadable(s);
            url = new MoURL(c[0],context);
            date = new MoDate(c[1],context);
            name = c[2];
            type = Integer.parseInt(c[3]);
            loadSubBookmarks(context, c[4]);
        }catch(Exception ignore){}

    }

    private void loadSubBookmarks(Context context, String data) {
        String[] d = MoFile.loadable(data);
        if(MoFile.isValidData(d)){
            MoBookmarkManager.loadInto(context,d,this);
        }
    }

    @Override
    public String getData() {
        return MoFile.getData(url,date,name,type, subs);
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
        this.isSearched = MoSearchableUtils.isSearchable(true,objects,this.name,this.url.getUrlString());
        return isSearchable();
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
