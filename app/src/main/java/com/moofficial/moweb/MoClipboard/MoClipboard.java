package com.moofficial.moweb.MoClipboard;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.widget.Toast;

public class MoClipboard {

    private static final String LABEL = "moclipboard";

    public static final String WAS_COPIED = " was copied to your clipboard";

    public static void addClipBoardText(ClipboardManager clipboardManager,String text){
        ClipData clip = ClipData.newPlainText(LABEL, text);
        clipboardManager.setPrimaryClip(clip);
    }


    public static void addClipBoardText(ClipboardManager clipboardManager, String text,
                                             Context c, String toastMessage){
        ClipData clip = ClipData.newPlainText(LABEL, text);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(c,toastMessage + WAS_COPIED,Toast.LENGTH_SHORT).show();
    }

}
