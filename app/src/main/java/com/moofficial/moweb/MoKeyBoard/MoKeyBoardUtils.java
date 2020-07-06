package com.moofficial.moweb.MoKeyBoard;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class MoKeyBoardUtils {

    public static void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }
}
