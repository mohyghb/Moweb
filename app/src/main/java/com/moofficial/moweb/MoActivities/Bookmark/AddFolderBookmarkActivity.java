package com.moofficial.moweb.MoActivities.Bookmark;

import android.app.Activity;
import android.content.Intent;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoAcceptDenyLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoButton;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoEditText.MoEditText;
import com.moofficial.moweb.Moweb.MoBookmark.MoBookmarkManager;
import com.moofficial.moweb.R;

import java.util.Objects;

public class AddFolderBookmarkActivity extends MoSmartActivity {

    private static final int CHOOSE_FOLDER_REQUEST_CODE = 1;
    public static final String EXTRA_FOLDER_NAME = "exfolname";
    public static final String EXTRA_PARENT_FOLDER = "parentfolder";

    private MoButton folderButton;
    private MoEditText editText;
    private MoToolBar moToolBar;
    private MoAcceptDenyLayout acceptDenyLayout;
    private String folderParent;


    @Override
    protected void init() {
        setTitle(R.string.new_folder);
        setSubTitle(R.string.new_folder_message);
        initEditText();
        initFolderButton();
        initMoToolBar();
        initAcceptDenyLayout();
    }

    private void initFolderButton() {
        folderParent = getExtraParentFolder(getIntent());
        folderButton = new MoButton(this);
        this.folderButton.setTitle("Parent folder")
                .setIcon(R.drawable.ic_baseline_folder_open_24)
                .setDescription(folderParent)
                .setOnButtonClickListener(view -> BookmarkFolderChooserActivity.startActivityForResult(this,
                        CHOOSE_FOLDER_REQUEST_CODE))
                .getCardView().makeCardRecRound();
        l.linearNested.addView(folderButton, MoMarginBuilder.getLinearParams(this, 8));
    }

    private void initEditText() {
        this.editText = new MoEditText(this).setHint(R.string.add_folder_in_bookmark_hint_dialog).actionDone()
                .setOnEditorActionListener((textView, i, keyEvent) -> {
                    if (i == EditorInfo.IME_ACTION_DONE) {
                        createFolder();
                    }
                    return false;
                });
        l.linearNested.addView(this.editText, MoMarginBuilder.getLinearParams(this, 8));
    }

    private void initMoToolBar() {
        this.moToolBar = new MoToolBar(this);
        this.moToolBar.onlyTitleAndLeftButtonVisible()
                .setLeftOnClickListener(view -> onBackPressed());
        l.toolbar.addToolbar(this.moToolBar);
        syncTitle(moToolBar.getTitle());
    }

    private void initAcceptDenyLayout() {
        this.acceptDenyLayout = new MoAcceptDenyLayout(this);
        this.acceptDenyLayout.setAcceptButtonText(R.string.create)
                .setOnAcceptClickedListener(view -> createFolder())
                .setDenyButtonText(R.string.cancel)
                .setOnDenyClickedListener(view -> finish())
                .getCardView().makeTransparent();
        l.linearBottom.addView(this.acceptDenyLayout);
    }

    private void createFolder() {
        String name = editText.getInputText().trim();
        MoBookmarkManager.createFolder(this, name,
                MoBookmarkManager.getFolder(this.folderParent), editText, () -> {
                    Toast.makeText(this,
                            String.format("Folder %s was created!", name), Toast.LENGTH_SHORT).show();
                    setResultAndFinish(name);
                });
    }

    /**
     * pass the name of the created folder as
     * data in case it is waiting for on result activity
     * @param name of the newly created folder
     */
    private void setResultAndFinish(String name) {
        Intent data = new Intent();
        data.putExtra(EXTRA_FOLDER_NAME, name);
        setResult(RESULT_OK, data);
        finish();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == CHOOSE_FOLDER_REQUEST_CODE && data != null) {
            folderParent = BookmarkFolderChooserActivity.getChosenFolder(data.getExtras());
            folderButton.setDescription(folderParent);
        }
    }


    public static void launch(Activity a, String parent, int requestCode) {
        Intent intent = new Intent(a, AddFolderBookmarkActivity.class);
        intent.putExtra(EXTRA_PARENT_FOLDER, parent);
        a.startActivityForResult(intent, requestCode);
    }

    public static String getExtraFolderName(Intent data) {
        return Objects.requireNonNull(data.getExtras()).getString(EXTRA_FOLDER_NAME);
    }

    public static String getExtraParentFolder(Intent data) {
        return Objects.requireNonNull(data.getExtras()).getString(EXTRA_PARENT_FOLDER);
    }

}