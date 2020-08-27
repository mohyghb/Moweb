package com.moofficial.moweb.MoClipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class MoClipboardUtils {

    private static final String LABEL = "moclipboard";

    public static final String WAS_COPIED = " was copied to your clipboard";


    private static void addClipBoardText(ClipboardManager clipboardManager, String text,
                                             Context c, String toastMessage){
        ClipData clip = ClipData.newPlainText(LABEL, text);
        clipboardManager.setPrimaryClip(clip);
        if(toastMessage!=null){
            Toast.makeText(c,toastMessage + WAS_COPIED,Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * adds the text to the clip board
     * of the phone
     * @param c context of the app
     * @param text text to be added to the clipboard
     * @param toastMessage toast message to be shown to let
     *                     the user know that the action was successful,
     *                     pass null for no toast message
     */
    @SuppressWarnings("ConstantConditions")
    public static void add(Context c, String text,String toastMessage){
        addClipBoardText((ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE),text,c,toastMessage);
    }

    /**
     * adds the text to the clip board
     * of the phone
     * @param c context of the app
     * @param text text to be added to the clipboard
     */
    @SuppressWarnings("ConstantConditions")
    public static void add(Context c, String text){
        addClipBoardText((ClipboardManager) c.getSystemService(Context.CLIPBOARD_SERVICE),text,c,null);
    }

}
