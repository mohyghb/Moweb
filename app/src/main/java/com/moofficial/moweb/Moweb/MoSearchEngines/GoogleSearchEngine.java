package com.moofficial.moweb.Moweb.MoSearchEngines;

import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;

public class GoogleSearchEngine extends MoSearchEngine {

    private static final String SEARCH_SUGGESTIONS = "http://google.com/complete/search?output=toolbar&q=";

    public static final String GOOGLE_ADDRESS = "https://www.google.com";
    private static final String GOOGLE_SEARCH = "https://www.google.com/search?q=";
    public static final String GOOGLE_SEARCH_COMMAND = "search?";
    private static final String SPLITTER = "suggestion data=";

    public GoogleSearchEngine() {
        super(GOOGLE_SEARCH,GOOGLE_ADDRESS,SEARCH_SUGGESTIONS);
    }


    @Override
    protected MoSuggestions getSuggestions(String html, MoSuggestions suggestions) {
        String[] ls = html.split(SPLITTER);
        for(int i = 1; i < ls.length; i++){
            // second index of \"
            int index = ls[i].indexOf("\"", 1);
            if(index>1){
                suggestions.add(ls[i].substring(1,index));
            }
        }
        return suggestions;
    }
}
