package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moweb.R;

public class MoUserPassHolderView extends MoConstraint {

    private TextView username,password,host;
    private MoCardView cardView;


    public MoUserPassHolderView(Context context) {
        super(context);
    }

    public MoUserPassHolderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoUserPassHolderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoUserPassHolderView setUsername(String user) {
        username.setText(user);
        return this;
    }

    public MoUserPassHolderView setPassword(String pass) {
        password.setText(pass);
        return this;
    }

    public MoUserPassHolderView setHost(String host) {
        this.host.setText(host);
        return this;
    }

    public MoUserPassHolderView setOnAutoFillClickListener(Runnable r) {
        this.cardView.setOnClickListener((v)->r.run());
        return this;
    }

    public MoCardView getCardView() {
        return this.cardView;
    }

    @Override
    public int getLayoutId() {
        return R.layout.mo_user_pass_holder;
    }

    @Override
    public void initViews() {
        this.username = findViewById(R.id.password_auto_fill_username);
        this.password = findViewById(R.id.password_auto_fill_password);
        this.cardView = findViewById(R.id.user_pass_holder_card_view);
        this.host = findViewById(R.id.user_pass_host);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
