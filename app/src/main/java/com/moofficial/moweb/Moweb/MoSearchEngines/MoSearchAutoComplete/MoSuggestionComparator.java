package com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete;

import java.util.Comparator;

public class MoSuggestionComparator implements Comparator<MoSuggestion> {
    @Override
    public int compare(MoSuggestion moSuggestion, MoSuggestion t1) {
        float sim1 = moSuggestion.getSimilarity();
        float sim2 = t1.getSimilarity();
        if (sim1 == sim2) {
            return 0;
        } else if (sim1 > sim2) {
            return -1;
        } else {
            return 1;
        }
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }
}
