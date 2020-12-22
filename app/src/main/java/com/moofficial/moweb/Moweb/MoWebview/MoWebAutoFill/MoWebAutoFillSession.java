package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill.MoGeneralAutoFillManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill.MoLinkedAutoFills;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoSavePasswordView;

import java.io.IOException;
import java.util.HashMap;

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
    public void add (@NonNull String id, @NonNull String value, String type, String autoComplete) {
        if(id == null || id.isEmpty() || value == null || value.isEmpty())
            return;
        if (autoFills.containsKey(id)) {
            // then we already have it, update the reference
            autoFills.get(id)
                    .setValue(value)
                    .setFieldType(type)
                    .setAutoCompleteType(autoComplete);
        } else {
            MoWebAutoFill f = new MoWebAutoFill()
                    .setValue(value)
                    .setId(id)
                    .setFieldType(type)
                    .setAutoCompleteType(autoComplete);
            autoFills.put(id, f);
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
        // it is okay to save general auto-fills without confirmation
        saveGeneralAutoFill(c);
        // we need permission for user pass auto fill
        if (MoUserPassManager.enabled) {
            saveUserPassAutoFill(c,url);
        }
        // new session
        clearSession();
    }

    /**
     * ask the user if they want to save it or not
     */
    private void saveUserPassAutoFill(Context c,String url) {
        String host = MoUrlUtils.getHost(url);
        if(MoUserPassManager.neverSave(host)) {
            MoLog.print("avoided saving user name password for " + host);
            return;
        }
        MoUserPassAutoFill userPassAutoFill = extractUserPassword(host);
        if (userPassAutoFill.isValid()) {
            // we only ask the user if the combination is valid
            if (MoUserPassManager.has(userPassAutoFill)) {
                MoLog.print("we already saved pass for this account");
                return;
            }
            MoSavePasswordView.askUserToSave(c, webView,
                    () -> saveUsernamePassword(c, userPassAutoFill),
                    () -> neverSaveUserPassForThisHost(c, userPassAutoFill));
        }
    }

    /**
     * save password and username
     * @param c context needed for saving
     * @param userPassAutoFill instance to save
     */
    private void saveUsernamePassword(Context c, MoUserPassAutoFill userPassAutoFill) {
        try {
            MoUserPassManager.add(c, userPassAutoFill);
            Toast.makeText(c,"Password saved!",Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(c,"Error in saving password",Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * they don't want to get this ever again on this site
     * @param c context for saving
     * @param userPassAutoFill to get the host from
     */
    private void neverSaveUserPassForThisHost(Context c, MoUserPassAutoFill userPassAutoFill) {
        MoUserPassManager.neverSave(c,userPassAutoFill.getHost());
    }

    /**
     * save general auto-fills like name, last name
     * automatically, todo currently the credit card is
     *                   considered to be general THAT'S NOT GOOD change it
     * @param c
     */
    private void saveGeneralAutoFill(Context c) {
        MoLinkedAutoFills linkedAutoFills = extractGeneralAutoFill();
        try {
            MoGeneralAutoFillManager.add(c,linkedAutoFills);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * we can only extract one user pass per
     * page of the web view
     */
    private MoUserPassAutoFill extractUserPassword(String host) {
        MoUserPassAutoFill passAutoFill = new MoUserPassAutoFill();
        for (MoWebAutoFill autoFill: autoFills.values()) {
            passAutoFill.add(autoFill);
        }
        passAutoFill.chooseTopSuggestionIfNoUsername();
        passAutoFill.setHost(host);
        return passAutoFill;
    }

    /**
     * extract general auto-fills and create a link
     * todo, when you add a general auto-fill, then you should remove it
     *  since it is definitely not used inside pass word extraction
     * between them
     * @return linked auto fills
     */
    private MoLinkedAutoFills extractGeneralAutoFill() {
        MoLinkedAutoFills linkedAutoFills = new MoLinkedAutoFills();
        for (MoWebAutoFill autoFill : autoFills.values()) {
            if (autoFill.isNotUserPass() && autoFill.isNotOff()) {
                // it is general auto fill and auto-fill is not off
                linkedAutoFills.add(autoFill);
            }
        }
        return linkedAutoFills;
    }



    /**
     * clears the map of auto fills
     */
    public void clearSession() {
        autoFills.clear();
    }

}
