package com.moofficial.moweb.Moweb.MoWebview.MoWebError;

import android.content.Context;
import android.graphics.Typeface;
import android.net.http.SslCertificate;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoSSLCertificateView extends MoConstraint {

    LinearLayout linearLayout;
    SslCertificate sslCertificate;

    public MoSSLCertificateView(Context context) {
        super(context);
    }

    public MoSSLCertificateView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoSSLCertificateView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoSSLCertificateView setSslCertificate(SslCertificate sslCertificate) {
        this.sslCertificate = sslCertificate;
        return this;
    }

    public MoSSLCertificateView populate() {
        populateIssue("ISSUED TO", sslCertificate.getIssuedTo())
                .addSpace()
                .populateIssue("ISSUED BY", sslCertificate.getIssuedBy())
                .addSpace().addTextView("Not valid before", sslCertificate.getValidNotBeforeDate().toString())
                .addTextView("Not valid after", sslCertificate.getValidNotAfterDate().toString());

        return this;
    }

    public MoSSLCertificateView addSpace() {
        TextView v = new TextView(getContext());
        linearLayout.addView(v, MoMarginBuilder.getLinearParams(getContext(), 0));
        return this;
    }

    public MoSSLCertificateView populateIssue(String issueType, SslCertificate.DName dName) {
        SpannableStringBuilder s = new SpannableStringBuilder()
                .append(issueType, new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        TextView tv = new TextView(getContext());
        tv.setText(s);
        this.linearLayout.addView(tv);

        return addTextView("Organization", dName.getOName())
                .addTextView("Common-name (CN)", dName.getCName())
                .addTextView("Distinguished name", dName.getDName())
                .addTextView("Organizational Unit", dName.getUName());
    }

    public MoSSLCertificateView addTextView(String title, String description) {
        if (description == null || description.isEmpty()) {
            return this;
        }
        SpannableStringBuilder s = new SpannableStringBuilder()
                .append(title, new StyleSpan(Typeface.BOLD), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                .append("\n")
                .append(description);
        TextView tv = new TextView(getContext());
        tv.setText(s);
        this.linearLayout.addView(tv);
        return this;
    }

    public void show() {
        new MoBottomSheet(getContext())
                .setContentView(this)
                .setState(BottomSheetBehavior.STATE_EXPANDED)
                .build()
                .show();
    }

    @Override
    public int getLayoutId() {
        return R.layout.certificate_layout;
    }

    @Override
    public void initViews() {
        this.linearLayout = findViewById(R.id.certificate_layout_linear_layout);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
