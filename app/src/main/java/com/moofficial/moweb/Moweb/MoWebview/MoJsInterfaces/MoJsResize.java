package com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces;

import android.webkit.JavascriptInterface;

import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnResizeWebViewListener;

public class MoJsResize implements MoJsInterface {

    public static final String CLASS_NAME = "MoJsResize";



    private MoOnResizeWebViewListener onResizeWebViewListener = h -> {};

    public MoOnResizeWebViewListener getOnResizeWebViewListener() {
        return onResizeWebViewListener;
    }

    public MoJsResize setOnResizeWebViewListener(MoOnResizeWebViewListener onResizeWebViewListener) {
        this.onResizeWebViewListener = onResizeWebViewListener;
        return this;
    }

    @JavascriptInterface
    public void resize(float height) {
        onResizeWebViewListener.onResize(height);
    }

    @Override
    public String getClassName() {
        return CLASS_NAME;
    }

}
