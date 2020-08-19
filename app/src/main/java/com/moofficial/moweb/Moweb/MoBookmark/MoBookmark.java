package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoDate.MoDate;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSwitchSavable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.Interfaces.MoTabOpenable;
import com.moofficial.moweb.Moweb.MoUrl.MoURL;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;

/**
 * a class which can be both a bookmark and a folder
 * based on the type that is given to it
 */
public class MoBookmark implements MoSwitchSavable, MoLoadable, MoSelectableItem, MoSearchableItem, MoTabOpenable {


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
    private ArrayList<MoBookmark> subBookmarks = new ArrayList<>();

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
        if(this.url == null){
            return "";
        }
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
        addToRespectiveList(b);
    }

    private void addToRespectiveList(MoBookmark b) {
        switch (b.getType()){
            case FOLDER:
                subFolders.add(b);
                break;
            case BOOKMARK:
                subBookmarks.add(b);
                break;
        }
    }

    public void remove(MoBookmark b){
        b.setParent(null);
        subs.remove(b);
        removeFromRespectiveList(b);
    }

    private void removeFromRespectiveList(MoBookmark b) {
        switch (b.getType()){
            case FOLDER:
                subFolders.remove(b);
                break;
            case BOOKMARK:
                subBookmarks.remove(b);
                break;
        }
    }

    /**
     * returns the key used inside the maps
     * to identify different bookmark types
     * @return
     */
    public String getKey(){
        if(this.isFolder()){
            return name;
        }else{
            return getUrl();
        }
    }

    public boolean isEmpty(){
        return subs.isEmpty();
    }
    public boolean hasParent(){ return this.parent!=null;}
    public boolean isFolder(){
        return this.type == FOLDER;
    }
    public boolean isBookmark(){return this.type==BOOKMARK;}


    /**
     * recursively adds all the bookmarks
     * that are available under the subs of this bookmark to
     * the string builder. if include title is true, we also include the title
     * inside the string builder
     * @param sb string builder, keeping all the information inside one object
     * @param includeTitle whether or not we should include the title of bookmark
     */
    public void addSubBookmarkUrlRecursive(StringBuilder sb,boolean includeTitle){
        if(subs.isEmpty()){
            return;
        }
        for(MoBookmark b: subs){
            switch (b.getType()){
                case FOLDER:
                    b.addSubBookmarkUrlRecursive(sb,includeTitle);
                    break;
                case BOOKMARK:
                    if(includeTitle){
                        sb.append(b.getUrl()).append("\n");
                    }
                    sb.append(b.getUrl()).append("\n");
                    break;
            }
        }
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


    // searchable

    @Override
    public boolean updateSearchable(Object... objects) {
        switch (type){
            case FOLDER:
                this.isSearched = MoSearchableUtils.isSearchable(true,objects,this.name);
                break;
            case BOOKMARK:
                this.isSearched = MoSearchableUtils.isSearchable(true,objects,this.name,this.url.getUrlString());
                break;
        }

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

    @Override
    public String getSearchString() {
        return getUrl();
    }
}
