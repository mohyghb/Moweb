package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import java.util.ArrayList;
import java.util.List;

public class MoSuggestions {



    private List<String> suggestions;


    public MoSuggestions(List<String> suggestions){
        this.suggestions = suggestions;
    }

    public MoSuggestions(){
        this.suggestions = new ArrayList<>();
    }



    public List<String> getSuggestions() {
        return suggestions;
    }

    public void add(String s){
        this.suggestions.add(s);
    }


    public boolean isEmpty(){
        return this.suggestions.isEmpty();
    }

}
