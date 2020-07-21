package com.moofficial.moweb.Moweb.MoWebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.view.DragEvent;
import android.view.View;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.MoBitmap.MoBitmapUtils;

import com.moofficial.moweb.MoTouchPosition.MoTouchPosition;
import com.moofficial.moweb.Moweb.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

public class MoWebView {

    //UI
    private WebView webView;
    private MoWebClient client;
    private MoChromeClient chromeClient;
    private MoTab tab;
    private MoHitTestResultParser hitTestResultParser;
    private Context context;
    private MoTouchPosition touchPosition;


    public MoChromeClient getChromeClient() {
        return chromeClient;
    }

    public void setChromeClient(MoChromeClient chromeClient) {
        this.chromeClient = chromeClient;
    }

    public MoWebView(MoTab tab,Context c) {
        this.tab = tab;
        this.context = c;
        this.client = new MoWebClient() {

            @Override
            protected void onUrlChanged(String u, boolean isReload) {
                tab.updateUrl(u);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // save a bitmap of the content
                captureBitmap();
                // inject the js to web view
                webView.evaluateJavascript(MoWebElementDetection.injectJs(context),null);
                // save history
                tab.addSearchToHistory(webView.getTitle());
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                tab.onErrorReceived(request,error);
            }


        };
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void init() {
        initWebView();
        // for showing pop up menu when
        // user long presses over an element of web view
        initHitTestResult();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        //appbar = tab.getView().findViewById(R.id.smart_tab_bar);
        webView = tab.getView().findViewById(R.id.tab_web_view);
        webView.setWebViewClient(this.client);
        webView.setWebChromeClient(this.chromeClient);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // allowing pinch zoom
        webSettings.setBuiltInZoomControls(true);
        // disabling the zoom buttons
        webSettings.setDisplayZoomControls(false);
    }


    private void initHitTestResult(){
        this.hitTestResultParser = new MoHitTestResultParser(this.context,this);
        // init touch position
        this.touchPosition = new MoTouchPosition(this.webView).setLongClickListener(()->{
            boolean dialogCreated = hitTestResultParser.createDialog();
            if(!dialogCreated){
                // only show the bottom search if dialog was not created
                Handler handler = new Handler();
                handler.postDelayed(() -> hitTestResultParser.onTextSelected(), 100);
            }
        });
    }

    public String getBaseUrl(){
        return this.tab.getBaseUrl();
    }

    public WebView getWebView() {
        return webView;
    }

    /**
     * captures a bitmap from webview
     */
    private void captureBitmap() {
        if(tab.isCaptureImage()){
            webView.postDelayed(() ->
                            tab.getMoBitmap().setBitmap(MoBitmapUtils.createBitmapFromView(webView, 0, 0)),
                    100);
        }
    }

    /**
     * sets the visibility of the web view to v
     * @param v
     */
    public void setVisibility(int v){
        this.webView.setVisibility(v);
    }


    /**
     * returns the tab that this web view belongs to
     * @return
     */
    public MoTab getTab() {
        return tab;
    }

    /**
     * loads the url into the web view
     * @param url of the page they are trying to go
     */
    public void loadUrl(String url){
        this.webView.loadUrl(url);
    }

    /**
     * finds all the texts that are applicable
     * to the search string and calls find listener
     * multiple times when it is updating
     * @param s
     * @param findListener
     */
    public void findAllAsync(String s, WebView.FindListener findListener){
        this.webView.post(() -> {
            webView.findAllAsync(s);
            this.webView.setFindListener(findListener);
        });
    }

    /**
     * moves to the next find item
     */
    public void findNext(){
        this.webView.findNext(true);
    }

    /**
     * moves to the previous find item
     */
    public void findPrevious(){
        this.webView.findNext(false);
    }


    /**
     * clears the matches from find
     */
    public void clearMatches(){
        this.webView.clearMatches();
    }


    /**
     * returns the title of the web view
     * @return
     */
    public String getTitle(){
        return this.webView.getTitle();
    }


    /**
     * if it can go forward from this url
     * @return
     */
    public boolean canGoForward(){
        return this.webView.canGoForward();
    }

    /**
     * goes forward a url
     */
    public void goForward(){
        this.webView.goForward();
    }

    /**
     * goes forwards if it can
     */
    public void goForwardIfYouCan(){
        if(canGoForward()){
            goForward();
        }
    }

}