package com.moofficial.moweb.Moweb.MoWebview.MoHistory;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoLogo;
import com.moofficial.moweb.R;

public class MoHistoryHolder extends RecyclerView.ViewHolder {

    // history
    TextView urlTextView,dateTimeTextView,titleTextView;
    MoLogo moImageTextLogo;
    CardView cardView;
    LinearLayout linearLayout;

    // date tile
    TextView date;

    int type;

    public MoHistoryHolder(@NonNull View itemView,int type) {
        super(itemView);
        this.type = type;
        init();
    }

    private void init(){
        switch (type){
            case MoHistory.TYPE_DATE:
                initDateTile();
                break;
            case MoHistory.TYPE_HISTORY:
                initHistory();
                break;
        }
    }

    private void initHistory(){
        urlTextView = itemView.findViewById(R.id.history_url);
        dateTimeTextView = itemView.findViewById(R.id.history_date_time);
        titleTextView = itemView.findViewById(R.id.history_title);
        moImageTextLogo = itemView.findViewById(R.id.image_text_logo);
        cardView = itemView.findViewById(R.id.history_tile_card_view);
        linearLayout = itemView.findViewById(R.id.history_tile_layout);
    }


    private void initDateTile(){
        date = itemView.findViewById(R.id.date_tile);
    }




}
