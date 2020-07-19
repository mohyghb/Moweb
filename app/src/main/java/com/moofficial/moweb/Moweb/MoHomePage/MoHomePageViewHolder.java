package com.moofficial.moweb.Moweb.MoHomePage;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moweb.R;

public class MoHomePageViewHolder extends RecyclerView.ViewHolder {

    RadioButton radioButton;
    TextView urlTextView;
    CardView cardView;
    LinearLayout coverLayout;

    public MoHomePageViewHolder(@NonNull View v) {
        super(v);
        radioButton = v.findViewById(R.id.active_home_page_radio_button);
        urlTextView = v.findViewById(R.id.url_home_page);
        cardView = v.findViewById(R.id.home_page_card_view);
        coverLayout = v.findViewById(R.id.cover_home_page_layout);
    }
}
