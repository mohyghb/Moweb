package com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.moofficial.moessentials.MoEssentials.MoClipboard.MoClipboardUtils;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShareUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moweb.R;

public class MoTabSearchHelper extends MoConstraint {

    private TextView text;
    private ImageButton copy, share;

    public MoTabSearchHelper(Context context) {
        super(context);
    }

    public MoTabSearchHelper(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoTabSearchHelper(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MoTabSearchHelper setText(String s) {
        this.text.setText(s);
        return this;
    }

    public MoTabSearchHelper setOnCopyListener(View.OnClickListener l) {
        this.copy.setOnClickListener(l);
        return this;
    }

    public MoTabSearchHelper setOnShareListener(View.OnClickListener l) {
        this.share.setOnClickListener(l);
        return this;
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_search_helper;
    }

    @Override
    public void initViews() {
        this.share = findViewById(R.id.tab_search_helper_share);
        this.copy = findViewById(R.id.tab_search_helper_copy);
        this.text = findViewById(R.id.tab_search_helper_text);

        defaultShare();
        defaultCopy();
    }

    private void defaultCopy() {
        this.copy.setOnClickListener((v) -> MoClipboardUtils.add(getContext(), this.text.getText().toString()));
    }

    private void defaultShare() {
        this.share.setOnClickListener((v) -> MoShareUtils.share(getContext(), this.text.getText().toString()));
    }


    @Override
    public int[] getAttrs() {
        return new int[0];
    }
}
