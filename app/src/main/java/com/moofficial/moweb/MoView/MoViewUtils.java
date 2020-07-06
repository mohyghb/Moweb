package com.moofficial.moweb.MoView;

import android.view.View;
import android.view.ViewGroup;

public class MoViewUtils {

    public static ViewGroup getParent(View view) {
        return (ViewGroup)view.getParent();
    }

    public static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if(parent != null) {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView) {
        ViewGroup parent = getParent(currentView);
        if(parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    public static void addView(View view,View addThisView){
        ViewGroup parentFrom = getParent(addThisView);
        ViewGroup parentView = getParent(view);
        if(parentFrom == null) {
            return;
        }
        final int index = parentFrom.indexOfChild(addThisView);
        removeView(addThisView);
        parentView.addView(addThisView,index);
    }



}
