package com.moofficial.moweb.Moweb.MoBookmark;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moweb.R;

public class MoBookmarkViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView url,title;
    LinearLayout coverLayout;

    public MoBookmarkViewHolder(@NonNull View v) {
        super(v);
        this.cardView = v.findViewById(R.id.bookmark_card_view);
        this.url = v.findViewById(R.id.url_bookmark);
        this.title = v.findViewById(R.id.title_bookmark);
        this.coverLayout = v.findViewById(R.id.cover_view);
    }
}
