package com.moofficial.moweb.Moweb.MoWebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moweb.MoBitmap.MoBitmapUtils;

import com.moofficial.moweb.MoTouchPosition.MoTouchPosition;
import com.moofficial.moweb.Moweb.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoStackTabHistory.MoStackTabHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnBitmapCapturedListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnReceivedError;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnUpdateUrlListener;

// a webview which has more helper functions
public class MoWebView implements MoSavable,MoLoadable {



    private Context context;
    private WebView wv;
    private MoWebClient client = new MoWebClient() {

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            onUpdateUrlListener.onUrlUpdate(url,isReload);
            stackTabHistory.update();
            if(saveHistory && !isReload){
                MoHistoryManager.add(url,view.getTitle(),context);
            }
            // save history
            MoWebView.this.url = url;
        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            // save a bitmap of the content
            captureBitmap();
            // inject the js to web view
            evaluateJavascript(MoWebElementDetection.injectJs(context),null);
        }
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            onErrorReceived.onReceivedError(view,request,error);
        }
    };
    private String url;
    private MoChromeClient chromeClient;
    //private MoTab tab;
    private MoHitTestResultParser hitTestResultParser;
    private MoTouchPosition touchPosition;
    private MoStackTabHistory stackTabHistory = new MoStackTabHistory();
    private MoOnUpdateUrlListener onUpdateUrlListener = (url, isReload) -> {};
    private MoOnBitmapCapturedListener onBitmapCapturedListener = (b) -> {};
    private MoOnReceivedError onErrorReceived = (view, request, error) -> {};
    private boolean captureBitmap = true;
    private boolean saveHistory = true;



    public MoWebView(Context c) {
        this.context = c;
    }

    public Context getContext() {
        return context;
    }

    public MoWebView setContext(Context context) {
        this.context = context;
        return this;
    }

    public MoWebClient getClient() {
        return client;
    }

    public MoWebView setClient(MoWebClient client) {
        this.client = client;
        return this;
    }

    public MoChromeClient getChromeClient() {
        return chromeClient;
    }

    public MoWebView setChromeClient(MoChromeClient chromeClient) {
        this.chromeClient = chromeClient;
        return this;
    }

    public MoHitTestResultParser getHitTestResultParser() {
        return hitTestResultParser;
    }

    public MoWebView setHitTestResultParser(MoHitTestResultParser hitTestResultParser) {
        this.hitTestResultParser = hitTestResultParser;
        return this;
    }

    public MoOnUpdateUrlListener getOnUpdateUrlListener() {
        return onUpdateUrlListener;
    }

    public MoWebView setOnUpdateUrlListener(MoOnUpdateUrlListener onUpdateUrlListener) {
        this.onUpdateUrlListener = onUpdateUrlListener;
        return this;
    }

    public MoOnBitmapCapturedListener getOnBitmapCapturedListener() {
        return onBitmapCapturedListener;
    }

    public MoWebView setOnBitmapCapturedListener(MoOnBitmapCapturedListener onBitmapCapturedListener) {
        this.onBitmapCapturedListener = onBitmapCapturedListener;
        return this;
    }

    public MoOnReceivedError getOnErrorReceived() {
        return onErrorReceived;
    }

    public MoWebView setOnErrorReceived(MoOnReceivedError onErrorReceived) {
        this.onErrorReceived = onErrorReceived;
        return this;
    }

    public MoTouchPosition getTouchPosition() {
        return touchPosition;
    }

    public MoWebView setTouchPosition(MoTouchPosition touchPosition) {
        this.touchPosition = touchPosition;
        return this;
    }

    public MoStackTabHistory getStackTabHistory() {
        return stackTabHistory;
    }

    public MoWebView setStackTabHistory(MoStackTabHistory stackTabHistory) {
        this.stackTabHistory = stackTabHistory;
        return this;
    }

    public boolean isCaptureBitmap() {
        return captureBitmap;
    }

    public MoWebView setCaptureBitmap(boolean captureBitmap) {
        this.captureBitmap = captureBitmap;
        return this;
    }

    public boolean isSaveHistory() {
        return saveHistory;
    }

    public MoWebView setSaveHistory(boolean saveHistory) {
        this.saveHistory = saveHistory;
        return this;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public void init() {
        initWebView();
        // for showing pop up menu when
        // user long presses over an element of web view
        initStackTabHistory();
        initHitTestResult();
    }


    private void initStackTabHistory(){
        this.stackTabHistory.setWebView(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {

        setWebViewClient(this.client);
        setWebChromeClient(this.chromeClient);
        WebSettings webSettings = getSettings();
        webSettings.setJavaScriptEnabled(true);
        // allowing pinch zoom
        webSettings.setBuiltInZoomControls(true);
        // disabling the zoom buttons
        webSettings.setDisplayZoomControls(false);
    }

    public void setWebViewClient(MoWebClient client) {
        wv.setWebViewClient(client);
    }

    public void setWebChromeClient(WebChromeClient client) {
        wv.setWebChromeClient(client);
    }

    public WebSettings getSettings(){
        return wv.getSettings();
    }

    private void initHitTestResult(){
        this.hitTestResultParser = new MoHitTestResultParser(this.context,this);
        // init touch position
        this.touchPosition = new MoTouchPosition(wv).setLongClickListener(()->{
            boolean dialogCreated = hitTestResultParser.createDialog();
            if(!dialogCreated){
                // only show the bottom search if dialog was not created
                Handler handler = new Handler();
                handler.postDelayed(() -> hitTestResultParser.onTextSelected(), 100);
            }
        });
    }

    public String getBaseUrl(){
        return MoUrlUtils.getBaseUrl(getUrl());
    }



    /**
     * captures a bitmap from webview
     */
    private void captureBitmap() {
        if(captureBitmap){
            postDelayed(() -> onBitmapCapturedListener.onBitmapCaptured(MoBitmapUtils.createBitmapFromView(wv,
                    0, 0)), 100);
        }
    }






    /**
     * finds all the texts that are applicable
     * to the search string and calls find listener
     * multiple times when it is updating
     * @param s
     * @param findListener
     */
    public void findAllAsync(String s, WebView.FindListener findListener){
        post(() -> {
            findAllAsync(s);
            setFindListener(findListener);
        });
    }

    public void post(Runnable r){
        wv.post(r);
    }

    public void postDelayed(Runnable r, long delay){
        wv.postDelayed(r,delay);
    }

    public String getUrl(){
        return this.url;
    }

    public String getTitle(){
        return wv.getTitle();
    }

    public void clearMatches(){
        wv.clearMatches();
    }

    public void findAllAsync(String s){
        wv.findAllAsync(s);
    }

    public void setFindListener(WebView.FindListener findListener){
        wv.setFindListener(findListener);
    }

    /**
     * reloads/refreshes the web page
     * (url that we are currently on)
     */
    public void reload(){
        wv.reload();
    }

    /**
     * moves to the next find item
     */
    public void findNext(){
        findNext(true);
    }

    /**
     * moves to the previous find item
     */
    public void findPrevious(){
        findNext(false);
    }

    public void findNext(boolean b){
        wv.findNext(b);
    }





    /**
     * if it can go forward from this url
     * @return
     */
    public boolean canGoForward(){
        return stackTabHistory.canGoForward();
    }

    /**
     * goes forward a url
     */
    public void goForward(){
        this.stackTabHistory.goForward();
    }

    /**
     * goes forwards if it can
     */
    public void goForwardIfYouCan(){
        if(canGoForward()){
            goForward();
        }
    }

    public boolean canGoBack(){
        return stackTabHistory.canGoBack();
    }

    public void goBack(){
        this.stackTabHistory.goBack();
    }


    public void evaluateJavascript(String js, ValueCallback<String> v){
        wv.evaluateJavascript(js,v);

    }


    @SuppressLint("JavascriptInterface")
    public void addJavascriptInterface(Object o, String name){
        wv.addJavascriptInterface(o,name);
    }


    public void loadUrl(String url){
        wv.loadUrl(url);
    }

    public WebView getWebView() {
        return wv;
    }

    public MoWebView setWebView(WebView wv) {
        this.wv = wv;
        return this;
    }

    @Override
    public void load(String s, Context context) {
        this.stackTabHistory.load(s,context);
    }

    @Override
    public String getData() {
        return this.stackTabHistory.getData();
    }
}