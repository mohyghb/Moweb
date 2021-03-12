package com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.moofficial.moessentials.MoEssentials.MoClipboard.MoClipboardUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoImageButton;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoWrappers.MoWrapperLinearLayout;
import com.moofficial.moweb.R;

public class MoSmartTextSearchView extends MoConstraint {

    TextView title;
    MoWrapperLinearLayout linearLayout;
    LinearLayout parent;
    ProgressBar progressBar;
    MoImageButton copy, openInNewTab;

    public MoSmartTextSearchView(Context context) {
        super(context);
    }

    public MoSmartTextSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoSmartTextSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoSmartTextSearchView setTitle(String text) {
        this.title.setText(text);
        return this;
    }

    public MoSmartTextSearchView add(View v) {
        this.linearLayout.addView(v, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return this;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MoSmartTextSearchView onOpenNewTab(Runnable r) {
        this.openInNewTab.setOnClickListener((v) -> r.run());
        return this;
    }


    @Override
    public int getLayoutId() {
        return R.layout.smart_text_search_layout;
    }

    @Override
    public void initViews() {
        this.title = findViewById(R.id.bottom_sheet_title);
        this.linearLayout = new MoWrapperLinearLayout(findViewById(R.id.nested_linear_bottom_sheet));
        this.progressBar = findViewById(R.id.progress_bar);
        this.copy = findViewById(R.id.bottom_sheet_copy);
        this.openInNewTab = findViewById(R.id.bottom_sheet_open);
        this.copy.setOnClickListener((v) -> MoClipboardUtils.add(getContext(), this.title.getText().toString()));

        this.parent = findViewById(R.id.bottom_sheet_linear_layout);
        this.parent.setMinimumHeight(getContext().getResources().getDisplayMetrics().heightPixels);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
