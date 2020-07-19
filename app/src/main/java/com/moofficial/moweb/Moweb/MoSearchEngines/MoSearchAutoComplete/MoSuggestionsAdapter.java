package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moweb.MoInflatorView.MoInflaterView;

import com.moofficial.moweb.R;

import java.util.List;

public class MoSuggestionsAdapter extends RecyclerView.Adapter<MoSuggestionsAdapter.SuggestionViewHolder> {



    private List<String> suggestions;
    private Context context;
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
    public MoSuggestionsAdapter(List<String> myDataset, Context c, MoRunnable runnable) {
        this.suggestions = myDataset;
        this.context = c;
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
        holder.suggestion.setText(suggestions.get(position));
        holder.cardView.setOnClickListener(view -> {
            if(onSuggestionClicked!=null)
                onSuggestionClicked.run(suggestions.get(position));
        });
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return suggestions.size();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }







}
