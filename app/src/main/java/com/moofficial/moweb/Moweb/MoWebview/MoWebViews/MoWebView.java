package com.moofficial.moweb.Moweb.MoWebview.MoWebViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.http.SslError;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHitTestResultParser;
import com.moofficial.moweb.Moweb.MoWebview.MoStackTabHistory.MoStackWebHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnPageFinishedListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnReceivedError;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnUpdateUrlListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebState;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;



// a better web view!
public class MoWebView extends MoNestedWebView implements MoSavable, MoLoadable {




    private MoWebClient client = new MoWebClient() {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            onErrorReceived.onSSLErrorReceived(view, handler, error);
        }

        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            url = MoUrlUtils.removeUrlUniqueness(url);
            onUpdateUrlListener.onUrlUpdate(url,isReload);
            stackTabHistory.update(url,isReload);
            if(saveHistory && !isReload) {
                MoHistoryManager.add(view);
            }
            MoWebView.this.url = url;
            //MoLog.print("update visited history " + url);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            MoWebView.this.onPageFinishedListener.onFinished(view,url);
            onErrorReceived.hideError();
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

//            if (request.getUrl().toString().equals(view.getUrl())) {
//                MoLog.print("they are the same");
//                forceReload();
//                return true;
//            }
//            MoLog.print(request.getUrl().toString()  + "but for  web view this is: " + view.getUrl());
//            moJsInput.gatherData();
            return super.shouldOverrideUrlLoading(view, request);
        }
    };
    private String url;
    private MoChromeClient chromeClient;
    private MoHitTestResultParser hitTestResultParser;
    private MoStackWebHistory stackTabHistory = new MoStackWebHistory();
    private MoOnUpdateUrlListener onUpdateUrlListener = (url, isReload) -> {};
    private MoOnReceivedError onErrorReceived = MoOnReceivedError.EMPTY;
    private MoOnPageFinishedListener onPageFinishedListener = (view, url) -> {};
    private MoWebState webState = new MoWebState();
    private boolean captureBitmap = true;
    private boolean saveHistory = true;
    private boolean isInDesktopMode = false;
    private boolean isPaused = true;

    public MoWebView(Context context) {
        super(context);
    }

    public MoWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoWebView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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

    public MoStackWebHistory getStackTabHistory() {
        return stackTabHistory;
    }

    public MoWebView setStackTabHistory(MoStackWebHistory stackTabHistory) {
        this.stackTabHistory = stackTabHistory;
        return this;
    }


    public MoWebState getWebState() {
        return webState;
    }

    public MoWebView setWebState(MoWebState webState) {
        this.webState = webState;
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

    public MoOnPageFinishedListener getOnPageFinishedListener() {
        return onPageFinishedListener;
    }

    public MoWebView setOnPageFinishedListener(MoOnPageFinishedListener onPageFinishedListener) {
        this.onPageFinishedListener = onPageFinishedListener;
        return this;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public MoWebView init() {
        initWebView();
        // for showing pop up menu when
        // user long presses over an element of web view
        initHitTestResult();
        addJsInterfaces();
        enableCorrectMode();
        return this;
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
        // showing inside overlay scroll bars
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // setting the cookie based on the setting of the user
        MoWebUtils.updateThirdPartyCookies(this);

        clearCache(true);


    }


    private void initHitTestResult() {
        this.hitTestResultParser = new MoHitTestResultParser(this);
    }

    /**
     * adds all the necessary js interfaces
     * that are included inside the js interface list
     * (classes themselves add
     * themselves to the js interfaces)
     */
    private void addJsInterfaces() {
//        this.moJsInput = new MoJsInput(this);
    }



    public String getBaseUrl(){
        return MoUrlUtils.getBaseUrl(this.url);
    }

    @Override
    public String getUrl() {
        return this.url;
    }



    public MoWebView neverOverScroll(){
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        return this;
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
    @Override
    public boolean canGoForward(){
        return stackTabHistory.canGoForward();
    }

    /**
     * goes forward a url
     */
    @Override
    public void goForward(){
        this.loadUrl(this.stackTabHistory.goForward());
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
    @Override
    public boolean canGoBack(){
        return stackTabHistory.canGoBack();
    }

    /**
     * goes back a page
     * can throw EmptyStackException if the stack
     * tab history is empty
     * we load from cache if possible
     */
    @Override
    public void goBack() {
        loadUrl(this.stackTabHistory.goBack());
    }


    /**
     * goes backwards if it can
     */
    public void goBackIfYouCan(){
        if(canGoBack()){
            goBack();
        }
    }


    /**
     * reloads the web view even if the
     * cache mode is turned on
     * if the url is not null and not empty
     * (reloads it to the current url of this class not the
     * url inside the web view class)
     */
    public void forceReload(){
        if(this.url!=null && !this.url.isEmpty()) {
            this.loadUrl(this.url,false);
        }
    }

    /**
     * loads the url normally if
     * loadFromCache is true, and if it is false
     * we make sure that the url is unique so that the
     * load url does not load it from cache
     * @param url to be loaded inside the web view
     * @param loadFromCache whether we should load this url
     *                      from cache or not
     */
    public void loadUrl(String url,boolean loadFromCache) {
        loadUrl(loadFromCache?url:MoUrlUtils.makeUrlUnique(url));
    }






    /**
     * loading the url and saving
     * it inside our fields
     * @param url
     */
    @Override
    public void loadUrl(String url) {
        onErrorReceived.hideError();
        super.loadUrl(url);
        this.url = url;
    }

    @Override
    public String getTitle() {
        String title = super.getTitle();
        return title.isEmpty() ? getUrl() : title;
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
     * @param viewGroup to move the web view inside there
     * @param lp layout params of the web view inside that view group
     */
    public void moveWebViewTo(ViewGroup viewGroup, ViewGroup.LayoutParams lp){

        if(this.getParent()!=null){
            // it has a parent, remove the web view from the parent first
            ViewGroup p = ((ViewGroup)getParent());
            p.removeView(this);
        }
        viewGroup.addView(this,lp);
    }

    /**
     * moves the web view to the view group that is passed
     * @param viewGroup to move the web view inside there
     */
    public void moveWebViewTo(ViewGroup viewGroup,int width, int height){
       moveWebViewTo(viewGroup,new ViewGroup.LayoutParams(width,height));
    }

    /**
     * moves the web view to the view group that is passed
     * @param viewGroup to move the web view inside there
     *                  we automatically set the width and height to
     *                  match parent (you can use other methods to specify
     *                  height and width)
     */
    public void moveWebViewTo(ViewGroup viewGroup){
        moveWebViewTo(viewGroup,ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
    }


    public MoWebView removeListeners() {
        this.setOnErrorReceived(MoOnReceivedError.EMPTY);
        this.setDownloadListener(null);
        return this;
    }



    /**
     * restores the state of the web view
     * like: x,y position where it was left off
     * and the height and width of it
     */
    public void restoreState(){
        webState.applyState(this);
    }


    @Override
    public void onResume(){
       // MoLog.print("web view on Resume()");
        super.onResume();
        isPaused = false;
    }

    @Override
    public void onPause(){
      //  MoLog.print("web view on pause()");
        super.onPause();
        isPaused = true;
    }

    public void onDestroy(){
        super.destroy();
        //todo remove all the js interfaces
    }

    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public void load(String s, Context context) {
        if(s==null || s.isEmpty())
            return;
        String[] c = MoFile.loadable(s);
        if (c.length != 0) {
            this.stackTabHistory.load(c[0],context);
            this.isInDesktopMode = Boolean.parseBoolean(c[1]);
            this.webState.load(c[2],context);
        }
    }

    @Override
    public String getData() {
        return MoFile.getData(this.stackTabHistory.getData(),this.isInDesktopMode,this.webState);
    }
}