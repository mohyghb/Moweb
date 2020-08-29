package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class MoSuggestions {

    // does not prevent the add method from adding
    // suggestions to the list
    private int limit = 10;
    private List<String> suggestions = new ArrayList<>();
    private HashSet<String> duplicates = new HashSet<>();


    public MoSuggestions(List<String> suggestions){
        this.suggestions = suggestions;
        this.duplicates = new HashSet<>(suggestions);
    }

    public MoSuggestions(){}



    public List<String> getSuggestions() {
        return suggestions;
    }

    public MoSuggestions setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * adds the suggestion s to the list
     * of suggestions if it is not a duplicate
     * @param s suggestion to be added
     * @return true if we have to stop adding
     * since the suggestion list has reached to the limit
     */
    public boolean add(String s) {
        if(!duplicates.contains(s)) {
            this.suggestions.add(s);
            this.duplicates.add(s);
        }
        return reachedLimit();
    }

    /**
     *
     * @return whether we have added enough suggestions
     * to have equal or higher than the limit size
     */
    public boolean reachedLimit() {
        return suggestions.size() >= limit;
    }

    /**
     * returns true if we already have this
     * suggestion in our list
     * @param suggestion to check whether we have it or not
     * @return true if the suggestion is a duplicate
     */
    public boolean has(String suggestion){
        return duplicates.contains(suggestion);
    }


    public boolean isEmpty(){
        return this.suggestions.isEmpty();
    }

}
