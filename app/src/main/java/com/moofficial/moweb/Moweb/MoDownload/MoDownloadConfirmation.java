package com.moofficial.moweb.Moweb.MoDownload;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoLogo;
import com.moofficial.moweb.R;

public class MoDownloadConfirmation extends MoConstraint {

    private TextView description, title;
    private Button save,close;
    private MoLogo logo;

    public MoDownloadConfirmation(Context context) {
        super(context);
    }

    public MoDownloadConfirmation(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoDownloadConfirmation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoDownloadConfirmation onSave(Runnable save) {
        this.save.setOnClickListener((v) -> {
            save.run();
            this.save.setEnabled(false);
        });
        return this;
    }

    public MoDownloadConfirmation withName(String name) {
        this.description.setText("Do you want to download " + name +"?");
        this.logo.setText(MoString.getSignature(name));
        return this;
    }

    public MoDownloadConfirmation onCancel(Runnable onCancel) {
        this.close.setOnClickListener((v)-> {
            onCancel.run();
            this.close.setEnabled(false);
        });
        return this;
    }

    public MoDownloadConfirmation setSize(long total) {
        this.title.setText("Download (" + MoDownloadUtils.formatSize(total)+")");
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.download_confirmation_layout;
    }

    @Override
    public void initViews() {
        this.description = findViewById(R.id.download_confirmation_description);
        this.close = findViewById(R.id.download_confirmation_close);
        this.save = findViewById(R.id.download_confirmation_save);
        this.title = findViewById(R.id.download_confirmation_title);
        this.logo = findViewById(R.id.download_confirmation_logo);
        this.logo.filledCircle();
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
