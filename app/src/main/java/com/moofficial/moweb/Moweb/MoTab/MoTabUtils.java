package com.moofficial.moweb.Moweb.MoTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import com.moofficial.moweb.Moweb.MoWebview.MoWebView;

public class MoTabUtils {

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToInTabMode(MoWebView m, ViewGroup viewGroup,ViewGroup root){
        m.moveWebViewTo(viewGroup);
        // making the web view function properly
        // TODO: touch point listener should not be working anymore
        m.getWebView().setOnTouchListener((view, motionEvent) -> false);
        m.getWebView().resumeTimers();
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToListTabMode(Context c, MoWebView m, ViewGroup viewGroup,ViewGroup root){
        int height = (int)(c.getResources().getDisplayMetrics()
                .heightPixels/2.25f);
        m.moveWebViewTo(viewGroup, ViewGroup.LayoutParams.MATCH_PARENT,height);
        m.getWebView().setOnTouchListener((view, motionEvent) -> true);
        m.getWebView().pauseTimers();
    }







}
