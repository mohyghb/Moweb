package com.moofficial.moweb.Moweb.MoWebview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.transition.TransitionManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.moofficial.moessentials.MoEssentials.MoClipboard.MoClipboardUtils;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoFileProvider.MoFileProvider;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShare;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShareUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMenuBuilder.MoMenuBuilder;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadListener;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebManifest;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoWebview.MoClient.MoWebClient;
import com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult.MoHitTestResult;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

public class MoHitTestResultParser {



    private static final int BOTTOM_SHEET_PEEK_HEIGHT = 200;


    private MoWebView webView;
    private Context context;
    private String selectedText;
    private String url;
    private String title;
    private String src;
    private Bitmap bitmap;
//    private Dialog dialog;
    private MoBottomSheet bottomSheet;



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

        View v = MoInflaterView.inflate(R.layout.popup_menu__webview,c);
        ((TextView)v.findViewById(R.id.title_dialog)).setText(title);
        ((TextView)v.findViewById(R.id.description_dialog)).setText(url);

        ImageView imageView = v.findViewById(R.id.popup_web_view_image);
        if (src!=null && !src.isEmpty()) {
            imageView.setVisibility(View.VISIBLE);
            Glide.with(context).asBitmap().load(this.src).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                    bitmap = resource;
                    TransitionManager.beginDelayedTransition((ViewGroup) v);
                    imageView.setImageBitmap(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {

                }
            });
        } else {
            imageView.setVisibility(View.GONE);
        }

        MoMenuBuilder builder = new MoMenuBuilder(context)
                .textsWith(16);

        addAction(isValidVar(title), this::copyLinkText,c.getString(R.string.copy_link_text),builder);
        addAction(isValidVar(url), this::copyLinkAddress,c.getString(R.string.copy_link_address),builder);
        addAction(isValidVar(url), this::openInNewTab,c.getString(R.string.open_in_tab),builder);
        addAction(isValidVar(url), this::openInNewIncognitoTab,c.getString(R.string.open_in_private_tab),builder);
        addAction(isValidVar(url), this::downloadLink,c.getString(R.string.download_link),builder);
        addAction(isValidVar(url), this::shareLink,c.getString(R.string.share_link),builder);
        addAction(isValidVar(src),MoHitTestResultParser.this::shareImage,
                context.getString(R.string.share_image),builder);
        builder.allWith((v1)->bottomSheet.dismiss()).build();


        this.bottomSheet = new MoBottomSheet(context)
                .addTitle(v)
                .add(MoMarginBuilder.getLinearParams(context,0),builder.asArray())
                .setState(BottomSheetBehavior.STATE_EXPANDED)
                .build()
                .show();

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
                        onTextSelected(context);
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

        WebView web = new WebView(context);
        web.setWebChromeClient(new MoChromeClient(context));
        web.setWebViewClient(new MoWebClient());
        web.loadUrl(MoSearchEngine.instance.getURL(selectedText));

        TextView tv = new TextView(context);
        tv.setText(selectedText);

        new MoBottomSheet(context)
                .setPeekHeight(BOTTOM_SHEET_PEEK_HEIGHT)
                .setDraggable(true)
                .setFitToContents(false)
                .add(web)
                .addTitle(tv)
                .build()
                .show();

        return true;
    }



    /**
     * returns a button if condition is true, with onclick listener and text
     * @param condition
     * @param onClickListener
     * @param text
     * @return
     */
    private void addAction(boolean condition, Runnable onClickListener, String text, MoMenuBuilder b){
        if(!condition)
            return;
        b.text(text,(v)->onClickListener.run());
    }


    // copies the link text in clip board
    private void copyLinkText() {
        MoClipboardUtils.add(context,title,"Link Text");
    }

    // copies the link address
    private void copyLinkAddress(){
        MoClipboardUtils.add(context, url,"Link Address");
    }

    // opens this url in new tab
    private void openInNewTab(){
        MoTabsManager.addTab(context, url,true);
    }

    private void openInNewIncognitoTab(){
        MoTabsManager.addPrivateTab((Activity) context, url, true);
    }

    private void downloadLink(){
        MoDownloadListener.download(context, url);
    }

    private void shareLink() {
        new MoShare().setText(this.url).shareText(this.context);
    }

    // shares the current bitmap with other apps
    private void shareImage() {
        Uri uri = MoFileProvider.getImageUri(this.context,this.bitmap,
                "mo_images",(title==null?"image":title)+".png",
                MoWebManifest.FILE_PROVIDER_AUTHORITY);
        new MoShare()
                .setType(MoShareUtils.TYPE_PNG_IMAGE)
                .addImage(uri)
                .shareImages(this.context);
    }


    public static boolean isValidVar(String input) {
        return input != null && !input.isEmpty();
    }

    /**
     * this method returns the text that is selected in web view
     */
    public void onTextSelected(Context context) {
        webView.post(() -> webView.evaluateJavascript("(function(){return window.getSelection().toString()})()",
                value -> {
                    selectedText = value.replace("\"","");
                    smartTextSearch(context);
                }));
    }






}
