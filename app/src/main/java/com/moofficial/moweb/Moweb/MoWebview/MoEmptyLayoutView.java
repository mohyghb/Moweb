package com.moofficial.moweb.Moweb.MoWebview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoEmptyLayoutView extends MoConstraint {

    public static LinearLayout.LayoutParams getUniversalMargin(Context context) {
        return MoMarginBuilder.getLinearParams(context, 0, 180, 0,0);
    }

    ImageView image;
    TextView title;

    public MoEmptyLayoutView(Context context) {
        super(context);
    }

    public MoEmptyLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoEmptyLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoEmptyLayoutView setText(@StringRes int res) {
        this.title.setText(res);
        return this;
    }

    public MoEmptyLayoutView setItemColor(@ColorRes int res) {
        this.title.setTextColor(getColor(res));
        this.image.setColorFilter(getColor(res));
        return this;
    }

    public MoEmptyLayoutView setIcon(@DrawableRes int res) {
        Drawable d = ContextCompat.getDrawable(getContext(), res);
        d.setTint(getColor(R.color.MoInverseColor));
        this.image.setImageDrawable(d);
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.empty_layout_view;
    }

    @Override
    public void initViews() {
        title = findViewById(R.id.empty_layout_title);
        image = findViewById(R.id.empty_layout_icon);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
