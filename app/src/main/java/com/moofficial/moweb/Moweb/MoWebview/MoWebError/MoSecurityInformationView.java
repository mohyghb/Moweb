package com.moofficial.moweb.Moweb.MoWebview.MoWebError;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;

public class MoSecurityInformationView extends MoConstraint {
    ImageView icon;
    TextView title, description;
    Button details;

    public MoSecurityInformationView(Context context) {
        super(context);
    }

    public MoSecurityInformationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoSecurityInformationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoSecurityInformationView sync(WebView view, SslError sslError) {
        String url = view.getUrl();
        if (URLUtil.isHttpUrl(url) || sslError != null) {
            Drawable d = ContextCompat.getDrawable(getContext(), R.drawable.ic_baseline_lock_open_24);
            DrawableCompat.setTint(d, getColor(R.color.not_secure_site_color));
            icon.setImageDrawable(d);
            title.setText(R.string.connection_is_not_secure_title);
            title.setTextColor(getColor(R.color.not_secure_site_color));
            description.setText(R.string.connection_is_not_secure_description);
        }
        details.setOnClickListener((v)-> new MoSSLCertificateView(getContext())
                .setSslCertificate(view.getCertificate())
                .populate()
                .show());
        return this;
    }

    public MoSecurityInformationView show() {
        new MoBottomSheet(getContext())
                .setContentView(this)
                .setState(BottomSheetBehavior.STATE_EXPANDED)
                .build()
                .show();
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.security_information_layout;
    }

    @Override
    public void initViews() {
        icon = findViewById(R.id.security_information_icon);
        title = findViewById(R.id.security_information_title);
        description = findViewById(R.id.security_information_description);
        details = findViewById(R.id.security_information_details);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
