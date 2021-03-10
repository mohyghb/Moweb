package com.moofficial.moweb.Moweb.MoBookmark;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoLogo;
import com.moofficial.moweb.R;

public class MoBookmarkViewHolder extends RecyclerView.ViewHolder {

    CardView cardView;
    TextView url,title;
    MoLogo imageTextLogo;
    ConstraintLayout layout;

    public MoBookmarkViewHolder(@NonNull View v) {
        super(v);
        this.cardView = v.findViewById(R.id.bookmark_card_view);
        this.url = v.findViewById(R.id.url_bookmark);
        this.title = v.findViewById(R.id.title_bookmark);
        this.imageTextLogo = v.findViewById(R.id.include_logo);
        this.layout = v.findViewById(R.id.book_mark_layout_constraint);
    }
}
