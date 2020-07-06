package com.moofficial.moweb.Moweb.MoGoogle;

import android.os.AsyncTask;

import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;

public class MoGoogle extends AsyncTask<Void,Void,Void> {

    private static final String SEARCH_SUGGESTIONS = "http://google.com/complete/search?output=toolbar&q=";

    public static final String GOOGLE_ADDRESS = "https://www.google.com";
    private static final String GOOGLE_SEARCH = "https://www.google.com/search?q=";
    public static final String GOOGLE_SEARCH_COMMAND = "search?";


    private MoSuggestions suggestions;
    private String url;
    private Runnable onFinished;
    private Runnable onError;


//    /**
//     * for getting suggestions
//     * @param search
//     */
//    public MoGoogle getSuggestions(String search,Runnable onSuggestionsReceived){
//        this.url = SEARCH_SUGGESTIONS + search;
//        this.onFinished = onSuggestionsReceived;
//        return this;
//    }

    @Override
    protected Void doInBackground(Void... voids) {

//        try {
//            String data = MoHtml.getHtml(this.url);
//            suggestions = new MoSuggestions(data);
//        } catch (IOException e) {
//            // error
//            somethingWentWrong();
//        }

        return null;
    }


    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if(this.onFinished!=null)
            onFinished.run();
    }

    public MoSuggestions getSuggestions() {
        return suggestions;
    }

//    private void somethingWentWrong(){
//        if(this.onError!=null)
//            this.onError.run();
//    }
//
//
//    /**
//     * makes the search using the google urls
//     * @param s thing to be searched
//     * @return
//     */
//    public static String googlifyUrl(String s){
//        if(MoUrlUtils.isUrl(s)){
//            return s;
//        }
//        return GOOGLE_SEARCH + s;
//    }



}
