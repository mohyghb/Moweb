package com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileManager;
import com.moofficial.moessentials.MoEssentials.MoUtils.MoKeyboardUtils.MoKeyboardUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.MoUserPassAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoWebAutoFillSession;

import java.io.IOException;

// sends js inputs back to the app
// for example when the user uses a
// text field to enter things
public class MoJsInput {

    public static final String MO_JS = "MoJs";
    public static String AUTO_FILL_SCRIPT = "";
    public static String LISTENER_SCRIPT = "";
    public static final String NAME = "jsInput";

    /**
     * a boolean indicating that there is
     * a form on the current page
     * and the user tried to edit it
     * false initially because we do not know
     */
    private boolean thereIsAForm = false;
    private Context context;
    private MoWebAutoFillSession session;
    private WebView webView;


    public MoJsInput(WebView v) {
        this.webView = v;
        this.webView.addJavascriptInterface(this, MoJsInput.NAME);
        this.context = webView.getContext();
        this.session = new MoWebAutoFillSession(webView);
    }


    /**
     * notifies the class that there is
     * a form inside the current page and
     * the user has edit it
     */
    @JavascriptInterface
    public void activate() {
        this.thereIsAForm = true;
    }

    /**
     * gathers all the forms
     * data into our system
     *
     */
    public void gatherData() {
        webView.evaluateJavascript(AUTO_FILL_SCRIPT,null);
        print("gather data");
    }


    /**
     * we set our on click listeners
     * to the text inputs of the web site
     * and whenever the user clicks
     * on them, we provide auto-fill data
     */
    public void setupListeners() {
        webView.evaluateJavascript(LISTENER_SCRIPT,null);
    }

    /**
     * one by one the script will call this method
     * to pass in the id, value, and type of the
     * input that user typed into
     * and then we process them
     * @param id of the input
     * @param value of the input
     * @param type of the input
     * @param name used to find the input that we are looking for if
     *             the element does not have any ids
     */
    @JavascriptInterface
    public void onGather(String name,String id,String value,String type,String autoComplete) {
        // construction, there might be an error later on since
        //  we are either passing name or id and we don't know which one to use
        //  to find the view inside js
        session.add(id.isEmpty()?name:id,value,type,autoComplete);
        print(String.format("{name = %s, id = %s,value = %s, type == %s}",name,id,value,type));
    }

    /**
     * called from the js interface
     * when it is done calling on gathered
     */
    @JavascriptInterface
    public void onFinishedGathering() {
        session.processAndClearSession(this.context);
        print("on finish gather");
    }

    @JavascriptInterface
    public void print(String p) {
        Log.d(MO_JS,p);
    }


    /**
     * this is called every
     * time the user clicks on a input field
     * @param id of the element
     * @param name of the element
     * @param type of the element
     */
    @JavascriptInterface
    public void onClicked(String id, String name, String type,String autocomplete) {
        if(MoWebAutoFill.autoFillIsOff(autocomplete)) {
            // if auto complete is off we don't do anything here
            return;
        }

        if (MoWebAutoFill.isUserPassAutoFill(autocomplete)) {
            // this is a user password auto fill
            MoKeyboardUtils.hideSoftKeyboard(webView);
            MoUserPassAutoFill.showUserPassAutoFill(context,webView);
            // todo same thing as above but for passwords.
        } else if (MoWebAutoFill.isCreditCardAutoFill(autocomplete)){
            // todo credit card auto fill
        } else {
            // todo general auto fill
            // todo change the bottom layout to suggestion layout when typing for general
        }


    }



    /**
     * reads the js files inside the asset folder
     * and loads it inside string and save it
     * so we don't load it multiple times
     * @param c context
     */
    public static void loadScript(Context c) {
        try {
            autoFillScript(c);
            listenerScript(c);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void autoFillScript(Context c) throws IOException {
        if(!AUTO_FILL_SCRIPT.isEmpty())
            return;
        AUTO_FILL_SCRIPT = MoFileManager.readAssetFile(c,"TextFieldDetection.js");
    }

    public static void listenerScript(Context c) throws IOException {
        if(!LISTENER_SCRIPT.isEmpty())
            return;
        LISTENER_SCRIPT = MoFileManager.readAssetFile(c,"TextFieldListener.js");
    }

}
