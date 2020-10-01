package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoUI.MoPopupWindow.MoPopupWindow;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill.MoWebAutoFillManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill.MoWebAutoFills;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoSavePasswordView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// saves and organizes all the inputs
// of the web inside here
// and puts them inside respective
// web auto fill or web password save
// (two or more auto fills can be placed here)
public class MoWebAutoFillSession {


    /**
     * these are related auto fills that are all inside
     * one session, therefore, when we process them,
     * we can put them inside one list of auto fills
     */
    private final HashMap<String, MoWebAutoFill> autoFills = new HashMap<>();
    private final List<MoWebAutoFill> passwordFills = new ArrayList<>();
    private WebView webView;

    // creates an auto-fill session for web view
    public MoWebAutoFillSession(WebView webView) {
        this.webView = webView;
    }

    /**
     * add a auto fill value based
     * on the params passed
     * if id or value is empty or null do
     * not add them
     * @param id
     * @param value
     * @param type
     */
    @SuppressWarnings("ConstantConditions")
    public void add (@NonNull String id, @NonNull String value, String type) {
        if(id == null || id.isEmpty() || value == null || value.isEmpty())
            return;
        if(autoFills.containsKey(id)) {
            // then we already have it, update the reference
            autoFills.get(id).setValue(value).setFieldType(type);
        }else {
            MoWebAutoFill f = new MoWebAutoFill().setValue(value).setId(id).setFieldType(type);
            autoFills.put(id, f);
            if (f.isPassword()) {
                passwordFills.add(f);
            }
        }
    }

    /**
     * processes the information that is currently
     * stored inside the (if any exist)
     * and clears them from the global map
     * then we start a new session meaning that
     * new auto fills can be put inside here
     */
    public void processAndClearSession(Context c) {
        // don't do anything if
        // the map is empty
        if(autoFills.isEmpty())
            return;
        String url = webView.getUrl();
       // MoWebAutoFills f = new MoWebAutoFills().addAll(autoFills.values());
        // webView.post(()-> askUserToSave(c, f));

        // new session
        clearSession();
    }

    /**
     * we get permission to save their
     * passwords
     * @param c
     * @param f
     */
    private void askUserToSave(Context c, MoWebAutoFills f) {
        MoSavePasswordView savePasswordView = new MoSavePasswordView(c);
        MoPopupWindow popupWindow = new MoPopupWindow(c)
                .setViews(savePasswordView)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocusable(false)
                .setOutsideTouchable(false)
                .setOverlapAnchor(false)
                .setDuration(3000)
                .build();

        savePasswordView
                .setOnCloseClickListener((v)-> popupWindow.dismiss())
                .setOnSaveClickListener((v)-> {
                    // save password
                    try {
                        MoWebAutoFillManager.add(c,f);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // tell them it is saved
                    Toast.makeText(c,
                            "Password saved!", Toast.LENGTH_SHORT).show();
                    popupWindow.dismiss();
                });
        popupWindow.showOn(webView,0,0, Gravity.BOTTOM);
    }

    /**
     * clears the map of auto fills
     */
    public void clearSession() {
        autoFills.clear();
        passwordFills.clear();
    }

}
