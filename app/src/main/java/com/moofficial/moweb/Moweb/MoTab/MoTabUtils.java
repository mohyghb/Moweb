package com.moofficial.moweb.Moweb.MoTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.ViewGroup;

import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

public class MoTabUtils {

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToInTabMode(MoWebView m, ViewGroup viewGroup, ViewGroup.LayoutParams lp){
        // todo, on touch listener needs to be updated
        m.setOnTouchListener((view, motionEvent) -> false);
        m.resumeTimers();
        m.setNestedScrollingEnabled(true);
        m.moveWebViewTo(viewGroup, lp);
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToListTabMode(Context c, MoWebView m, ViewGroup viewGroup){

        m.moveWebViewTo(viewGroup);
        m.setOnTouchListener((view, motionEvent) -> true);
        m.pauseTimers();
    }







}
