package com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult;

import android.os.Message;
import android.webkit.WebView;

public class MoHitTestResult {

    private WebView webView;
    private MoOnSrcUrlTitleReceived onSrcUrlTitleReceived;


    /**
     * getting the hit test result of the web view
     *
     * @param webView to obtain hit test result from
     * @return this
     */
    public MoHitTestResult of(WebView webView) {
        this.webView = webView;
        return this;
    }

    /**
     * what we do when the result of hit test is obtained
     *
     * @param onResult listener to know what to do when we are done
     * @return this
     */
    public MoHitTestResult onResult(MoOnSrcUrlTitleReceived onResult) {
        this.onSrcUrlTitleReceived = onResult;
        return this;
    }

    /**
     * request the web view to find the href of
     * the node element that was last touched by the user
     *
     * @return this
     */
    public MoHitTestResult request() {
        MoHitHandler handler = new MoHitHandler().setOnHandleMessage(msg -> {
            WebView.HitTestResult h = webView.getHitTestResult();
            String src = msg.getData().getString("src");
            String url = msg.getData().getString("url");
            String title = msg.getData().getString("title");
            onSrcUrlTitleReceived.received(src, url, title);
        });
        Message m = new Message();
        m.setTarget(handler);
        webView.requestFocusNodeHref(m);
        return this;
    }


}
