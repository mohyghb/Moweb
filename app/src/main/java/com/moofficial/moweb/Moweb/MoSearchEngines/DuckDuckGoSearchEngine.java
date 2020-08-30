package com.moofficial.moweb.Moweb.MoSearchEngines;

import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestion;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;

import org.json.JSONArray;
import org.json.JSONException;

public class DuckDuckGoSearchEngine extends MoSearchEngine {

    private static final String DUCK_DUCK_GO_ADDRESS = "https://duckduckgo.com";
    private static final String DUCK_DUCK_GO_SEARCH = "https://duckduckgo.com/?q=";
    private static final String DUCK_DUCK_GO_AUTO_COMPLETE = "https://duckduckgo.com/ac/?q=";
    private static final String PHRASE = "phrase";

    private static final String DARK_THEME = "kae = d";

    public DuckDuckGoSearchEngine() {
        super(DUCK_DUCK_GO_SEARCH, DUCK_DUCK_GO_ADDRESS, DUCK_DUCK_GO_AUTO_COMPLETE);
    }


    @Override
    protected MoSuggestions getSuggestions(MoSuggestions suggestions,String html) {
        try {
            JSONArray jsonArray = new JSONArray(html);
            for(int i = 0;i < jsonArray.length(); i++){
                String suggestion = jsonArray.getJSONObject(i).getString(PHRASE);
                float similarity = MoString.getSimilarity(suggestion,suggestions.getSearch(),true);
                suggestions.add(new MoSuggestion(suggestion,similarity));
            }
        } catch (JSONException ignore) {}
        return suggestions;
    }
}
