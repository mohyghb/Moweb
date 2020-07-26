package com.moofficial.moweb.MoPopupWindow;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MoPopupWindow {

    public static final int MAX_HEIGHT = 1200;
    public static final int MAX_WIDTH  = 800;
    public static final int MIN_HEIGHT = 400;
    public static final int MIN_WIDTH  = 500;

    private ArrayList<View> allViews = new ArrayList<>();
    private PopupWindow popupWindow;
    private Dialog dialog;
    private View rootView;
    private LinearLayout rootLinearLayout;
    private Context context;
    private int maxHeight = MAX_HEIGHT;
    private int maxWidth = MAX_WIDTH;
    private int minHeight = MIN_HEIGHT;
    private int minWidth = MIN_WIDTH;

    public MoPopupWindow(Context c){
        this.context = c;
    }

    public ArrayList<View> getAllViews() {
        return allViews;
    }

    public MoPopupWindow setAllViews(ArrayList<View> allViews) {
        this.allViews = allViews;
        return this;
    }

    public PopupWindow getPopupWindow() {
        return popupWindow;
    }

    public MoPopupWindow setPopupWindow(PopupWindow popupWindow) {
        this.popupWindow = popupWindow;
        return this;
    }

    public Dialog getDialog() {
        return dialog;
    }

    public MoPopupWindow setDialog(Dialog dialog) {
        this.dialog = dialog;
        return this;
    }

    public View getRootView() {
        return rootView;
    }

    public MoPopupWindow setRootView(View rootView) {
        this.rootView = rootView;
        return this;
    }

    public LinearLayout getRootLinearLayout() {
        return rootLinearLayout;
    }

    public MoPopupWindow setRootLinearLayout(LinearLayout rootLinearLayout) {
        this.rootLinearLayout = rootLinearLayout;
        return this;
    }

    public Context getContext() {
        return context;
    }

    public MoPopupWindow setContext(Context context) {
        this.context = context;
        return this;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public MoPopupWindow setMaxHeight(int maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public int getMaxWidth() {
        return maxWidth;
    }

    public MoPopupWindow setMaxWidth(int maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public int getMinHeight() {
        return minHeight;
    }

    public MoPopupWindow setMinHeight(int minHeight) {
        this.minHeight = minHeight;
        return this;
    }

    public int getMinWidth() {
        return minWidth;
    }

    public MoPopupWindow setMinWidth(int minWidth) {
        this.minWidth = minWidth;
        return this;
    }

    public MoPopupWindow setViews(View ... views){
        return this.setViews(Arrays.asList(views));
    }

    public MoPopupWindow setViews(List<View> views){
        allViews.addAll(views);
        return this;
    }

    public MoPopupWindow groupViewsHorizontally(View ... views){
        return groupViewsHorizontally(Arrays.asList(views));
    }

    public MoPopupWindow groupViewsHorizontally(List<View> views){
        LinearLayout linearLayout = new LinearLayout(this.context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        for(View v:views){
            linearLayout.addView(v);
        }
        allViews.add(linearLayout);
        return this;
    }



    private void addAll(ArrayList<View> v,View ... views){
        v.addAll(Arrays.asList(views));
    }

    /**
     * shows the pop up window as a drop down
     * to the anchor specified
     * @param anchor
     */
    public void show(View anchor){
        if(popupWindow!=null && popupWindow.isShowing()) {
            return;
        }else if(popupWindow==null){
            initPopupWindow();
        }
        popupWindow.showAsDropDown(anchor);
    }

    private void initPopupWindow() {
        initLayouts();
        popupWindow = new PopupWindow(rootView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setClippingEnabled(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setElevation(3f);
        calculateHeightWidth();
    }


    private void initLayouts() {
        rootView = MoInflaterView.inflate(R.layout.mo_pop_up_window_layout,this.context);
        this.rootLinearLayout = rootView.findViewById(R.id.root_linear_layout);
        addViewsToLayout(this.rootLinearLayout,this.allViews);
    }

    private void calculateHeightWidth() {
        rootLinearLayout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(getAppropriateHeight());
        popupWindow.setWidth(getAppropriateWidth());
    }

    private int getAppropriateHeight(){
        return Math.max(Math.min(rootLinearLayout.getMeasuredHeight(), maxHeight),minHeight);
    }

    private int getAppropriateWidth(){
        return Math.max(Math.min(rootLinearLayout.getMeasuredWidth(),maxWidth),minWidth);
    }

    /**
     * adds all the views to the layout
     * @param layout
     * @param views
     */
    private void addViewsToLayout(LinearLayout layout, List<View> views){
        layout.removeAllViews();
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        for(View v: views){
            v.setLayoutParams(lp);
            layout.addView(v);
        }
    }

}
