package com.moofficial.moweb.Moweb.MoWebview.MoWebError;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.button.MaterialButton;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

// to show ssl errors and regular errors we use this class
public class MoWebErrorView extends MoConstraint {

    private TextView title, description;
    private MaterialButton advanced;
    private ConstraintLayout layout;

    public MoWebErrorView(Context context) {
        super(context);
    }

    public MoWebErrorView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoWebErrorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoWebErrorView onAdvancedClicked(View.OnClickListener l) {
        this.advanced.setOnClickListener(l);
        return this;
    }


    public MoWebErrorView normalError() {
        this.advanced.setVisibility(View.GONE);
        this.layout.setBackgroundTintList(getContext().getColorStateList(R.color.MoBackground));
        this.title.setTextColor(getColor(R.color.MoInverseColor));
        this.description.setTextColor(getColor(R.color.MoInverseColor));
        return this;
    }

    public MoWebErrorView sslError() {
        this.advanced.setVisibility(View.VISIBLE);
        this.layout.setBackgroundTintList(getContext().getColorStateList(R.color.error_color));
        this.title.setTextColor(getColor(R.color.MoBackground));
        this.description.setTextColor(getColor(R.color.MoBackground));
        return this;
    }

    public MoWebErrorView setTitle(String title) {
        this.title.setText(title);
        return this;
    }

    public MoWebErrorView setDescription(String des) {
        this.description.setText(des);
        return this;
    }




    @Override
    public int getLayoutId() {
        return R.layout.mo_error_view;
    }

    @Override
    public void initViews() {
        this.title = findViewById(R.id.mo_error_title);
        this.description = findViewById(R.id.mo_error_description);
        this.advanced = findViewById(R.id.mo_error_advanced_button);
        this.layout = findViewById(R.id.mo_error_layout);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
