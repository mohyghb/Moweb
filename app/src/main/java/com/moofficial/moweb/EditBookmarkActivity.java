package com.moofficial.moweb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoOriginalActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoWindow.MoSoftInputBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoEditTextBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoAcceptDenyLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoInputBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoButton;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkUtils;

public class EditBookmarkActivity extends MoOriginalActivity {

    public static final String EXTRA_URL = "extra_url";
    public static final String EXTRA_NAME = "extra_name";
    private final int CHOOSE_FOLDER_REQUEST_CODE = 2;

    private MoToolBar moToolBar;
    private MoBookmark editBookmark;
    private MoInputBar titleInput,urlInput;
    private MoButton folderButton;
    private MoAcceptDenyLayout acceptDenyLayout;
    private String originalKey;
    private String newFolderName;

    @Override
    protected void init() {
        initOriginalKey();
        initUI();
        initClass();
    }

    private void initOriginalKey() {
        Bundle b = getIntent().getExtras();
        this.originalKey = b.getString(EXTRA_URL);
        if(originalKey == null){
            this.originalKey  =b.getString(EXTRA_NAME);
            editBookmark = MoBookmarkManager.getFolder(this.originalKey);
        }else{
            editBookmark = MoBookmarkManager.getBookmark(this.originalKey);
        }
    }

    private void initUI() {
        new MoSoftInputBuilder(this).adjustResizeSoftInput().build();
        initToolbar();
        initNestedLinearLayout();
        initAcceptDeny();

        setTitle(getString(R.string.edit_title));
        floatingActionButton.hide();
        makeActivityRound();
        syncTitle(moToolBar.getTitle());
    }

    private void initNestedLinearLayout() {
        initEditName();
        initEditUrl();
        initFolder();

        // adding the content to nested linear layout
        linearNested.addView(titleInput, MoMarginBuilder.getLinearParams(8));
        linearNested.addView(folderButton, MoMarginBuilder.getLinearParams(0,8,0,0));

    }

    private void initEditName() {
        titleInput = new MoInputBar(this)
                .setHint(R.string.name_hint)
                .setTitle(R.string.name)
                .setText(editBookmark.getName());
        titleInput.getCardView().makeCardRectangular();
        if(editBookmark.isFolder()){
            // we need to add a text watcher so
            // that it tells them if this is not acceptable
            titleInput.customize(new MoEditTextBuilder(this).setWatcher(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if(MoBookmarkManager.hasFolder(charSequence.toString())){
                        Toast.makeText(EditBookmarkActivity.this,"has this",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            }),titleInput.EDId());
        }
    }

    private void initEditUrl() {
        urlInput = new MoInputBar(this);
        if(!editBookmark.isFolder()){
            urlInput.setTitle(getString(R.string.url))
                    .setHint(R.string.url_hint)
                    .setText(editBookmark.getUrl()).getCardView().makeCardRectangular();
            linearNested.addView(urlInput, MoMarginBuilder.getLinearParams(8));
        }
    }

    private void initFolder() {
        newFolderName = MoBookmarkUtils.getFolderName(this,editBookmark);
        folderButton = new MoButton(this)
                .setTitle("Parent folder")
                .setIcon(R.drawable.ic_baseline_folder_24)
                .setDescription(newFolderName)
                .setOnButtonClickListener(view -> {
                    BookmarkFolderChooserActivity.startActivityForResult(EditBookmarkActivity.this,
                            CHOOSE_FOLDER_REQUEST_CODE,
                            this.editBookmark);
                });
    }

    private void initAcceptDeny(){
        this.acceptDenyLayout = new MoAcceptDenyLayout(this);
        this.acceptDenyLayout.setAcceptButtonText(R.string.save)
                             .setOnAcceptClickedListener(view -> {
                                 onSavePressed();
                             }).setOnDenyClickedListener(view -> {
                                setResultCanceled();
                                finish();
                             }).getCardView().makeTransparent()
        ;
        linearBottom.addView(this.acceptDenyLayout);
    }

    /**
     * if we can validate all the fields, we save it
     * otherwise we show them the error
     */
    private void onSavePressed() {
        if(MoBookmarkManager.validateEditInputs(this,editBookmark,titleInput.getEditText(),
                urlInput.getEditText(),originalKey)){
            setResultOkay();
            MoBookmarkManager.editBookmark(editBookmark,originalKey,
                    titleInput.getInputText(),
                    editBookmark.isFolder()?"":urlInput.getInputText(),
                    newFolderName);
            finish();
        }
    }


    private void initToolbar(){
        this.moToolBar = new MoToolBar(this).onlyTitleAndLeftButtonVisible();
        moToolBar.getCardView().makeTransparent();
        toolbar.addToolbar(moToolBar);
    }

    private void initClass(){}




    private void setResultCanceled(){
        setResult(RESULT_CANCELED);
    }

    private void setResultOkay(){
        setResult(RESULT_OK);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case CHOOSE_FOLDER_REQUEST_CODE:
                if(resultCode == RESULT_OK){
                    if (data != null) {
                        //noinspection ConstantConditions
                        this.newFolderName = data.getExtras().
                                getString(BookmarkFolderChooserActivity.CHOSEN_FOLDER_TAG);
                        Toast.makeText(this,"Folder was changed to "
                                +newFolderName,Toast.LENGTH_SHORT).show();
                        folderButton.setDescription(this.newFolderName);
                    }
                }
                break;
        }
    }


//    public static void startActivity(Context c,MoBookmark b){
//        Intent i = new Intent(c,EditBookmarkActivity.class);
//        i.putExtra(EXTRA_URL,b.getUrl());
//        c.startActivity(i);
//    }

    public static void startActivityForResult(Activity a, MoBookmark b,int code){
        Intent i = new Intent(a,EditBookmarkActivity.class);
        if(b.isFolder()){
            i.putExtra(EXTRA_NAME,b.getName());
        }else{
            i.putExtra(EXTRA_URL,b.getUrl());
        }
        a.startActivityForResult(i,code);
    }
}