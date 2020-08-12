package com.moofficial.moweb;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;

public class MoTabActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(MoTabController.instance.getCurrent().getZeroPaddingView());
    }


    @Override
    public void onBackPressed() {
        // go back inside the tab, if the tab can not go back anymore, finish the activity
        MoTabController.instance.getCurrent().onBackPressed(this::finishAffinity);
    }

    public static void startActivity(Context c){
        Intent i = new Intent(c, MoTabActivity.class);
        c.startActivity(i);
    }

}