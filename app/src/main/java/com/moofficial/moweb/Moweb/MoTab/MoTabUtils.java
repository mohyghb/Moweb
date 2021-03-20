package com.moofficial.moweb.Moweb.MoTab;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

public class MoTabUtils {

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToInTabMode(MoWebView m, ViewGroup viewGroup, ViewGroup.LayoutParams lp){
        m.moveWebViewTo(viewGroup, lp);
        m.onResume();
    }

    @SuppressLint("ClickableViewAccessibility")
    public static void transitionToListTabMode(Context c, MoWebView m, ViewGroup viewGroup){
        m.setOnTouchListener((view, motionEvent) -> true);
        m.moveWebViewTo(viewGroup, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        m.setLayerType(View.LAYER_TYPE_SOFTWARE,null);
        m.pauseTimers();
    }







}
