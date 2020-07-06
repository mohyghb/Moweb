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

import com.moofficial.moweb.MoBitmap.MoBitmapUtils;
import com.moofficial.moweb.MoLog.MoLog;
import com.moofficial.moweb.MoTouchPosition.MoTouchPosition;
import com.moofficial.moweb.Moweb.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.R;

public class MoWebView {
    //UI
    private WebView webView;//private WebViewClient client;
    private MoWebClient client;
    private MoChromeClient chromeClient;
    private MoTab tab;
    private MoHitTestResultParser hitTestResultParser;
    private Context context;
    private MoTouchPosition touchPosition;
    //private View appbar;

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


//        webView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View view, int x, int y, int oldX, int oldY) {
//                if(y<=6){
//                    Toast.makeText(context,"hit top",Toast.LENGTH_SHORT).show();
//                    appbar.setVisibility(View.VISIBLE);
//                }else{
//                    appbar.setVisibility(View.GONE);
//                }
//            }
//        });

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

    // sets the visibility of the web view
    public void setVisibility(int v){
        this.webView.setVisibility(v);
    }


    public MoTab getTab() {
        return tab;
    }
}