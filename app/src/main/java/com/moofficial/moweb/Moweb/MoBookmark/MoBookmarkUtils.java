package com.moofficial.moweb.Moweb.MoBookmark;

import android.content.Context;

import com.moofficial.moweb.R;

import java.util.Arrays;

public class MoBookmarkUtils {

    /**
     * combines all the surface url so it
     * can be shared to others
     * @param b
     * @return
     */
    public static String shareBookmarkText(boolean includeTitle,MoBookmark ... b){
        return shareBookmarkText(Arrays.asList(b),includeTitle);
    }

    public static String shareBookmarkText(Iterable<MoBookmark> b, boolean includeTitle){
        StringBuilder sb = new StringBuilder();
        for(MoBookmark book : b){
            book.addSubBookmarkUrlRecursive(sb,includeTitle);
        }
        return sb.toString().trim();
    }


    /**
     * returns the folder name of the book mark
     * @param c
     * @param b
     * @return
     */
    public static String getFolderName(Context c, MoBookmark b){
        return b.hasParent()?b.getParent().getName():c.getString(R.string.bookmark_title);
    }

    /**
     * encodes an array of bookmarks into
     * an array of string
     * for each bookmark we get
     * [type,key]
     * @param bookmarks
     * @return
     */
    public static String[] encodeBookmarks(MoBookmark ... bookmarks){
        if(bookmarks == null)
            return new String[]{};

        String[] encoded = new String[bookmarks.length*2];
        int i = 0;
        for(MoBookmark b: bookmarks){
            encoded[i] = b.getType()+"";
            encoded[i+1] = b.getKey();
            i+=2;
        }
        return encoded;
    }

    /**
     * decodes an array of [type,string] into
     * a array of book marks
     * @param encoded
     * @return
     */
    public static MoBookmark[] decodeBookmarks(String[] encoded){
        if(encoded == null)
            return new MoBookmark[]{};

        MoBookmark[] decoded = new MoBookmark[encoded.length/2];
        int index = 0;
        for(int i =0 ; i < encoded.length; i+=2){
            decoded[index] = MoBookmarkManager.get(Integer.parseInt(encoded[i]),encoded[i+1]);
            index++;
        }
        return decoded;
    }

}
