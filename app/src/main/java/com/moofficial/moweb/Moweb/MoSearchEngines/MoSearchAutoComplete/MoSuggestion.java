package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import androidx.annotation.IntDef;

import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistory;

// either suggestion is made from their
// history or from search engines suggestion
// api
public class MoSuggestion {

    @IntDef({TYPE_HISTORY,TYPE_SEARCH_ENGINE})
    public @interface SuggestionType{}
    public static final int TYPE_HISTORY = 0;
    public static final int TYPE_SEARCH_ENGINE = 1;

    private float similarity;
    @SuggestionType private int type;
    private MoHistory history;
    private String searchEngineSuggestion;

    public MoSuggestion(MoHistory history,float similarity){
        this.history = history;
        this.type = TYPE_HISTORY;
        this.similarity = similarity;
    }

    public MoSuggestion(String searchEngineSuggestion,float similarity) {
        this.searchEngineSuggestion = searchEngineSuggestion;
        this.type = TYPE_SEARCH_ENGINE;
        this.similarity = similarity;
    }

    public float getSimilarity() {
        return similarity;
    }

    public int getType() {
        return type;
    }

    public MoHistory getHistory() {
        return history;
    }

    public String getSearchEngineSuggestion() {
        return searchEngineSuggestion;
    }

    // key to search the suggestion inside a hash map
    public String getKey() {
        if(type == TYPE_HISTORY) {
            return history.getTitle();
        } else {
            return searchEngineSuggestion;
        }
    }
}
