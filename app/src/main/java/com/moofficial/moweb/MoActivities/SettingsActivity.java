package com.moofficial.moweb.MoActivities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.MoSettingsEssentials.MoTheme.MoTheme;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.R;

public class SettingsActivity extends AppCompatActivity {

    private ImageButton back;

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

    public static class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener{


        private Activity activity;
        private Preference homePagePref;

        public SettingsFragment(Activity a){
            this.activity = a;
        }

        public SettingsFragment(){}


        private void init(){
            initHomePagePref();
        }

        private void initHomePagePref() {
            this.homePagePref = findPreference(activity.getString(R.string.home_page));
            if (this.homePagePref != null) {
                this.homePagePref.setOnPreferenceClickListener(preference -> {
                    Intent i = new Intent(activity, HomePageActivity.class);
                    activity.startActivity(i);
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
            MoSharedPref.loadAll(activity);
            if(s.equals(string(R.string.search_engine))){
                MoSearchEngine.updateSearchEngine(activity);
            }else if(s.equals(string(R.string.theme_version))){
                MoTheme.updateTheme(activity);
            }else if(s.equals(string(R.string.auto_complete))){
                MoSearchAutoComplete.updateSearchAutoComplete(activity);
            }else if(s.equals(string(R.string.history_enabled))){
                MoHistoryManager.updateSharedPref(activity);
            }
        }





        private String string(int id){
            return activity.getString(id);
        }


    }
}