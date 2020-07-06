package com.moofficial.moweb.Moweb.MoHistory;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moweb.R;

public class MoHistoryHolder extends RecyclerView.ViewHolder {

    // history
    TextView urlTextView,dateTimeTextView,titleTextView,firstLetter;

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
            case MoHistory.TYPE_DATE_TILE:
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
        firstLetter = itemView.findViewById(R.id.first_letter_text);
    }


    private void initDateTile(){
        date = itemView.findViewById(R.id.date_tile);
    }


}
