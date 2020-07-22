package com.moofficial.moweb.MoPopupWindow;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.moofficial.moweb.MoDynamicUnit.MoDynamicUnit;

import java.util.ArrayList;
import java.util.List;


// uses our standards to build various views
public class MoPopupItemBuilder {

    public static final int MO_ITEM_PADDING = MoDynamicUnit.convertDpToPixels(14f);
    public static final int MO_ICON_PADDING = MoDynamicUnit.convertDpToPixels(16f);

    private final int TYPE_TEXT_BUTTON = 0;
    private final int TYPE_IMAGE_BUTTON = 1;


    private Context context;
    private ArrayList<View> views = new ArrayList<>();
    private int textButtonPadding = MO_ITEM_PADDING;
    private int imageButtonPadding = MO_ICON_PADDING;


    public MoPopupItemBuilder(Context c){
        this.context = c;
    }


    public Context getContext() {
        return context;
    }

    public MoPopupItemBuilder setContext(Context context) {
        this.context = context;
        return this;
    }

    public ArrayList<View> getViews() {
        return views;
    }

    public MoPopupItemBuilder setViews(ArrayList<View> views) {
        this.views = views;
        return this;
    }

    public int getTextButtonPadding() {
        return textButtonPadding;
    }

    public MoPopupItemBuilder setTextButtonPadding(int textButtonPadding) {
        this.textButtonPadding = textButtonPadding;
        return this;
    }

    public int getImageButtonPadding() {
        return imageButtonPadding;
    }

    public MoPopupItemBuilder setImageButtonPadding(int imageButtonPadding) {
        this.imageButtonPadding = imageButtonPadding;
        return this;
    }

    /**
     *

     * @param title
     * @param clickListener
     * @return
     */
    public MoPopupItemBuilder buildTextButton(String title, View.OnClickListener clickListener){
        TextView v = new TextView(context);
        v.setText(title);
        finalViewBuild(v,clickListener,TYPE_IMAGE_BUTTON);
        return this;
    }

    // overloaded method of buildTextButton
    public MoPopupItemBuilder buildTextButton(int title, View.OnClickListener clickListener){
        return buildTextButton(getString(title),clickListener);
    }

    /**
     *
     * @param drawable
     * @param clickListener
     * @return
     */
    public MoPopupItemBuilder buildCheckedImageButton(int drawable, View.OnClickListener clickListener){
        ImageButton ib = new ImageButton(context);
        ib.setBackground(context.getDrawable(drawable));
        finalViewBuild(ib,clickListener,TYPE_IMAGE_BUTTON);
        return this;
    }

    /**
     *
     * @param checked
     * @param notChecked
     * @param clickListener
     * @param isChecked
     * @return
     */
    public MoPopupItemBuilder buildCheckedImageButton(int checked, int notChecked,
                                                      @NonNull View.OnClickListener clickListener, MoPopupCondition isChecked){
        ImageButton ib = new ImageButton(context);
        ib.setBackground(isChecked.getCondition()?getDrawable(checked):getDrawable(notChecked));
        View.OnClickListener switchListener = new View.OnClickListener() {
            boolean n = isChecked.getCondition();
            @Override
            public void onClick(View view) {
                n = !n;
                ib.setBackground(n?getDrawable(checked):getDrawable(notChecked));
                clickListener.onClick(view);
            }
        };
        finalViewBuild(ib,switchListener,TYPE_IMAGE_BUTTON);
        return this;
    }


    /**
     *
     * @param v
     * @param clickListener
     */
    private void finalViewBuild(View v, View.OnClickListener clickListener,int type) {
        v.setOnClickListener(clickListener);
        applyPadding(v,type);
        makeItRippleOnClick(v);
        views.add(v);
    }


    /**
     *
     * @param v
     */
    private void makeItRippleOnClick(View v){
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
        v.setForeground(context.getDrawable(outValue.resourceId));
        v.setClickable(true);
    }

    /**
     *
     * @param v
     */
    private void applyPadding(View v,int type){
        int p = 0;
        switch (type){
            case TYPE_IMAGE_BUTTON:
                p = MO_ICON_PADDING;
                break;
            case TYPE_TEXT_BUTTON:
                p = MO_ITEM_PADDING;
                break;
        }
        v.setPadding(p,p,p,p);
    }


    private Drawable getDrawable(int d){
        return context.getDrawable(d);
    }

    private String getString(int s){
        return context.getString(s);
    }

    public List<View> build(){
        return this.views;
    }

}
