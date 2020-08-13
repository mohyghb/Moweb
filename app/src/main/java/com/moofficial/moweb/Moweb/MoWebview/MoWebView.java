package com.moofficial.moweb.Moweb.MoWebview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.MoTouchPosition.MoTouchPosition;
import com.moofficial.moweb.Moweb.MoBitmap.MoBitmap;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoStackTabHistory.MoStackTabHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnReceivedError;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnUpdateUrlListener;

// a web view which has more helper functions
public class MoWebView extends WebView implements MoSavable,MoLoadable {



    private Context context;
   // private WebView wv;
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
            MoLog.print("on page finished "+view.getProgress()+"" + url);
            captureBitmapWithDelay(1200);
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
    private MoOnReceivedError onErrorReceived = (view, request, error) -> {};
    private MoBitmap moBitmap;
    private boolean captureBitmap = true;
    private boolean saveHistory = true;
    private boolean isInDesktopMode = false;
    private boolean isPaused = true;

    public MoWebView(Context context) {
        super(context);
        this.context = context;
    }

    public MoWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public MoWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public MoWebView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
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

    public MoBitmap getMoBitmap() {
        return moBitmap;
    }

    public MoWebView setMoBitmap(MoBitmap moBitmap) {
        this.moBitmap = moBitmap;
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
        enableCorrectMode();
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
        // if we have it inside cache, load it from there, else use network
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }


    private void initHitTestResult(){
        this.hitTestResultParser = new MoHitTestResultParser(this.context,this);
        // init touch position
        this.touchPosition = new MoTouchPosition(this).setLongClickListener(()->{
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
    public void captureBitmap() {
        if(captureBitmap){
            moBitmap.captureBitmap(this);
        }
    }

    public void captureBitmapWithDelay(long delay){
        if(captureBitmap){
            moBitmap.captureBitmapWithDelay(this,delay);
        }
    }

    public void captureBitmapIfNotLoading(){
        if(captureBitmap){
            moBitmap.captureBitmapIfNotLoading(this);
        }
    }

    public void forceCaptureBitmapIfNotLoading(){
        if(captureBitmap){
            moBitmap.forceCaptureBitmapIfNotLoading(this);
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

    /**
     * whether or not this web view can go back a
     * URL or not
     * @return
     */
    public boolean canGoBack(){
        return stackTabHistory.canGoBack();
    }

    /**
     * goes back a page
     * can throw EmptyStackException if the stack
     * tab history is empty
     */
    public void goBack(){
        this.stackTabHistory.goBack();
    }


    /**
     * reloads the web view even if the
     * cache mode is turned on
     * if the url is not null and not empty
     */
    public void forceReloadFromNetwork(){
        if(this.url!=null && !this.url.isEmpty()){
            this.loadUrl(MoWebUtils.makeUrlUnique(this.url));
        }
    }


    /**
     * enables the desktop mode for this web view
     */
    public void enableDesktopMode(){
        MoWebUtils.setDesktopMode(this,true);
        this.isInDesktopMode = true;
    }

    /**
     * disables the desktop mode for this web view
     */
    public void disableDesktopMode(){
        MoWebUtils.setDesktopMode(this,false);
        this.isInDesktopMode = false;
    }

    /**
     * if isInDesktop mode, then we enable the desktop mode
     * otherwise we disable the dektop mode
     */
    public void enableCorrectMode(){
        if(this.isInDesktopMode){
            this.enableDesktopMode();
        }else{
            this.disableDesktopMode();
        }
    }

    /**
     * reverse enables the correct mode
     * if it is in desktop mode, it activates the
     * phone mode and vice versa
     */
    public void enableReverseMode(){
        this.isInDesktopMode = !this.isInDesktopMode;
        enableCorrectMode();
    }

    /**
     * destroys the web view of this class
     */
    public void destroyWebView(){
        destroy();
    }


    public boolean isInDesktopMode(){
        return this.isInDesktopMode;
    }
    public boolean isLoading(){
        return this.getProgress()!=100;
    }

    /**
     * moves the web view to the view group that is passed
     * @param viewGroup
     */
    public void moveWebViewTo(ViewGroup viewGroup,int width, int height){
        if(this.getParent()!=null){
            // it has a parent, remove the web view from the parent first
            ((ViewGroup)getParent()).removeView(this);
        }
        viewGroup.addView(this,new ViewGroup.LayoutParams(width,height));
    }

    public void moveWebViewTo(ViewGroup viewGroup){
        moveWebViewTo(viewGroup,ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }



    public void onResume(){
        super.onResume();
        isPaused = false;
    }

    public void onPause(){
        super.onPause();
        isPaused = true;
    }

    public void onDestroy(){
        super.destroy();
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        this.stackTabHistory.load(c[0],context);
        this.isInDesktopMode = Boolean.parseBoolean(c[1]);
    }

    @Override
    public String getData() {
        return MoFile.getData(this.stackTabHistory.getData(),this.isInDesktopMode);
    }
}