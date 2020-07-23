package com.moofficial.moweb.MoDialog;

import android.app.AlertDialog;
import android.content.DialogInterface;

public interface MoOnDialogTextInputListener {

    void onPositiveButtonPressed(DialogInterface dialogInterface, String input);
    void onNegativeButtonPressed(DialogInterface dialogInterface,String input);

}
