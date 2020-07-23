package com.moofficial.moweb.MoDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.moofficial.moessentials.MoEssentials.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.MoDynamicUnit.MoDynamicUnit;
import com.moofficial.moweb.R;

public class MoDialogBuilder {


    public static final int MO_EDIT_TEXT_PADDING = MoDynamicUnit.convertDpToPixels(14f);
    public static final int MO_ICON_PADDING = MoDynamicUnit.convertDpToPixels(16f);


    public static void showUserInputTextLayoutDialog(Context context,String title,String message,
                                                String pButton,String nButton,
                                                MoOnDialogTextInputListener onDialogTextInputListener){
        View v = MoInflaterView.inflate(R.layout.mo_input_bar,context);
        applyPadding(v,MO_EDIT_TEXT_PADDING);
        EditText editText = v.findViewById(R.id.input_bar_edit_text);
        AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setView(v)
                .setPositiveButton(pButton, (dialogInterface, i) -> {
                    onDialogTextInputListener.onPositiveButtonPressed(dialogInterface, editText.getText().toString());
                })
                .setNegativeButton(nButton, (dialogInterface, i) -> {
                    onDialogTextInputListener.onNegativeButtonPressed(dialogInterface,editText.getText().toString());
                    dialogInterface.dismiss();
                })
                .create();
        alertDialog.show();
    }


    private static void applyPadding(View v,int p){
        v.setPadding(p,p,p,p);
    }



}
