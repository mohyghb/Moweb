package com.moofficial.moweb.Moweb.MoTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import com.moofficial.moweb.Moweb.MoWebview.MoWebView;

public class MoTabUtils {

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToInTabMode(MoWebView m, ViewGroup viewGroup){
        m.moveWebViewTo(viewGroup);
        // making the web view function properly
        // TODO: touch point listener should not be working anymore
        m.setOnTouchListener((view, motionEvent) -> false);
        m.resumeTimers();
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToListTabMode(Context c, MoWebView m, ViewGroup viewGroup){
        int height = (int)(c.getResources().getDisplayMetrics()
                .heightPixels/2.25f);
        m.moveWebViewTo(viewGroup, ViewGroup.LayoutParams.MATCH_PARENT,height);
        m.setOnTouchListener((view, motionEvent) -> true);
        m.pauseTimers();
    }







}
