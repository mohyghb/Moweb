package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MoSuggestions {


    // does not prevent the add method from adding
    // suggestions to the list
    private int limit = 5;
    private ArrayList<MoSuggestion> suggestions = new ArrayList<>();
    private HashSet<String> duplicates = new HashSet<>();
    // what they are searching
    private String search;


    public MoSuggestions(String search) {
        this.search = search;
    }


    public List<MoSuggestion> getSuggestions() {
        return new ArrayList<>(suggestions);
    }

    public MoSuggestions setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    /**
     * adds the suggestion s to the list
     * of suggestions if it is not a duplicate
     *
     * @param s suggestion to be added
     * @return true if we have to stop adding
     * since the suggestion list has reached to the limit
     */
    public boolean add(MoSuggestion s) {
        if (reachedLimit())
            return true;
        String key = s.getKey();
        if (!duplicates.contains(key)) {
            this.suggestions.add(s);
            this.duplicates.add(key);
        }
        return reachedLimit();
    }

    /**
     * adds all the suggestions inside the
     * suggestions object to this suggestions
     *
     * @param suggestions to be added
     */
    public void addAll(MoSuggestions suggestions) {
        for (MoSuggestion s : suggestions.suggestions) {
            boolean b = add(s);
            if (b) {
                break;
            }
        }
    }

    /**
     * sorts the suggestions based on the
     * similarity
     */
    public void sortBySimilarityToSearch() {
        Collections.sort(this.suggestions, new MoSuggestionComparator());
    }

    // monote trim the suggestions
    //  and make history suggestions more reliable by
    //  factoring the time in (time they accessed it matters)

    /**
     * @return whether we have added enough suggestions
     * to have equal or higher than the limit size
     */
    public boolean reachedLimit() {
        return suggestions.size() >= limit;
    }

    /**
     * returns true if we already have this
     * suggestion in our list
     *
     * @param suggestion to check whether we have it or not
     * @return true if the suggestion is a duplicate
     */
    public boolean has(String suggestion) {
        return duplicates.contains(suggestion);
    }


    public boolean isEmpty() {
        return this.suggestions.isEmpty();
    }

    public String getSearch() {
        return search;
    }
}
