package com.moofficial.moweb.MoActivities.Download;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moweb.R;

public class MoDownloadSettingsActivity extends MoSmartActivity {

    private static final int REQUEST_CODE_FILE_CHOOSER = 1;

    @Override
    protected void init() {
        openFileSearch();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FILE_CHOOSER) {

        }
    }

    public void openFileSearch() {
        Intent chooseFile;
        Intent intent;
        chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("file/*");
        intent = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER);
    }

}