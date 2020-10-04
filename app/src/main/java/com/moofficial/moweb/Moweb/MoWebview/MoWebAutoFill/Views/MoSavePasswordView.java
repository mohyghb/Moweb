package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoPopupWindow.MoPopupWindow;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoSavePasswordView extends MoConstraint {

    private Button save;
    private Button neverSave;
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

    public MoSavePasswordView setOnNeverClickListener(View.OnClickListener l){
        neverSave.setOnClickListener(l);
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
        neverSave = findViewById(R.id.save_password_never_button);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }



    /**
     * we get permission to save their
     * passwords
     * @param c context for views
     * @param accepted when user hits save button, this function is called
     * @param never when the user hits this, we should
     *             not save any other user password for the host
     */
    public static void askUserToSave(Context c, WebView webView, Runnable accepted,Runnable never) {
        MoSavePasswordView savePasswordView = new MoSavePasswordView(c);
        MoPopupWindow popupWindow = new MoPopupWindow(c)
                .setViews(savePasswordView)
                .setWidth(ViewGroup.LayoutParams.MATCH_PARENT)
                .setHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setFocusable(false)
                .setOutsideTouchable(false)
                .setOverlapAnchor(false)
                .setDuration(3000)
                .build();

        savePasswordView
                .setOnCloseClickListener((v)-> popupWindow.dismiss())
                .setOnSaveClickListener((v)-> {
                    accepted.run();
                    popupWindow.dismiss();
                })
                .setOnNeverClickListener((v)-> {
                    never.run();
                    popupWindow.dismiss();
                });
        popupWindow.showOn(webView,0,0, Gravity.BOTTOM);
    }
}
