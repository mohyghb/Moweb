package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestionsAdapter;
import com.moofficial.moweb.R;

public class MoTabSuggestion {

    private MoSuggestionsAdapter adapter;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    // list of suggestions that get updated
    private MoSuggestions suggestions;
    private Context context;
    private View view;
    private MoRunnable onSuggestionClicked;

    public MoTabSuggestion(Context context, View v){
        this.recyclerView = v.findViewById(R.id.suggestion_recycler_view);
        this.context = context;
        this.view = v;
    }


    public MoTabSuggestion setOnSuggestionClicked(MoRunnable runnable){
        onSuggestionClicked = runnable;
        return this;
    }


    /**
     * shows the suggestions that are inside MoSuggestions class
     * @param suggestions
     */
    public void show(MoSuggestions suggestions){
        if(suggestions==null || suggestions.isEmpty()) {
            hide();
            return;
        }
        this.suggestions = suggestions;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new MoSuggestionsAdapter(suggestions.getSuggestions(),context,this.onSuggestionClicked);
        recyclerView.setAdapter(adapter);
        recyclerView.setVisibility(View.VISIBLE);
    }


    public void hide(){
        this.recyclerView.setVisibility(View.GONE);
    }




}
