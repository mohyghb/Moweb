package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShare;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoEditText.MoEditText;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
    public static boolean add(Context context, String url,String title) {
        return add(context, buildBookmark(processUrl(url),title));
    }

    /**
     * adds or removes the bookmark from the list
     * if we already have it
     * @param context of the app
     * @param url of the bookmark
     * @param title of the bookmark
     * @return true if the url was added
     * or false if it was removed from the database (that means
     * that previously the url was inside the databse so we removed
     * it)
     */
    public static boolean addOrRemoveIfWasAddedAlready(Context context,String url,String title){
        if(!add(context,url,title)){
            // it did not add it to the bookmarks because we already had one with the same url
            removeAndSave(context,mapOfBookmarks.get(url));
            return false;
        }
        return true;
    }

    /**
     * removes the bookmark from the list
     * and saves it for future references
     * @param c context
     * @param b bookmark to be removed
     */
    public static void removeAndSave(Context c,MoBookmark b){
        remove(b);
        save(c);
    }



    /**
     * creates a folder with title 'title' and runs onAddDone
     * or returns and displays a message to user
     * @param c
     * @param title
     */
    public static void createFolder(Context c,String title,MoBookmark parent,MoEditText editText,Runnable onAddDone){
        if (!canCreateThisFolder(c, title,editText)) return;
        MoBookmark folder = buildFolder(title);
        addToMap(folder);
        parent.add(folder);
        save(c);
        onAddDone.run();
    }

    /**
     * returns true if we can create a folder named
     * title
     * @param c context of the place
     * @param title of the new folder
     * @return true if this folder can be created
     */
    private static boolean canCreateThisFolder(Context c, String title,MoEditText editText) {
        if(title.isEmpty()){
            editText.setError("Name can not be empty");
            return false;
        }else if(MoBookmarkManager.hasFolder(title)){
            editText.setError("Folder " +title + " already exits");
            return false;
        }else{
            editText.removeError();
        }
        return true;
    }


    public static boolean validateEditInputs(Context a, MoBookmark b,
                                             MoEditText name, MoEditText url, String originalKey){
        String nameText = processName(name.getInputText());
        int errors = 0;
        switch (b.getType()){
            case FOLDER:
                if(nameText.isEmpty()){
                    name.setError(a.getString(R.string.error_bookmark_empty_name));
                    errors++;
                }else if(!nameText.equals(originalKey) && hasFolder(nameText)){
                    name.setError(a.getString(R.string.error_bookmark_folder_already_exist));
                    errors++;
                }else{
                    name.removeError();
                }
                break;
            case BOOKMARK:
                String urlText = processUrl(url.getInputText());
                if(nameText.isEmpty()){
                    // it can not be empty
                    name.setError(a.getString(R.string.error_bookmark_empty_name));
                    errors++;
                }else{
                    name.removeError();
                }
                if(urlText.isEmpty()){
                    url.setError(a.getString(R.string.error_bookmark_empty_url));
                    errors++;
                }else if(!originalKey.equals(urlText) && has(urlText)){
                    // the url is not for this bookmark
                    // and we have it somewhere else inside our
                    // database, therefore, they can not change it
                    // to this url
                    url.setError(a.getString(R.string.error_bookmark_url_already_exist));
                    errors++;
                }else{
                    url.removeError();
                }
                break;
        }
        return errors==0;
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
        if(mainFolder.isEmpty()) {
            String[] d = MoFile.loadable(MoReadWrite.readFile(FILE_NAME,c));
            if(MoFile.isValidData(d)) {
                mainFolder.load(d[0], c);
            }
            addToMap(mainFolder);
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
    public static void addToMap(MoBookmark b) {
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
     * removes the bookmark from their
     * respective map
     * @param b bookmark to be removed from map
     */
    public static void removeFromMap(MoBookmark b) {
        switch (b.getType()){
            case FOLDER:
                mapOfFolders.remove(b.getName());
                break;
            case BOOKMARK:
                mapOfBookmarks.remove(b.getUrl());
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
        return mapOfBookmarks.containsKey(url);
    }

    /**
     * returns true if there was a folder with
     * this title
     * @param title
     * @return
     */
    public static boolean hasFolder(String title){
        return mapOfFolders.containsKey(title);
    }


    /**
     * makes the url standard
     * @param url
     * @return
     */
    private static String processUrl(String url){
        return MoUrlUtils.removeUrlUniqueness(url);
    }

    /**
     * makes the name standard
     * @param name
     * @return
     */
    private static String processName(String name){
        return name.trim();
    }

    /**
     *
     * @param url
     * @param title
     * @return
     */
    public static MoBookmark buildBookmark(String url, String title){
        return new MoBookmark(processUrl(url),title);
    }

    /**
     *
     * @param title
     * @return
     */
    public static MoBookmark buildFolder(String title){
        return new MoBookmark(processName(title), FOLDER);
    }


    /**
     * returns all the bookmarks + all the folders inside one array list
     * @return
     */
    public static ArrayList<MoBookmark> getEverything(){
        ArrayList<MoBookmark> all = new ArrayList<>(mapOfBookmarks.values());
        // making sure that mainFolder is not inside everything
        // since main folder can not be deleted or edited
        remove(mainFolder);
        all.addAll(mapOfFolders.values());
        addToMap(mainFolder);
        // then adding it again to the map of folders
        return all;
    }


    private static ArrayList<MoBookmark> getFolders(){
        return new ArrayList<>(mapOfFolders.values());
    }



    /**
     * if this folder is inside the sub-folders, or
     * the parent is this folder, we do not want to include them inside the
     * search. If you don't care about this, pass null
     * @param bookmarks that we are trying to move (we can not include the parent or the sub folders
     *                      so we need to filter them out of the equation and we can not show itself)
     * @return all the folders that are applicable using those conditions
     */
    public static ArrayList<MoBookmark> getFolders(MoBookmark ... bookmarks){
        if(bookmarks == null || bookmarks.length == 0){
            return getFolders();
        }
        // we include all the folders except:
        // 1- current folder
        // 2- parent folder
        // 3- any sub folder under the current folder
        HashSet<MoBookmark> folders = new HashSet<>(mapOfFolders.values());
        for(MoBookmark b: bookmarks){
            // removing the current folder from the set
            if(b.isFolder()){
                folders.remove(b);
            }
            // removing the parent folder from set
            folders.remove(b.getParent());
            // recursively remove all the sub folders from the set
            b.removeAllTheSubFoldersRecursive(folders);
        }
        return new ArrayList<>(folders);
    }

    /**
     * sorts the array of bookmark that is passed alphabetically
     * @param a array of bookmarks or folders
     */
    public static void sortAlphabetically(ArrayList<MoBookmark> a){
        Collections.sort(a,(bookmark, t1) -> bookmark.getName().compareTo(t1.getName()));
    }

//    /**
//     * removes the bookmark completely
//     */
//    private static void remove(HashMap<String,MoBookmark> map,String key){
//        if(map.containsKey(key)){
//            Objects.requireNonNull(map.get(key)).setSavable(false);
//            map.remove(key);
//        }
//    }

//    /**
//     * removes the bookmark based on their type
//     * @param bm
//     */
//    public static void remove(MoBookmark bm){
//        switch (bm.getType()){
//            case BOOKMARK:
//                remove(mapOfBookmarks,bm.getUrl());
//                break;
//            case FOLDER:
//                remove(mapOfFolders,bm.getName());
//                break;
//        }
//    }

//    public static void clear(Context c) {
//        mapOfFolders.clear();
//        mapOfBookmarks.clear();
//        mainFolder.clear();
//        save(c);
//    }


    public static MoBookmark getBookmark(String url){
        return mapOfBookmarks.get(url);
    }

    public static MoBookmark getFolder(String name){
        return mapOfFolders.get(name);
    }


    public static MoBookmark get(int type,String key){
        switch (type){
            case FOLDER:
                return getFolder(key);
            case BOOKMARK:
                return getBookmark(key);
        }
        return null;
    }

    /**
     * shares the bookmark
     * if this is a folder, we share all the bookmarks that are in that folder
     * (we do not iterate through all the folders inside that folder as well
     *  we just search the surface for any bookmark, and we get those url)
     * @param context
     * @param b
     */
    public static void shareBookmark(Context context,boolean includeTitle,MoBookmark ... b){
        new MoShare().setText(MoBookmarkUtils.shareBookmarkText(includeTitle,b)).shareText(context);
    }

    public static void shareBookmark(Context context,Iterable<MoBookmark> b,boolean includeTitle){
        new MoShare().setText(MoBookmarkUtils.shareBookmarkText(b,includeTitle)).shareText(context);
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
     * edits and saves the changes to the bookmark
     * @param edit bookmark to be edited
     * @param originalKey the original key used to find the bookmark inside the maps
     * @param name new name of the bookmark
     * @param url new url of the bookmark
     * @param newFolderName new folder name of the bookmark
     */
    public static void editBookmarkAndSave(Context context,MoBookmark edit,String originalKey,
                                           String name,String url,String newFolderName) {
        editBookmark(edit, originalKey, name, url, newFolderName);
        save(context);
    }

    /**
     *
     * edits the bookmark
     * @param edit bookmark to be edited
     * @param originalKey the original key used to find the bookmark inside the maps
     * @param name new name of the bookmark
     * @param url new url of the bookmark
     * @param newFolderName new folder name of the bookmark
     */
    public static void editBookmark(MoBookmark edit,String originalKey,String name,String url,String newFolderName) {
        edit.setName(name);
        if(!edit.isFolder()){
            edit.setUrl(url);
        }
        MoBookmarkManager.moveTo(MoBookmarkManager.getFolder(newFolderName), edit);
        MoBookmarkManager.edit(edit,originalKey);
    }


    /**
     * moves all bookmarks in b to the folder
     * @param bs
     * @param folder
     */
    public static void moveToFolder(MoBookmark folder,Iterable<MoBookmark>  bs){
        for(MoBookmark b: bs){
            moveTo(folder, b);
        }
    }
    /**
     * moves all bookmarks in b to the folder
     * and saves the changes
     * @param bs
     * @param folder
     */
    public static void moveToFolderAndSave(Context c,MoBookmark folder,Iterable<MoBookmark>  bs){
        for(MoBookmark b: bs){
            moveTo(folder, b);
        }
        save(c);
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

    /**
     * monote consider a case where user deletes these selected
     *  bookmarks, and the tab they are on contains one of these(therefore the icons inside tab
     *  mode are not updated)
     * deletes all the selected items inside the list
     * @param c context
     * @param list items that are selected
     */
    public static void deleteSelectedBookmarks(Context c, List<MoBookmark> list){
        remove(list);
        save(c);
    }

    /**
     * removes all the bookmarks inside the list
     * also recursively removes all the sub bookmarks
     * or folders inside it
     * @param list of bookmarks to be deleted
     */
    private static void remove(List<MoBookmark> list) {
        if (list == null) {
            return;
        }

        for (MoBookmark b: list) {
            if (b.isFolder()) {
                remove(b.getSubFolders());
                remove(b.getSubs());
            }
            remove(b);
        }
    }

    private static void remove(MoBookmark b) {
        // removes from main folder
        b.removeFromParent();
        // removes from maps
        removeFromMap(b);
    }


}
