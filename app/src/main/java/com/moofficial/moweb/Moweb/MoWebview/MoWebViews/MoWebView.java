package com.moofficial.moweb.Moweb.MoWebview.MoWebViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.core.widget.NestedScrollView;

import com.moofficial.moessentials.MoEssentials.MoBitmap.MoBitmap;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.MoTouchPosition.MoTouchPosition;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoHitTestResultParser;
import com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces.MoJsResize;
import com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces.MoWebElementDetection;
import com.moofficial.moweb.Moweb.MoWebview.MoStackTabHistory.MoStackTabHistory;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnPageFinishedListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnReceivedError;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnResizeWebViewListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnUpdateUrlListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebState;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;

// a web view which has more helper functions
public class MoWebView extends MoNestedWebView implements MoSavable, MoLoadable {



    private Context context;
    private MoWebClient client = new MoWebClient() {
        @Override
        public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
            super.doUpdateVisitedHistory(view, url, isReload);
            onUpdateUrlListener.onUrlUpdate(url,isReload);
            stackTabHistory.update();
            if(saveHistory && !isReload){
                MoHistoryManager.add(view);
            }
            // save history
            MoWebView.this.url = url;
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            MoLog.print("on page finished "+view.getProgress()+"" + url);
            // capture bitmap with a delay of one second after the page has been finished
            if(captureBitmapWhenFinishedLoading){
                captureBitmapWithDelay(1000);
            }
            // inject the js to web view
            evaluateJavascript(MoWebElementDetection.injectJs(context),null);
            super.onPageFinished(view, url);
            onPageFinishedListener.onFinished(view,url);
        }

//        @Nullable
//        @Override
//        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//            // getting the height of the web view and calling the js interface
////            if(request.isForMainFrame() || request.hasGesture()){
////                MoLog.print("for main frame or gesture");
////                //MoLog.print("intercept " + request.getUrl() + "====" + request.getMethod()  +"-=====" + request.isForMainFrame());
////                view.post(() -> view.loadUrl("javascript:MoJsResize.resize(document.body.getBoundingClientRect().height)"));
////            }
//
//            return super.shouldInterceptRequest(view, request);
//        }

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
    private MoOnPageFinishedListener onPageFinishedListener = (view, url) -> {};
    private MoBitmap moBitmap;
    private MoJsResize jsResize;
    private MoWebState webState = new MoWebState();
    private boolean captureBitmap = true;
    private boolean captureBitmapWhenFinishedLoading = false;
    private boolean enableWebViewResize = true;
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

    public boolean isEnableWebViewResize() {
        return enableWebViewResize;
    }

    public MoWebView setEnableWebViewResize(boolean enableWebViewResize) {
        this.enableWebViewResize = enableWebViewResize;
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
    public void init() {
        initWebView();
        // for showing pop up menu when
        // user long presses over an element of web view
        initStackTabHistory();
        initHitTestResult();
        addJsInterfaces();
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
        // showing inside overlay scroll bars
        setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
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

    /**
     * adds all the necessary js interfaces
     * that are included inside the js interface list
     */
    private void addJsInterfaces() {
        addResizeWebViewJs();
    }

    /**
     * makes the web view change it's height
     * when the content height changes
     * this is useful
     */
    private void addResizeWebViewJs() {
        if(enableWebViewResize){
            // add the resize web view on search finished js interface
            jsResize = new MoJsResize();
            addJavascriptInterface(jsResize,jsResize.getClassName());
        }
    }

    /**
     * when resizing from an activity
     * @param l
     * @return
     */
    public MoWebView setOnResizeWebViewListener(MoOnResizeWebViewListener l){
        this.jsResize.setOnResizeWebViewListener(l);
        return this;
    }


    public String getBaseUrl(){
        return MoUrlUtils.getBaseUrl(this.url);
    }



    /**
     * captures a bitmap from webview
     */
    public void captureBitmap() {
        if(captureBitmap){
            post(() -> moBitmap.captureBitmap(MoWebView.this));
        }
    }

    public void captureBitmapWithDelay(long delay){
        if(captureBitmap){
            post(()->moBitmap.captureBitmapWithDelay(this,delay));
        }
    }

    public void captureBitmapIfNotLoading(){
        if(captureBitmap){
            post(()->moBitmap.captureBitmapIfNotLoading(this));
        }
    }

    public void forceCaptureBitmapIfNotLoading(){
        if(captureBitmap){
            post(()->moBitmap.forceCaptureBitmapIfNotLoading(this));
        }
    }

    public void forceCaptureBitmap(){
        if(captureBitmap){
            moBitmap.forceCaptureBitmap(this);
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
     * @param viewGroup to move the web view inside there
     * @param lp layout params of the web view inside that view group
     */
    public void moveWebViewTo(ViewGroup viewGroup, ViewGroup.LayoutParams lp){
        if(this.getParent()!=null){
            // it has a parent, remove the web view from the parent first
            ((ViewGroup)getParent()).removeView(this);
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




    /**
     * restores the state of the web view
     * like: x,y position where it was left off
     * and the height and width of it
     * @param n
     */
    public void restoreState(NestedScrollView n){
        webState.applyState(this,n);
    }


    @Override
    public void onResume(){
        MoLog.print("web view on Resume()");
        super.onResume();
        isPaused = false;
    }

    @Override
    public void onPause(){
        MoLog.print("web view on pause()");
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
        String[] c = MoFile.loadable(s);
        this.stackTabHistory.load(c[0],context);
        this.isInDesktopMode = Boolean.parseBoolean(c[1]);
        this.webState.load(c[2],context);
    }

    @Override
    public String getData() {
        return MoFile.getData(this.stackTabHistory.getData(),this.isInDesktopMode,this.webState);
    }
}