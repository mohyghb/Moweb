package com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult;

public interface MoOnSrcUrlTitleReceived {

    /**
     * @param src of the hit web element (might be null)
     * @param url of the hit web element (might be null)
     * @param title of the hit web element (might be null)
     */
    void received(String src,String url,String title);
}
