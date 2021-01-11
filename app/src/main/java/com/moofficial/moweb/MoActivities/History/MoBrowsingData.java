package com.moofficial.moweb.MoActivities.History;

import android.view.Gravity;
import android.view.ViewGroup;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoDynamicUnit.MoDynamicUnit;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCheckBoxCard;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;
import com.moofficial.moweb.R;

public class MoBrowsingData extends MoSmartActivity {



    private MoCheckBoxCard histories,cookiesAndSiteData,
            cachedImages,savedPasswords,autoFillFormData,siteSettings;

    private MoToolBar moToolBar;

    @Override
    protected void init() {
        setTitle("Browser Data");
        initCheckBoxes();
        initToolbar();
        addExtendedFloatingActionButton();

    }

    private void initCheckBoxes() {
        histories = makeCardBox(R.drawable.history_clear_icon,
                R.string.histories_clear_title,
                R.string.histories_clear_description);

        cookiesAndSiteData= makeCardBox(R.drawable.cookies_clear_icon,
                R.string.cookies_clear_title,
                R.string.cookies_clear_description);

        cachedImages = makeCardBox(R.drawable.cached_image_clear_icon,
                R.string.cached_image_clear_title,
                R.string.cached_image_clear_description);

        savedPasswords = makeCardBox(R.drawable.saved_passwords_clear_icon,
                R.string.saved_passwords_clear_title,
                R.string.saved_passwords_clear_description);

        autoFillFormData = makeCardBox(R.drawable.auto_fill_clear_icon,
                R.string.auto_fill_clear_title,
                R.string.auto_fill_clear_description);

        siteSettings = makeCardBox(R.drawable.site_settings_clear_icon,
                R.string.site_settings_clear_title,
                R.string.site_settings_clear_description);

        // adding all the views to the linear nested layout
        l.linearNested.addViews(histories,cookiesAndSiteData,cachedImages,savedPasswords,autoFillFormData,siteSettings);
    }

    private void initToolbar() {
        moToolBar = new MoToolBar(this).onlyTitleAndLeftButtonVisible()
                .setLeftOnClickListener(view -> onBackPressed());
        moToolBar.getCardView().makeTransparent();
        l.toolbar.addToolbar(moToolBar);
        syncTitle(moToolBar.getTitle());
    }

    private MoCheckBoxCard makeCardBox(int p, int p2, int p3) {
        MoCheckBoxCard c = new MoCheckBoxCard(this)
                .setIcon(p)
                .setTitle(p2)
                .setDescription(p3);
        c.getCardView().setContentPadding(8f).makeCardRecRound();
        c.setLayoutParams(MoMarginBuilder.getLinearParams(this,8));
        return c;
    }

    private void addExtendedFloatingActionButton(){
        ExtendedFloatingActionButton efab = new ExtendedFloatingActionButton(this);
        efab.setText(R.string.browsing_data_clear_data);
        efab.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_baseline_delete_outline_24));
        CoordinatorLayout.LayoutParams p = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        p.gravity = Gravity.BOTTOM | Gravity.END;
        p.bottomMargin = MoDynamicUnit.convertDpToPixels(this,8f);
        p.rightMargin = MoDynamicUnit.convertDpToPixels(this,8f);
        efab.setLayoutParams(p);
        efab.setBackgroundColor(getColor(R.color.colorAccent));
        l.coordinatorLayout.addView(efab);
    }


    private void clear() {
        if(histories.isChecked()) {
            // clear all histories

        }
        if(cookiesAndSiteData.isChecked()) {
            // clear cookies
            MoWebUtils.clearCookies();
        }


    }

}