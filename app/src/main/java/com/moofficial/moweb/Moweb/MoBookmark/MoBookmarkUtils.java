package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;

import com.moofficial.moweb.R;

public class MoBookmarkUtils {

    public static String getCombinedSurfaceUrl(MoBookmark b){
        StringBuilder sb = new StringBuilder();
        for(MoBookmark book : b.getSubBookmarks()){
            if(!book.isFolder()){
                sb.append(book.getUrl()).append("\n");
            }
        }
        return sb.toString().trim();
    }


    public static String getFolderName(Context c, MoBookmark b){
        return b.hasParent()?b.getParent().getName():c.getString(R.string.bookmark_title);
    }

}
