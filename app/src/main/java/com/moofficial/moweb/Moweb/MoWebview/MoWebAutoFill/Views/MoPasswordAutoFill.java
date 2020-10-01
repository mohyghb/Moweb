package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoPasswordAutoFill extends MoConstraint {

    private TextView username,password;

    public MoPasswordAutoFill(Context context) {
        super(context);
    }

    public MoPasswordAutoFill(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoPasswordAutoFill(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoPasswordAutoFill setUsername(String user) {
        username.setText(user);
        return this;
    }

    public MoPasswordAutoFill setPassword(String pass) {
        password.setText(pass);
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mo_password_auto_fill_layout;
    }

    @Override
    public void initViews() {
        this.username = findViewById(R.id.password_auto_fill_username);
        this.password = findViewById(R.id.password_auto_fill_password);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
