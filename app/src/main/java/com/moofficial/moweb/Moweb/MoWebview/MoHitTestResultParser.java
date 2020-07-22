package com.moofficial.moweb.Moweb.MoWebview;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.moofficial.moessentials.MoEssentials.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.MoClipboard.MoClipboard;

import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoPopUpTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.R;

import java.util.Objects;

public class MoHitTestResultParser {


    private static final String OPEN_NEW_TAB = "Open in new tab";
    private static final String OPEN_NEW_INCOGNITO_TAB = "Open in new incognito tab";
    private static final String COPY_LINK_ADDRESS = "Copy link address";
    private static final String COPY_LINK_TEXT = "Copy link text";
    private static final String DOWNLOAD_LINK = "Download link";
    private static final String SHARE_LINK = "Share link";

    private static final int BOTTOM_SHEET_PEEK_HEIGHT = 200;

    private Context context;
    private MoWebView webView;

    private String selectedText;
    private String href;
    private String title;
    private MoWebElementDetection elementDetection;
    private ClipboardManager clipboard;
    private Dialog dialog;


    MoHitTestResultParser(Context context, MoWebView webView){
        this.context = context;
        this.webView = webView;
        // for detecting when the user clicks an element
        this.elementDetection = new MoWebElementDetection(webView);
        this.webView.addJavascriptInterface(this.elementDetection,MoWebElementDetection.CLASS_NAME);
        clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
    }


    boolean createDialog() {
        if(!elementDetection.isValidDialog()) {
            return false;
        }
        title = elementDetection.getInnerText();
        href = elementDetection.getHref();
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setOnDismissListener(dialogInterface -> elementDetection.onTouchEnd());
        dialog.setOnCancelListener(dialogInterface -> elementDetection.onTouchEnd());
        View v = MoInflaterView.inflate(R.layout.popup_menu__webview,context);
        LinearLayout linearLayout = v.findViewById(R.id.dialog_actions_linear_layout);
        ((TextView)v.findViewById(R.id.title_dialog)).setText(title);
        ((TextView)v.findViewById(R.id.description_dialog)).setText(href);
        addAction(MoWebElementDetection.isValidVar(title), this::copyLinkText,COPY_LINK_TEXT,context,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::copyLinkAddress,COPY_LINK_ADDRESS,context,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::openInNewTab,OPEN_NEW_TAB,context,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::openInNewIncognitoTab,OPEN_NEW_INCOGNITO_TAB,context,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::downloadLink,DOWNLOAD_LINK,context,linearLayout);
        addAction(MoWebElementDetection.isValidVar(href), this::shareLink,SHARE_LINK,context,linearLayout);
        dialog.setContentView(v);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return true;
    }


    /**
     * shows a web view for the text that they selected
     */
    private boolean smartTextSearch() {
        // don't show anything if there is no selected text
        if(selectedText == null || selectedText.isEmpty())
            return false;
        MoTab tab = new MoPopUpTab(selectedText,context).setCaptureImage(false);
        BottomSheetDialog bottomSheerDialog = new BottomSheetDialog(context);
        View parentView = MoInflaterView.inflate(R.layout.smart_text_search_layout,context);
        LinearLayout linearLayout = parentView.findViewById(R.id.nested_linear_bottom_sheet);
        linearLayout.addView(tab.getWebView());
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
        MoClipboard.addClipBoardText(clipboard,title,context,"Link Text");
        dismissDialog();
    }

    // copies the link address
    private void copyLinkAddress(){
        MoClipboard.addClipBoardText(clipboard,href,context,"Link Address");
        dismissDialog();
    }

    // opens this url in new tab
    private void openInNewTab(){
        MoTabsManager.addTab(context,href,true);
        dismissDialog();
    }

    private void openInNewIncognitoTab(){
        MoTabsManager.addIncognitoTab((Activity) context,href, true);
        dismissDialog();
    }

    private void downloadLink(){
        MoDownloadListener.download(context,href);
        dismissDialog();
    }

    private void shareLink(){
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, href);
        sendIntent.setType("text/html");
        Intent shareIntent = Intent.createChooser(sendIntent, "Choose an app");
        context.startActivity(shareIntent);
        dismissDialog();
    }

    // quits the dialog
    private void dismissDialog(){
        dialog.dismiss();
    }


    /**
     * this method returns the text that is selected in web view
     */
    void onTextSelected(){
        webView.evaluateJavascript("(function(){return window.getSelection().toString()})()",
                value -> {
                    selectedText = value.replace("\"","");
                    smartTextSearch();
                });
    }






}
