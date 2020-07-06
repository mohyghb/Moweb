package com.moofficial.moweb.Moweb.MoTab.MoTabs;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.google.android.material.textfield.TextInputEditText;
import com.moofficial.moweb.MoBitmap.MoBitmap;
import com.moofficial.moweb.MoBitmap.MoBitmapSaver;
import com.moofficial.moweb.MoHTML.MoHTMLAsyncTask;
import com.moofficial.moweb.MoIO.MoFile;
import com.moofficial.moweb.MoIO.MoLoadable;
import com.moofficial.moweb.MoIO.MoSavable;
import com.moofficial.moweb.MoInflatorView.MoInflaterView;
import com.moofficial.moweb.MoInflatorView.MoViewDisplayable;
import com.moofficial.moweb.MoKeyBoard.MoKeyBoardUtils;
import com.moofficial.moweb.MoRunnable.MoRunnable;
import com.moofficial.moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoClient.MoChromeClient;
import com.moofficial.moweb.Moweb.MoGoogle.MoGoogle;
import com.moofficial.moweb.Moweb.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabExceptions.MoTabNotFoundException;
import com.moofficial.moweb.Moweb.MoTab.MoTabSuggestion;
import com.moofficial.moweb.Moweb.MoTab.MoTabType.MoTabType;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebView;
import com.moofficial.moweb.R;


public class MoTab implements  MoSavable, MoLoadable, MoViewDisplayable {

    private static final String SEP_KEY = "ls&&sfsdfgqauizo2-4241eirhfue&";
    // DATA
    private MoTab parentTab;
    private MoWebView moWebView;
    private String url;
    private MoBitmap moBitmap;
    private boolean isUpToDate;
    private MoTabType tabType;
    //private MoSearchEngine searchEngine;

    // UI
    private Context context;
    private View view;
    private TextInputEditText searchText;
    private View searchBar;
    private CardView showTabsButton;
    private ProgressBar progressBar;
    private View errorLayout;
    private boolean captureImage = true;
    // suggestions
    private MoTabSuggestion suggestion;



    public MoTab(Context context, String url) {
        this.context = context;
        this.url = url;
        init();
        this.moBitmap = new MoBitmap();
        this.moWebView.getWebView().loadUrl(this.url);
        this.isUpToDate = true;
    }

    public MoTab(String searchText,Context context){
        this(context,MoSearchEngine.instance.getURL(searchText));
    }

    // for loading back tabs from saved files
    public MoTab(Context context){
        this.context = context;
        this.isUpToDate = false;
    }

    public MoTab setCaptureImage(boolean b){
        this.captureImage = b;
        return this;
    }

    public void setParentTab(MoTab t){
        this.parentTab = t;
    }


    /**
     * init the whole class
     */
    private void init(){
        this.view = MoInflaterView.inflate(R.layout.tab,this.context);
        this.moWebView = new MoWebView(this,this.context);
        initProgressBar();
        initChromeClient();
        initWebView();
        initSearchText();
        initTabsButton();
        initSearchBar();
        initSuggestion();
        initErrorLayout();
        this.tabType = new MoTabType(MoTabType.TYPE_NORMAL,getWebView());
    }

    private void initSuggestion() {
        this.suggestion = new MoTabSuggestion(this.context,this.view)
                .setOnSuggestionClicked(new MoRunnable() {
                    @Override
                    public <T> void run(T... args) {
                        String suggestion = (String)args[0];
                        if(suggestion != null){
                            search(suggestion);
                        }
                    }
                });
        this.suggestion.hide();
    }

    private void initProgressBar(){
        this.progressBar = this.view.findViewById(R.id.tab_progress);
        this.progressBar.setProgress(0);
        this.progressBar.setMax(100);
    }

    private void initChromeClient(){
        this.moWebView.setChromeClient(new MoChromeClient(this.context)
                .setProgressBar(this.progressBar));
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    private void initWebView(){
        moWebView.init();
    }

    /**
     * search edit text init
     */
    private void initSearchText(){
        //search text
        searchText = this.view.findViewById(R.id.search_bar_text);
        searchText.setText(this.url);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(searchText.hasFocus()){
                    // only show suggestions when user is actually editing it
                    showSuggestions(charSequence);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        searchText.setOnFocusChangeListener((view, b) -> {
            if(!b){
                // hide the suggestions when user is not editing
                suggestion.hide();
            }
        });
        searchText.setOnEditorActionListener((textView, i, keyEvent) -> {
            onSearch(textView,i,keyEvent);
            searchText.clearFocus();
            return false;
        });
    }

    /**
     * shows auto complete text when user is typing
     * their search or URL
     * @param charSequence
     */
    private void showSuggestions(CharSequence charSequence){
        if(MoSearchAutoComplete.enabled){
            // only provide them with search suggestions if they want them
            MoHTMLAsyncTask htmlAsyncTask = new MoHTMLAsyncTask().setUrl(
                    MoSearchEngine.instance.suggestionURL(charSequence.toString()));
            htmlAsyncTask.setOnHtmlReceived(new MoRunnable() {
                @SafeVarargs
                @Override
                public final <T> void run(T... args) {
                    // getting suggestion from search engine
                    MoSuggestions s = MoSearchEngine.instance.getSuggestions((String)args[0]);
                    // add the suggestions from history
                    MoHistoryManager.addSuggestionsFromHistory(charSequence.toString(),s);
                    suggestion.show(s);
                }
            });
            htmlAsyncTask.execute();
        }
    }


    /**
     * init the tab button
     */
    private void initTabsButton() {
        //tabs button
        TextView tabs = view.findViewById(R.id.tabs_button);
        tabs.setText(MoTabsManager.size()+"");

        this.showTabsButton = view.findViewById(R.id.tabs_button_card_view);
        this.showTabsButton.setOnClickListener(view -> {
            MoTabController.instance.onTabsButtonPressed();
        });
    }

    /**
     * init search bar
     */
    private void initSearchBar(){
        this.searchBar = this.view.findViewById(R.id.tab_search_bar);
    }


    private void initErrorLayout(){
        this.errorLayout = this.view.findViewById(R.id.tab_error_layout);
    }


//    private void initSearchEngine(){
//        this.searchEngine = MoSearchEngine.getPrefSearchEngine(this.context);
//    }




    /**
     * when the user presses back on main screen
     * if there is a parent tab, and this web view can not go back, go to
     * that parent tab
     * @param superBackPressed super.onBackPressed in main activity
     */
    public void onBackPressed(Runnable superBackPressed){
        if(this.moWebView.getWebView() != null && this.moWebView.getWebView().canGoBack()){
            this.moWebView.getWebView().goBack();
        }else if(parentTab!=null){
            // we can go back to the parent tab
            try {
                MoTabsManager.selectTab(this.parentTab);
            } catch (MoTabNotFoundException e) {
                e.printStackTrace();
            }
        }
        else{
            superBackPressed.run();
        }
    }


    /**
     * updates the textView of the url
     * as well as the url itself
     * @param u update this.url to u
     */
    public void updateUrl(String u){
        // update url
        this.url = u;
        // remove focus from search text
        this.searchText.clearFocus();
        // set the text for url
        this.searchText.setText(url);
        // hide keyboard
        MoKeyBoardUtils.hideSoftKeyboard(this.searchText);
    }




    /**
     * does a google search and handles all the U.I changes
     * @param s string of search
     */
    private void search(String s) {
        updateUrl(MoSearchEngine.instance.getURL(s));
        // load it inside the web view
        this.moWebView.getWebView().loadUrl(url);
    }

    /**
     * adds the search to search history
     */
    public void addSearchToHistory(String title) {
        MoHistoryManager.add(this.url,title,this.context);
    }



    public WebView getWebView() {
        return moWebView.getWebView();
    }
    public String getUrl() {
        return url;
    }
    public View getView() {
        return view;
    }




    // returns a bitmap of the web view background
    public Bitmap getWebViewBitmap(){
        return this.moBitmap.getBitmap();
    }


    /**
     *
     * @param type must be one of {MoTabType.TYPE_NORMAL,MoTabType.TYPE_INCOGNITO}
     */
    public void setType(int type){
        tabType.setType(type);
    }


    public int getType(){
        return this.tabType.getType();
    }

    public boolean isCaptureImage() {
        return captureImage;
    }

    /**
     * this is for main view (showing the web in main activity)
     * @return a view of web search
     */
    public View getZeroPaddingView(){
        // make the search bar visible
        searchBar.setVisibility(View.VISIBLE);

        this.view.setPadding(0,0,0,0);

        // loading the url if it has not been loaded yet
        if(!this.isUpToDate) {
            this.isUpToDate = true;
            moWebView.getWebView().loadUrl(this.url);
        }

        if(view.getParent() != null) {
            ((ViewGroup)view.getParent()).removeView(view); // <- fix
        }
        return view;
    }


    /**
     * saves the bitmap for this webview
     * @param saver
     */
    public void saveWebViewBitmap(MoBitmapSaver saver){
        saver.setFileName(this.moBitmap.getId().getSId());
        saver.save(this.moBitmap.getBitmap());
    }

    /**
     * deletes the bitmap for this tab
     * @param saver
     */
    public void deleteWebViewBitmap(MoBitmapSaver saver){
        saver.setFileName(this.moBitmap.getId().getSId());
        saver.delete();
    }


    /**
     * returns the website or baseUrl of this tab's url
     */
    public String getBaseUrl(){
        return MoUrlUtils.getBaseUrl(this.url);
    }



    /**
     * loads a savable object into its class
     *
     * @param data
     * @param context
     */
    @Override
    public void load(String data, Context context) {
        String[] c = MoFile.loadable(data);
        this.url = c[0];
        this.moBitmap = new MoBitmap(c[1],context);
        this.init();
    }



    /**
     * @return the data that is going to be saved by the save method
     * inside the class which implements MoSavable
     */
    @Override
    public String getData() {
        return MoFile.getData(this.url,this.moBitmap.getData());
    }

    /**
     * takes args and makes the class which implements it
     * displayable for the user
     * for example this can be used in the U.I
     *
     * @param args arguments that are passed to this method via other classes
     */
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public View display(Object... args) {
        // assume the first element is context
        // assume the second element is a runnable
        Context context = (Context) args[0];
        View v = MoInflaterView.inflate(R.layout.tab,context);
        WebView w = v.findViewById(R.id.tab_web_view);
        w = this.moWebView.getWebView();

        return this.view;
    }


    // when the user presses search button
    private void onSearch(TextView textView,int actionId,KeyEvent event){
        if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) ||
                (actionId == EditorInfo.IME_ACTION_GO) || actionId == EditorInfo.IME_ACTION_DONE) {
            // then we need to search
            this.search(textView.getText().toString());
        }
    }


    // when we receive an error from web view
    public void onErrorReceived(WebResourceRequest request, WebResourceError error){
//        moWebView.setVisibility(View.INVISIBLE);
//        errorLayout.setVisibility(View.VISIBLE);
    }


    public MoBitmap getMoBitmap() {
        return moBitmap;
    }
}
