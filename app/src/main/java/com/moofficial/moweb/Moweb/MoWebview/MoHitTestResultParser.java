package com.moofficial.moweb.Moweb.MoWebview;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileProvider.MoFileProvider;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShare;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShareUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.MoClipboard.MoClipboardUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoPopUpTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebManifest;
import com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult.MoHitTestResult;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

import java.util.Objects;

public class MoHitTestResultParser {



    private static final int BOTTOM_SHEET_PEEK_HEIGHT = 200;


    private MoWebView webView;
    private Context context;
    private String selectedText;
    private String url;
    private String title;
    private String src;
    private Bitmap bitmap;
    private Dialog dialog;


    public MoHitTestResultParser(MoWebView webView){
        this.webView = webView;
    }

    /**
     * create a dialog if the user long clicks
     * on a link, or show a smart web search
     * if the user is trying to copy a text
     * @param c context of the situation
     */
    public void createDialogOrSmartText(Context c){
        hitTestResult(c);
    }


    /**
     * creates a dialog
     * of available options
     * @param c
     * @return
     */
    public boolean createDialog(Context c) {
        if(uselessDialog()) {
            // we don't have anything to show to the user
            return false;
        }
        this.context = c;
        this.dialog = new Dialog(c);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setCancelable(true);
        View v = MoInflaterView.inflate(R.layout.popup_menu__webview,c);
        LinearLayout linearLayout = v.findViewById(R.id.dialog_actions_linear_layout);
        ((TextView)v.findViewById(R.id.title_dialog)).setText(title);
        ((TextView)v.findViewById(R.id.description_dialog)).setText(url);

        ImageView imageView = v.findViewById(R.id.popup_web_view_image);
        //monote image
        if(src!=null && !src.isEmpty()){
            MoLog.print("should see an image");
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().load(this.src).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    bitmap = resource;
                    TransitionManager.beginDelayedTransition((ViewGroup) v);
                    imageView.setImageBitmap(resource);
                    addAction(true,MoHitTestResultParser.this::shareImage,
                            context.getString(R.string.share_image),context,linearLayout);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        }else{
            imageView.setVisibility(View.GONE);
        }


        addAction(isValidVar(title), this::copyLinkText,c.getString(R.string.copy_link_text),c,linearLayout);
        addAction(isValidVar(url), this::copyLinkAddress,c.getString(R.string.copy_link_address),c,linearLayout);
        addAction(isValidVar(url), this::openInNewTab,c.getString(R.string.open_in_tab),c,linearLayout);
        addAction(isValidVar(url), this::openInNewIncognitoTab,c.getString(R.string.open_in_private_tab),c,linearLayout);
        addAction(isValidVar(url), this::downloadLink,c.getString(R.string.download_link),c,linearLayout);
        addAction(isValidVar(url), this::shareLink,c.getString(R.string.share_link),c,linearLayout);
        dialog.setContentView(v);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        return true;
    }

    /**
     * if we don't have anything to show to user
     * when they long click, then this is a useless dialog
     * therefore, we need to look for smart search or something else
     * @return true if all the elements of a dialog are null
     */
    private boolean uselessDialog() {
        return this.url == null && this.title == null && this.src == null;
    }


    private void hitTestResult(Context context) {
        new MoHitTestResult()
                .of(this.webView)
                .onResult((src, url, title) -> {
                    this.src = src;
                    this.url = url;
                    this.title = title;
                    if(!createDialog(context)){
                        // try smart search
                        smartTextSearch(context);
                    }
                })
                .request();
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
        MoClipboardUtils.add(context, url,"Link Address");
        dismissDialog();
    }

    // opens this url in new tab
    private void openInNewTab(){
        MoTabsManager.addTab(context, url,true);
        dismissDialog();
    }

    private void openInNewIncognitoTab(){
        MoTabsManager.addPrivateTab((Activity) context, url, true);
        dismissDialog();
    }

    private void downloadLink(){
        MoDownloadListener.download(context, url);
        dismissDialog();
    }

    private void shareLink() {
        new MoShare().setText(this.url).shareText(this.context);
        dismissDialog();
    }

    // shares the current bitmap with other apps
    private void shareImage() {
        Uri uri = MoFileProvider.getImageUri(this.context,this.bitmap,
                "mo_images",(title==null?"image":title)+".png",
                MoWebManifest.FILE_PROVIDER_AUTHORITY);
        new MoShare().setType(MoShareUtils.TYPE_PNG_IMAGE)
                .addImage(uri).shareImages(this.context);
    }

    // quits the dialog
    private void dismissDialog(){
        dialog.dismiss();
    }

    public static boolean isValidVar(String input){
        return input != null && !input.isEmpty();
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
