package com.moofficial.moweb.Moweb.MoWebview;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShare;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.MoClipboard.MoClipboardUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoPopUpTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoJsInterfaces.MoWebElementDetection;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

import java.util.Objects;

public class MoHitTestResultParser {



    private static final int BOTTOM_SHEET_PEEK_HEIGHT = 200;


    private MoWebView webView;
    private Context context;
    private String selectedText;
    private String href;
    private String title;
    private MoWebElementDetection elementDetection;
    private ClipboardManager clipboard;
    private Dialog dialog;


    public MoHitTestResultParser(MoWebView webView){
        this.webView = webView;
        // for detecting when the user clicks an element
        this.elementDetection = new MoWebElementDetection(webView);
        this.webView.addJavascriptInterface(this.elementDetection,MoWebElementDetection.CLASS_NAME);
    }

    /**
     * create a dialog if the user long clicks
     * on a link, or show a smart web search
     * if the user is trying to copy a text
     * @param c context of the situation
     */
    public void createDialogOrSmartText(Context c){
        boolean b = createDialog(c);
        if(!b){
            onTextSelected(c);
        }
    }


    /**
     * creates a dialog
     * of available options
     * @param c
     * @return
     */
    public boolean createDialog(Context c) {
        if(!elementDetection.isValidDialog()) {
            return false;
        }
        this.context = c;
        this.title = elementDetection.getInnerText();
        this.href = elementDetection.getHref();
        this.dialog = new Dialog(c);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setCancelable(true);
        this.dialog.setOnDismissListener(dialogInterface -> elementDetection.onTouchEnd());
        this. dialog.setOnCancelListener(dialogInterface -> elementDetection.onTouchEnd());
        View v = MoInflaterView.inflate(R.layout.popup_menu__webview,c);
        LinearLayout linearLayout = v.findViewById(R.id.dialog_actions_linear_layout);
        ((TextView)v.findViewById(R.id.title_dialog)).setText(title);
        ((TextView)v.findViewById(R.id.description_dialog)).setText(href);
        addAction(MoWebElementDetection.isValidVar(title), this::copyLinkText,c.getString(R.string.copy_link_text),c,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::copyLinkAddress,c.getString(R.string.copy_link_address),c,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::openInNewTab,c.getString(R.string.open_in_tab),c,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::openInNewIncognitoTab,c.getString(R.string.open_in_private_tab),c,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::downloadLink,c.getString(R.string.download_link),c,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::shareLink,c.getString(R.string.share_link),c,linearLayout);
        dialog.setContentView(v);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return true;
    }


    /**
     * shows a web view for the text that they selected
     * construction this is not working (the web view is not showing)
     */
    private boolean smartTextSearch(Context context) {
        // don't show anything if there is no selected text
        if(selectedText == null || selectedText.isEmpty())
            return false;

        MoTab tab = new MoPopUpTab(selectedText,context).setCaptureImage(false);
        tab.init();

        BottomSheetDialog bottomSheerDialog = new BottomSheetDialog(context);
        View parentView = MoInflaterView.inflate(R.layout.smart_text_search_layout,context);
        LinearLayout linearLayout = parentView.findViewById(R.id.nested_linear_bottom_sheet);
        linearLayout.addView(tab.getMoWebView());
        bottomSheerDialog.setContentView(parentView);
        bottomSheerDialog.getBehavior().setSkipCollapsed(false);
        bottomSheerDialog.getBehavior().setState(BottomSheetBehavior.STATE_COLLAPSED);
        bottomSheerDialog.getBehavior().setPeekHeight(BOTTOM_SHEET_PEEK_HEIGHT);
        bottomSheerDialog.getBehavior().setDraggable(true);
        bottomSheerDialog.getBehavior().setFitToContents(false);
        //Objects.requireNonNull(bottomSheerDialog.getWindow()).clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        bottomSheerDialog.show();
        return true;
    }



    /**
     * returns a button if condition is true, with onclick listener and text
     * @param condition
     * @param onClickListener
     * @param text
     * @param context
     * @return
     */
    private void addAction(boolean condition,Runnable onClickListener,String text,Context context,LinearLayout linearLayout){
        if(!condition)
            return;
        MaterialButton button = new MaterialButton(context,null, R.attr.borderlessButtonStyle);
        button.setText(text);
        button.setOnClickListener(view -> onClickListener.run());
        linearLayout.addView(button);
    }


    // copies the link text in clip board
    private void copyLinkText() {
        MoClipboardUtils.add(context,title,"Link Text");
        dismissDialog();
    }

    // copies the link address
    private void copyLinkAddress(){
        MoClipboardUtils.add(context,href,"Link Address");
        dismissDialog();
    }

    // opens this url in new tab
    private void openInNewTab(){
        MoTabsManager.addTab(context,href,true);
        dismissDialog();
    }

    private void openInNewIncognitoTab(){
        MoTabsManager.addPrivateTab((Activity) context,href, true);
        dismissDialog();
    }

    private void downloadLink(){
        MoDownloadListener.download(context,href);
        dismissDialog();
    }

    private void shareLink(){
        new MoShare().setText(this.href).shareText(this.context);
        dismissDialog();
    }

    // quits the dialog
    private void dismissDialog(){
        dialog.dismiss();
    }


    /**
     * this method returns the text that is selected in web view
     */
    public void onTextSelected(Context context){
        webView.post(() -> webView.evaluateJavascript("(function(){return window.getSelection().toString()})()",
                value -> {
                    selectedText = value.replace("\"","");
                    smartTextSearch(context);
                }));
    }






}
