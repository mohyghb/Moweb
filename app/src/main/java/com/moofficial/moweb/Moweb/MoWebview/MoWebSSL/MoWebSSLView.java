package com.moofficial.moweb.Moweb.MoWebview.MoWebSSL;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

// todo not complete
public class MoWebSSLView extends MoConstraint {

    private ImageButton infoButton;

    public MoWebSSLView(Context context) {
        super(context);
    }

    public MoWebSSLView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoWebSSLView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoWebSSLView secure() {
        this.infoButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.ic_baseline_lock_24));
        return this;
    }

    public MoWebSSLView unknown() {
        this.infoButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.ic_baseline_info_24));
        return this;
    }

    public MoWebSSLView dangerous() {
        this.infoButton.setImageDrawable(ContextCompat.getDrawable(getContext(),
                R.drawable.ic_baseline_report_problem_24));
        return this;
    }

    public MoWebSSLView setOnSSLInfoClicked(View.OnClickListener l) {
        this.infoButton.setOnClickListener(l);
        return this;
    }


//    public MoWebSSLView showSSLInfo(WebView webView) {
//        webView.getCertificate().getX509Certificate().
//        return this;
//    }

    @Override
    public int getLayoutId() {
        return R.layout.mo_ssl_view;
    }

    @Override
    public void initViews() {
        this.infoButton = findViewById(R.id.mo_ssl_view_button);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
