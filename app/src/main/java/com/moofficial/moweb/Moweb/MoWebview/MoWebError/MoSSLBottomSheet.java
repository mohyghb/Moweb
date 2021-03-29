package com.moofficial.moweb.Moweb.MoWebview.MoWebError;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoSSLBottomSheet extends MoConstraint {

    private TextView description;
    private Button proceed, decline;


    public MoSSLBottomSheet(Context context) {
        super(context);
    }

    public MoSSLBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoSSLBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoSSLBottomSheet setDescription(String text) {
        this.description.setText(text);
        return this;
    }

    public MoSSLBottomSheet onDecline(Runnable r) {
        this.decline.setOnClickListener((v) -> r.run());
        return this;
    }

    public MoSSLBottomSheet onProceed(Runnable r) {
        this.proceed.setOnClickListener((v) -> r.run());
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mo_ssl_bottom_sheet;
    }

    @Override
    public void initViews() {
        this.description = findViewById(R.id.mo_ssl_description);
        this.decline = findViewById(R.id.mo_ssl_decline);
        this.proceed = findViewById(R.id.mo_ssl_proceed);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
