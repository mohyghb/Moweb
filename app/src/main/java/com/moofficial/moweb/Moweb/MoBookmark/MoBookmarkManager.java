package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoConnections.MoShare;
import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

import static com.moofficial.moweb.Moweb.MoBookmark.MoBookmark.BOOKMARK;
import static com.moofficial.moweb.Moweb.MoBookmark.MoBookmark.FOLDER;

// TODO test the new version of the bookmark implementation
public class MoBookmarkManager {

    private static final String FILE_NAME = "bookmarkfile";
    private static final String MAIN_FOLDER_NAME = "Bookmarks";

    private static MoBookmark mainFolder = buildFolder(MAIN_FOLDER_NAME);
    private static HashMap<String,MoBookmark> mapOfBookmarks = new HashMap<>();
    private static HashMap<String,MoBookmark> mapOfFolders = new HashMap<>();

    public static MoBookmark getMainFolder(){
        return mainFolder;
    }

    /**
     * adds the url to bookmark
     * if we already don't have it saved
     * @param url
     */
    public static boolean add(Context context, String url,String title){
        return add(context, buildBookmark(processUrl(url),title));
    }

    /**
     * adds or removes the bookmark from the list
     * if we already have it
     * @param context
     * @param url
     * @param title
     */
    public static void addOrRemoveIfWasAddedAlready(Context context,String url,String title){
        if(!add(context,url,title)){
            // it did not add it to the bookmarks because we already had one with the same url
            remove(context,url);
        }
    }

    /**
     * removes the bookmark with this url
     * @param url
     */
    @SuppressWarnings("ConstantConditions")
    static void remove(Context context,String url){
        if(has(url)){
            mapOfBookmarks.get(url).setSavable(false);
            Toast.makeText(context, "Removed " + url, Toast.LENGTH_SHORT).show();
            save(context);
        }
    }


    /**
     *
     * @param c
     * @param title
     */
    public static void createFolder(Context c,String title,MoBookmark parent){
        MoBookmark folder = buildFolder(title);
        addToMap(folder);
        parent.add(folder);
        save(c);
    }

    /**
     * adds the bookmark if it does not exist or
     * it is a folder and returns true
     * or returns false
     * @param context
     * @param bm
     * @return
     */
    private static boolean add(Context context,MoBookmark bm){
        boolean addThisBookmark = false;
        switch (bm.getType()){
            case FOLDER:
                addThisBookmark = !hasFolder(bm.getName());
                break;
            case BOOKMARK:
                addThisBookmark = !has(bm.getUrl());
                break;
        }
        if(addThisBookmark){
            addToEveryField(bm);
            save(context);
            return true;
        }
        return false;
    }


    /**
     *
     * @param bm
     */
    private static void addToEveryField(MoBookmark bm){
        mainFolder.add(bm);
        addToMap(bm);
    }

    /**
     * saves the bookmarks inside the file
     * @param context
     */
    public static void save(Context context){
        MoReadWrite.saveFile(FILE_NAME, MoFile.getData(mainFolder),context);
    }

    /**
     * loads all the bookmarks if they
     * have not been already loaded
     * @param c
     */
    public static void load(Context c){
        if(mainFolder.isEmpty()){
            String[] d = MoFile.loadable(MoReadWrite.readFile(FILE_NAME,c));
            if(MoFile.isValidData(d)) {
                 mainFolder.load(d[0],c);
                //loadInto(c, MoFile.loadable(d[0]), mainFolder.getSubBookmarks());
            }
        }
    }

    /**
     * loads the data inside a list of bookmarks
     * @param c

     */
    static void loadInto(Context c,String[]bms,MoBookmark parent){
        for(String b:bms){
            try{
                MoBookmark bookmark = new MoBookmark(b,c);
                parent.add(bookmark);
                addToMap(bookmark);
            }catch (Exception ignore){}
        }
    }

    /**
     * adds the bookmark (if it is not a folder)
     * to the map of bookmarks
     * this is used to remove or find bookmarks
     * @param b
     */
    public static void addToMap(MoBookmark b){
        switch (b.getType()){
            case FOLDER:
                mapOfFolders.put(b.getName(),b);
                break;
            case BOOKMARK:
                mapOfBookmarks.put(b.getUrl(),b);
                break;
        }
    }

    /**
     * return true if the user already
     * has this url in their bookmarks
     * if we have the
     * @param url
     * @return
     */
    public static boolean has(String url){
        return mapOfBookmarks.containsKey(url) && Objects.requireNonNull(mapOfBookmarks.get(url)).isSavable();
    }

    /**
     * returns true if there was a folder with
     * this title
     * @param title
     * @return
     */
    public static boolean hasFolder(String title){
        return mapOfFolders.containsKey(title) && Objects.requireNonNull(mapOfFolders.get(title)).isSavable();
    }


    /**
     * makes the url standard
     * @param url
     * @return
     */
    private static String processUrl(String url){
        return url.trim();
    }

    /**
     *
     * @param url
     * @param title
     * @return
     */
    public static MoBookmark buildBookmark(String url, String title){
        return new MoBookmark(url,title);
    }

    /**
     *
     * @param title
     * @return
     */
    public static MoBookmark buildFolder(String title){
        return new MoBookmark(title.trim(), FOLDER);
    }


    /**
     * returns all the bookmarks + all the folders inside one array list
     * @return
     */
    public static ArrayList<MoBookmark> getEverything(){
        ArrayList<MoBookmark> all = new ArrayList<>();
        all.addAll(mapOfBookmarks.values());
        all.addAll(mapOfFolders.values());
        return all;
    }


    private static ArrayList<MoBookmark> getFolders(){
        return new ArrayList<>(mapOfFolders.values());
    }



    /**
     * if this folder is inside the sub-folders, or
     * the parent is this folder, we do not want to include them inside the
     * search. If you don't care about this, pass null,null
     * @param currentFolder that we are in (we can not include the parent or the sub folders
     *                      so we need to filter them out of the equation and we can not show itself)
     * @return all the folders that are applicable using those conditions
     */
    public static ArrayList<MoBookmark> getFolders(MoBookmark currentFolder){
        if(currentFolder == null){
            return getFolders();
        }
        // we include all the folders except:
        // 1- current folder
        // 2- parent folder
        // 3- any sub folder under the current folder
        HashSet<MoBookmark> folders = new HashSet<>(mapOfFolders.values());
        // removing the current folder from the set
        folders.remove(currentFolder);
        // removing the parent folder from set
        folders.remove(currentFolder.getParent());
        // recursively remove all the sub folders from the set
        currentFolder.removeAllTheSubFoldersRecursive(folders);
        return new ArrayList<>(folders);
    }

    /**
     * sorts the array of bookmark that is passed alphabetically
     * @param a array of bookmarks or folders
     */
    public static void sortAlphabetically(ArrayList<MoBookmark> a){
        Collections.sort(a,(bookmark, t1) -> bookmark.getName().compareTo(t1.getName()));
    }

    /**
     * removes the bookmark completely
     */
    private static void remove(HashMap<String,MoBookmark> map,String key){
        if(map.containsKey(key)){
            Objects.requireNonNull(map.get(key)).setSavable(false);
            map.remove(key);
        }
    }

    /**
     * removes the bookmark based on their type
     * @param bm
     */
    public static void remove(MoBookmark bm){
        switch (bm.getType()){
            case BOOKMARK:
                remove(mapOfBookmarks,bm.getUrl());
                break;
            case FOLDER:
                remove(mapOfFolders,bm.getName());
                break;
        }
    }

    public static void clear(Context c){
//        mapOfFolders.clear();
//        mapOfBookmarks.clear();
//        mainFolder.clear();
        save(c);
    }


    public static MoBookmark getBookmark(String url){
        return mapOfBookmarks.get(url);
    }

    public static MoBookmark getFolder(String name){
        return mapOfFolders.get(name);
    }


    /**
     * shares the bookmark
     * if this is a folder, we share all the bookmarks that are in that folder
     * (we do not iterate through all the folders inside that folder as well
     *  we just search the surface for any bookmark, and we get those url)
     * @param context
     * @param b
     */
    public static void shareBookmark(Context context,MoBookmark b){
        switch (b.getType()){
            case FOLDER:
                new MoShare().setText(MoBookmarkUtils.getCombinedSurfaceUrl(b)).shareText(context);
                break;
            case BOOKMARK:
                new MoShare().setText(b.getUrl()).shareText(context);
                break;
        }
    }



    // for editing

    /**
     * edits the url of the bookmark
     * we first remove it from the map in case the url has changed
     * then update the the new bookmark with our new values
     * @param b
     * @param originalKey
     */
    public static void edit(MoBookmark b,String originalKey){
        switch (b.getType()){
            case FOLDER:
                mapOfFolders.remove(originalKey);
                break;
            case BOOKMARK:
                mapOfBookmarks.remove(originalKey);
                break;
        }
        addToMap(b);
    }


    /**
     *
     * @param edit
     * @param originalKey
     * @param name
     * @param url
     * @param newFolderName
     */
    public static void editBookmark(MoBookmark edit,String originalKey,String name,String url,String newFolderName) {
        edit.setName(name);
        if(!edit.isFolder()){
            edit.setUrl(url);
        }
        MoBookmarkManager.moveToFolder(MoBookmarkManager.getFolder(newFolderName),
                edit);
        MoBookmarkManager.edit(edit,originalKey);
    }

    /**
     * moves all bookmarks in b to the folder
     * @param bs
     * @param folder
     */
    public static void moveToFolder(MoBookmark folder,MoBookmark ... bs){
        for(MoBookmark b: bs){
            moveTo(folder, b);
        }
    }

    /**
     * moves b to folder
     * @param folder
     * @param b
     */
    private static void moveTo(MoBookmark folder, MoBookmark b) {
        if(folder!=null && b!=null){
            removeFromParent(b);
            // then add this b to the new folder
            folder.add(b);
        }
    }

    /**
     * removes b's parent
     * @param b
     */
    private static void removeFromParent(MoBookmark b) {
        // we first remove this bookmark from its parent
        if(b.hasParent()){
            b.getParent().remove(b);
        }
    }


}
