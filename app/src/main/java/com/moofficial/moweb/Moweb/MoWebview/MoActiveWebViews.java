package com.moofficial.moweb.Moweb.MoWebview;

import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

import java.util.LinkedList;
import java.util.Queue;

// a class to make sure that the unused web views are destroyed at the end
// and also, we call on pause when a certain number of web views get bigger
public class MoActiveWebViews {

    public static Queue<MoWebView> webViews = new LinkedList<>();



}
