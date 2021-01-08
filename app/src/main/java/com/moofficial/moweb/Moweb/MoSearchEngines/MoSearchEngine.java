package com.moofficial.moweb.Moweb.MoSearchEngines;

import android.content.Context;

import com.moofficial.moweb.MoSettingsEssentials.MoSharedPref.MoSharedPref;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.R;

public abstract class MoSearchEngine {


    public static final int GOOGLE = 0;
    public static final int DUCK_DUCK_GO = 1;


    public static MoSearchEngine instance;




    private String autoCompleteURL;
    private String searchURL;
    private String homePage;


    public MoSearchEngine(String surl,String hp,String acurl){
        this.searchURL = surl;
        this.homePage = hp;
        this.autoCompleteURL = acurl;
    }



    /**
     * returns the url combined with url of the
     * search engine
     * @param search
     * @return
     */
    public String getURL(String search){
        if(MoUrlUtils.isUrl(search)) {
            return search;
        }
        return searchEngineURL(search);
    }


    // returns the url of the search engine + search query
    private String searchEngineURL(String search){
        return this.searchURL + search;
    }

    // home page of the search engine
    public String homePage(){
        return this.homePage;
    }


    // the url that returns auto complete for search s
    public String suggestionURL(String s){
        return this.autoCompleteURL + s;
    }


    /**
     * parsing the html that is returned from auto complete of the
     * respective search engine and putting them inside a mo suggestions
     * to show them to the user
     * @param html html of auto complete from search engine
     * @param suggestions suggestions should be added here
     * @return suggestions
     */
    protected abstract MoSuggestions getSuggestions(MoSuggestions suggestions,String html);

    /**
     * checks to see if the html has the correct properties of being
     * parsed then passes it to the respective search engine and
     * returns those results in a mo suggestion
     * @param html to parse for search suggestion
     * @param suggestions populate this class for search suggestions
     * @return suggestions
     */
    public MoSuggestions getSuggestions(String html,MoSuggestions suggestions){
        if(html==null){
            // if no suggestion is returned by the html
            // return empty suggestions
            return suggestions;
        }else{
            return getSuggestions(suggestions,html);
        }
    }


    /**
     * returns a search engine based on the
     * preference of the user set inside the settings
     * if no preference, we return GOOGLE search engine
     * @param c
     * @return
     */
    public static MoSearchEngine getPrefSearchEngine(Context c){
        // get shared preference one
        int searchEngine = Integer.parseInt(MoSharedPref.get(c).getString(c.getString(R.string.search_engine),
                GOOGLE+""));
        switch (searchEngine) {
            case GOOGLE:
                return new GoogleSearchEngine();
            case DUCK_DUCK_GO:
                return new DuckDuckGoSearchEngine();
        }
        return new GoogleSearchEngine();
    }


    public static void updateSearchEngine(Context c){
        instance = getPrefSearchEngine(c);
    }


}
