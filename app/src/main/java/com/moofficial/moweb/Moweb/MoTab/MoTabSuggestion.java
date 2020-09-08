package com.moofficial.moweb.Moweb.MoTab;

import android.content.Context;
import android.view.View;

import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestionsAdapter;

import java.util.ArrayList;

public class MoTabSuggestion {

    private MoSuggestionsAdapter adapter;
    private MoCardRecyclerView moCardRecyclerView;
    private MoRecyclerView recyclerView;

    // list of suggestions that get updated
    private MoSuggestions suggestions;
    private Context context;
    private MoRunnable onSuggestionClicked;

    public MoTabSuggestion(Context context, MoCardRecyclerView v){
        this.context = context;
        this.moCardRecyclerView = v;
    }

    public MoTabSuggestion init(){
        moCardRecyclerView.getCardView().makeTransparent();
        adapter = new MoSuggestionsAdapter(new ArrayList<>(),context,this.onSuggestionClicked);
        recyclerView = MoRecyclerUtils.get(moCardRecyclerView.getRecyclerView(),adapter)
                            .setOrientation(MoRecyclerView.HORIZONTAL)
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
        moCardRecyclerView.setVisibility(View.VISIBLE);
    }




    public void hide(){
        this.moCardRecyclerView.setVisibility(View.GONE);
    }




}
