package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.view.View;

import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestionsAdapter;

import java.util.ArrayList;

public class MoTabSuggestion {

    private MoSuggestionsAdapter adapter;
    private MoRecyclerView recyclerView;

    // list of suggestions that get updated
    private MoSuggestions suggestions;
    private Context context;
    private MoRunnable onSuggestionClicked;

    public MoTabSuggestion(Context context, MoRecyclerView v){
        this.context = context;
        this.recyclerView = v;
    }

    public MoTabSuggestion init() {
        adapter = new MoSuggestionsAdapter(new ArrayList<>(),context,this.onSuggestionClicked);
        recyclerView = MoRecyclerUtils.get(recyclerView,adapter)
                            .show();
        return this;
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
        adapter.setDataSet(suggestions.getSuggestions());
        adapter.notifyDataSetChanged();
        recyclerView.setVisibility(View.VISIBLE);
    }




    public void hide() {
        this.recyclerView.setVisibility(View.GONE);
    }




}
