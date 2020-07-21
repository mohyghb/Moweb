package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoReadWrite.MoReadWrite;

import java.util.ArrayList;

public class MoBookmarkManager {

    private static final String FILE_NAME = "bookmarkfile";

    private static ArrayList<MoBookmark> bookmarks = new ArrayList<>();

    /**
     * adds the url to bookmark
     * if we already don't have it saved
     * @param url
     */
    public static void add(Context context, String url,String title){
        add(context,makeBookmark(processUrl(url),title));
    }

    public static void addFolder(Context context,String title){
        add(context,makeFolder(title));
    }




    private static void add(Context context,MoBookmark bm){
        if(!bookmarks.contains(bm)){
            bookmarks.add(bm);
            save(context);
            Toast.makeText(context, "added bookmark", Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(context, "already have this bookmark", Toast.LENGTH_SHORT).show();
        }
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
            if(MoFile.isValidData(d)){
                String[] bms = MoFile.loadable(d[0]);
                for(String b:bms){
                    try{
                        bookmarks.add(new MoBookmark(b,c));
                    }catch (Exception ignore){}
                }
            }
        }
    }

    /**
     * return true if the user already
     * has this url in their bookmarks
     * @param url
     * @return
     */
    public static boolean has(String url){
        String processedUrl = processUrl(url);
        for(MoBookmark b: bookmarks){
            if(b.has(processedUrl)){
                return true;
            }
        }
        return false;
    }

    private static String processUrl(String url){
        return url.trim();
    }

    /**
     *
     * @param url
     * @param title
     * @return
     */
    public static MoBookmark makeBookmark(String url,String title){
        return new MoBookmark(url,title);
    }

    /**
     *
     * @param title
     * @return
     */
    public static MoBookmark makeFolder(String title){
        return new MoBookmark(title,MoBookmark.FOLDER);
    }


    public static ArrayList<MoBookmark> getBookmarks(){
        return bookmarks;
    }

}
