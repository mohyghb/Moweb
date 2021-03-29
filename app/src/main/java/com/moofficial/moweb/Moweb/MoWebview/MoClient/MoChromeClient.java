package com.moofficial.moweb.Moweb.MoWebview.MoClient;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ProgressBar;


public class MoChromeClient extends WebChromeClient {

    private static final int MAX_PROGRESS = 100;


    private Context context;
    private ProgressBar bar;
    private boolean hideProgressBarWhenFinished = true;

    public MoChromeClient(Context context) {
        this.context = context;
    }

    public MoChromeClient setProgressBar(ProgressBar bar) {
        this.bar = bar;
        return this;
    }

    public MoChromeClient hideProgressBarWhenFinished(boolean hideProgressBarWhenFinished) {
        this.hideProgressBarWhenFinished = hideProgressBarWhenFinished;
        return this;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        updateProgressBar(newProgress);
    }

    /**
     * update the progress bar
     * if it is not null
     *
     * @param newProgress of the progress bar to be set
     */
    private synchronized void updateProgressBar(int newProgress) {
        if (bar == null)
            return;
        bar.post(() -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                bar.setProgress(newProgress, true);
            } else {
                bar.setProgress(newProgress);
            }
        });
        if (hideProgressBarWhenFinished && newProgress == MAX_PROGRESS) {
            // then we are done so hide the progress bar
            bar.setVisibility(View.INVISIBLE);
        } else {
            bar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {
        super.onShowCustomView(view, callback);
    }

}
