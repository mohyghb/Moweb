package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views;

import android.content.Context;
import android.util.AttributeSet;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoUserPassTitle extends MoConstraint {

    public MoUserPassTitle(Context context) {
        super(context);
    }

    public MoUserPassTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoUserPassTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.mo_user_pass_title;
    }

    @Override
    public void initViews() {

    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
