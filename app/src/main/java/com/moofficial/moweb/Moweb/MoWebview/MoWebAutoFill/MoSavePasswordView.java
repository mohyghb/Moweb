package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoSavePasswordView extends MoConstraint {

    private Button save;
    private ImageButton close;
    private TextView password;

    public MoSavePasswordView(Context context) {
        super(context);
    }

    public MoSavePasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoSavePasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public MoSavePasswordView setOnSaveClickListener(View.OnClickListener l){
        save.setOnClickListener(l);
        return this;
    }

    public MoSavePasswordView setOnCloseClickListener(View.OnClickListener l){
        close.setOnClickListener(l);
        return this;
    }

    public MoSavePasswordView setPasswordText(String text){
        password.setText(text);
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.save_password_conformation;
    }

    @Override
    public void initViews() {
        password = findViewById(R.id.save_password_password);
        save = findViewById(R.id.save_password_save_button);
        close = findViewById(R.id.close_save_password);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
