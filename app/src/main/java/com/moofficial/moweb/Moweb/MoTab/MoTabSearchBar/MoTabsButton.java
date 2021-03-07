package com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

// the button that is used to go to the main menu
public class MoTabsButton extends MoConstraint {

    private TextView tabsButton;
    private ConstraintLayout layout;

    public MoTabsButton(Context context) {
        super(context);
    }

    public MoTabsButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoTabsButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoTabsButton setNumberOfTabs(int s){
        if(s>99){
            tabsButton.setText(R.string.a_lot_of_tabs);
            this.tabsButton.setTextSize(12f);
        }else{
            this.tabsButton.setText(String.valueOf(s));
            this.tabsButton.setTextSize(14f);
        }
        return this;
    }

    public MoTabsButton setOnTabsButtonClicked(View.OnClickListener l){
        this.setOnClickListener(l);
        return this;
    }


    public void changeColor(@ColorRes int borderColor, @ColorRes int textColor){
        tabsButton.setBackgroundTintList(getContext().getColorStateList(borderColor));
        tabsButton.setTextColor(getContext().getColor(textColor));
    }

    public void showActivated() {
        changeColor(R.color.colorPrimary,R.color.colorPrimary);
    }

    public void showDeactivated() {
        changeColor(R.color.MoInverseColor,R.color.MoInverseColor);
    }

    @Override
    public int getLayoutId() {
        return R.layout.tabs_button;
    }

    @Override
    public void initViews() {
        this.tabsButton = findViewById(R.id.tabs_button);
        this.layout = findViewById(R.id.tabs_button_layout);
        showDeactivated();
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
