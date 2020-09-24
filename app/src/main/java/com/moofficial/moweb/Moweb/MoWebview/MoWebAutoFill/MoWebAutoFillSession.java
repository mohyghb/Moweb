package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.annotation.NonNull;

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
    private final HashMap<String,MoWebAutoFill> autoFills = new HashMap<>();
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
            autoFills.get(id).setValue(value).setType(type);
        }else {
            MoWebAutoFill f = new MoWebAutoFill().setValue(value).setId(id).setType(type);
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
        //try {
            MoWebAutoFills f = new MoWebAutoFills(url).addAll(autoFills.values());
            // monote ask the user if they want to save this data

            webView.post(()->{
                TextView[] passwords = new TextView[passwordFills.size()];
                int i = 0;
                for (MoWebAutoFill l: passwordFills) {
                    passwords[i] = new TextView(c);
                    passwords[i].setText("Do you want to save this password? " + l.getValue());
                    i++;
                }


//                MoSavePasswordView savePasswordView = new MoSavePasswordView(c);
//
//
//                new MoPopupWindow(c)
//                        .setViews(savePasswordView)
//                        .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
//                        .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
//                        .setFocusable(true)
//                        .setOutsideTouchable(true)
//                        .setOverlapAnchor(false)
//                        .build()
//                        .show(webView);
            });




//            MoWebAutoFillManager.add(c,f);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        // new session
        clearSession();
    }

    /**
     * clears the map of auto fills
     */
    public void clearSession() {
        autoFills.clear();
        passwordFills.clear();
    }

}
