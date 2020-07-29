package com.moofficial.moweb;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoOriginalActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoAcceptDenyLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoInputBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoButton;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmark;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkUtils;

import java.util.Objects;

public class EditBookmarkActivity extends MoOriginalActivity {

    public static final String EXTRA_URL = "extra_url";
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
        this.originalKey = Objects.requireNonNull(getIntent()
                .getExtras()).getString(EXTRA_URL);
        editBookmark = MoBookmarkManager.getBookmark(this.originalKey);
        initUI();
        initClass();
    }

    private void initUI() {
        initToolbar();
        initNestedLinearLayout();
        initAcceptDeny();

        setTitle(getString(R.string.edit_title));
        setSubTitle(editBookmark.getUrl());
        floatingActionButton.hide();
        makeActivityRound();
        syncTitle(moToolBar.getTitle());
    }

    private void initNestedLinearLayout() {
        titleInput = new MoInputBar(this)
                .setTitle(getString(R.string.title))
                .setText(editBookmark.getName());
        urlInput = new MoInputBar(this)
                .setTitle(getString(R.string.url))
                .setText(editBookmark.getUrl());

        initFolder();

        // adding the content to nested linear layout
        linearNested.addView(titleInput, MoMarginBuilder.getLinearParams(new MoMarginBuilder().setTop(8)
                .convertValuesToDp()));
        linearNested.addView(urlInput, MoMarginBuilder.getLinearParams(new MoMarginBuilder().setTop(8)
                .convertValuesToDp()));
        linearNested.addView(folderButton, MoMarginBuilder.getLinearParams(0,8,0,0));

    }

    private void initFolder() {
        newFolderName = MoBookmarkUtils.getFolderName(this,editBookmark);
        folderButton = new MoButton(this)
                .setTitle("Current folder")
                .setIcon(R.drawable.ic_baseline_folder_24)
                .setDescription(newFolderName)
                .setOnButtonClickListener(view -> {
                    BookmarkFolderChooserActivity.startActivityForResult(EditBookmarkActivity.this,
                            CHOOSE_FOLDER_REQUEST_CODE);
                });
    }

    private void initAcceptDeny(){
        this.acceptDenyLayout = new MoAcceptDenyLayout(this);
        this.acceptDenyLayout.setAcceptButtonText(R.string.save)
                             .setOnAcceptClickedListener(view -> {
                                setResultOkay();
                                editBookmark.setName(titleInput.getInputText());
                                editBookmark.setUrl(urlInput.getInputText());
                                 MoBookmarkManager.moveToFolder(MoBookmarkManager.getFolder(newFolderName),
                                         editBookmark);
                                MoBookmarkManager.edit(editBookmark,originalKey);
                                finish();
                             }).setOnDenyClickedListener(view -> {
                                setResultCanceled();
                                finish();
                             });
        linearBottom.addView(this.acceptDenyLayout);
    }

    private void initToolbar(){
        this.moToolBar = new MoToolBar(this).onlyTitleAndLeftButtonVisible();
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


    public static void startActivity(Context c,MoBookmark b){
        Intent i = new Intent(c,EditBookmarkActivity.class);
        i.putExtra(EXTRA_URL,b.getUrl());
        c.startActivity(i);
    }

    public static void startActivityForResult(Activity a, MoBookmark b,int code){
        Intent i = new Intent(a,EditBookmarkActivity.class);
        i.putExtra(EXTRA_URL,b.getUrl());
        a.startActivityForResult(i,code);
    }
}