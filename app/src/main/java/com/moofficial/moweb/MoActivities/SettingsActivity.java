package com.moofficial.moweb.MoActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.moofficial.moessentials.MoEssentials.MoKeyGuard.MoKeyGuard;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.MoActivities.History.SavedPasswordsActivity;
import com.moofficial.moweb.MoSettingsEssentials.MoTheme.MoTheme;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoWebFeatures.MoWebFeatures;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebUtils;
import com.moofficial.moweb.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton back;


    public static void launch(Context c) {
        c.startActivity(new Intent(c, SettingsActivity.class));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment(this))
                .commit();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        back = findViewById(R.id.setting_back_button);
        back.setOnClickListener(view -> finish());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MoLog.print("secure something " + resultCode);
        if (requestCode == MoKeyGuard.AUTHENTICATION_REQUEST_CODE && resultCode == RESULT_OK) {
            // they have been authenticated
            startActivity(new Intent(this, SavedPasswordsActivity.class));
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{


        private Activity activity;
        private Preference homePagePref;
        private Preference savePasswordsPref;
        private Preference setAsDefaultPref;

        public SettingsFragment(Activity a){
            this.activity = a;
        }

        public SettingsFragment(){}


        private void init() {
            initHomePagePref();
            initSavePasswordPref();
            this.setAsDefaultPref = findPreference(string(R.string.set_as_default_key));
            if (this.setAsDefaultPref != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    this.setAsDefaultPref.setOnPreferenceClickListener((p) -> {
                        startActivity(new Intent(Settings.ACTION_MANAGE_DEFAULT_APPS_SETTINGS));
                        return false;
                    });
                } else {
                    this.setAsDefaultPref.setEnabled(false);
                }
            }
        }

        private void initHomePagePref() {
            this.homePagePref = findPreference(string(R.string.home_page));
            if (this.homePagePref != null) {
                this.homePagePref.setOnPreferenceClickListener(preference -> {
                    Intent i = new Intent(activity, HomePageActivity.class);
                    activity.startActivity(i);
                    return false;
                });
            }
        }

        private void initSavePasswordPref() {
            this.savePasswordsPref = findPreference(string(R.string.passwords));
            if (this.savePasswordsPref != null) {
                this.savePasswordsPref.setOnPreferenceClickListener(preference -> {
                    MoKeyGuard.authenticateUser(getActivity(),"Authentication",
                            "Please authenticate yourself in order to access the passwords of this device");
                    return false;
                });
            }
        }


        @Override
        public void onResume() {
            super.onResume();
            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onPause() {
            super.onPause();
            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            this.activity = getActivity();
            init();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
            if(s.equals(string(R.string.search_engine))){
                MoSearchEngine.updateSearchEngine(activity);
            }else if(s.equals(string(R.string.theme_version))){
                MoTheme.updateTheme(activity);
                requireActivity().recreate();
            }else if(s.equals(string(R.string.auto_complete))){
                MoSearchAutoComplete.updateSearchAutoComplete(activity);
            }else if(s.equals(string(R.string.history_enabled))){
                MoHistoryManager.updateSharedPref(activity);
            } else if (s.equals(string(R.string.cookies_enabled))) {
                MoWebUtils.updateCookies(activity);
                MoWebUtils.updateThirdPartyCookies(activity);
            } else if (s.equals(string(R.string.snap_search_enabled))) {
                MoWebFeatures.updateSnapSearch(activity);
            } else if (s.equals(string(R.string.one_hand_web_enabled))) {
                MoWebFeatures.updateOneHand(activity);
            }
        }





        private String string(int id){
            return activity.getString(id);
        }


    }
}