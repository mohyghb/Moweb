package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInflatorView.MoInflaterView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerAdapters.MoRecyclerAdapter;
import com.moofficial.moweb.R;

import java.util.List;

public class MoSuggestionsAdapter extends MoRecyclerAdapter<MoSuggestionsAdapter.SuggestionViewHolder,
        MoSuggestion> {



    private MoRunnable onSuggestionClicked;


    public static class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private TextView suggestion;
        private CardView cardView;

        public SuggestionViewHolder(View v) {
            super(v);
            suggestion = v.findViewById(R.id.suggestion_text_view);
            cardView = v.findViewById(R.id.suggestion_button);
        }

        public void removeView(){
            ((ViewGroup) itemView).removeAllViews();
        }


    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MoSuggestionsAdapter(List<MoSuggestion> myDataset, Context c, MoRunnable runnable) {
        super(c,myDataset);

        this.onSuggestionClicked = runnable;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MoSuggestionsAdapter.SuggestionViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = MoInflaterView.inflate(R.layout.suggestion_layout,parent.getContext());
        return new MoSuggestionsAdapter.SuggestionViewHolder(v);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(MoSuggestionsAdapter.SuggestionViewHolder holder, int position) {
        holder.suggestion.setText(dataSet.get(position).getKey() + " " + dataSet.get(position).getSimilarity());
        holder.cardView.setOnClickListener(view -> {
            if(onSuggestionClicked!=null)
                onSuggestionClicked.run(dataSet.get(position));
        });
    }
}
