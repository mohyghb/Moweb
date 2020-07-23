package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static com.moofficial.moweb.Moweb.MoBookmark.MoBookmark.FOLDER;

public class MoBookmarkManager {

    private static final String FILE_NAME = "bookmarkfile";

    private static ArrayList<MoBookmark> bookmarks = new ArrayList<>();
    private static HashMap<String,MoBookmark> mapOfBookmarks = new HashMap<>();

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
    private static void remove(Context context,String url){
        if(has(url)){
            mapOfBookmarks.get(url).setSavable(false);
            Toast.makeText(context, "Removed " + url, Toast.LENGTH_SHORT).show();
            save(context);
        }
    }

    /**
     * adds a folder with name [title] to
     * the bookmarks
     * @param context
     * @param title
     */
    public static void addFolder(Context context,String title){
        add(context, buildFolder(title));
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
        if(bm.isFolder() || !has(bm.getUrl())){
            addToEveryField(bm);
            save(context);
            Toast.makeText(context, "Added bookmark", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            return false;
        }
    }

    private static void addToEveryField(MoBookmark bm){
        bookmarks.add(bm);
        addToMap(bm);
    }

    /**
     * saves the bookmarks inside the file
     * @param context
     */
    public static void save(Context context){
        MoReadWrite.saveFile(FILE_NAME, MoFile.getData(bookmarks),context);
    }

    /**
     * loads all the bookmarks if they
     * have not been already loaded
     * @param c
     */
    public static void load(Context c){
        if(bookmarks.isEmpty()){
            String[] d = MoFile.loadable(MoReadWrite.readFile(FILE_NAME,c));
            if(MoFile.isValidData(d)) {
                loadInto(c, MoFile.loadable(d[0]),bookmarks);
            }
        }
    }

    /**
     * loads the data inside a list of bookmarks
     * @param c

     * @param list
     */
    static void loadInto(Context c,String[]bms,List<MoBookmark> list){
        for(String b:bms){
            try{
                MoBookmark bookmark = new MoBookmark(b,c);
                list.add(bookmark);
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
        if(!b.isFolder()){
            mapOfBookmarks.put(b.getUrl(),b);
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
        return new MoBookmark(title, FOLDER);
    }


    public static ArrayList<MoBookmark> getBookmarks(){
        return bookmarks;
    }


    public static void removeFromAllPlaces(MoBookmark bm){
        mapOfBookmarks.remove(bm.getUrl());
    }

//    /**
//     * removes the bookmark with url from the bookmark list
//     * @param url
//     * @param bookmarkList
//     * @return
//     */
//    static boolean remove(String url, List<MoBookmark> bookmarkList){
//        for(int i = 0;i < bookmarkList.size();i++){
//            MoBookmark bookmark = bookmarkList.get(i);
//            if(bookmark.getType() == FOLDER){
//                return bookmark.remove(url);
//            }else if(bookmark.getUrl().equals(url)){
//                bookmarkList.remove(i);
//                return true;
//            }
//        }
//        return false;
//    }

}
