package com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.textfield.TextInputEditText;
import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moessentials.MoEssentials.MoRunnable.MoWorker.MoWorker;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewGroups.MoConstraint;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoFindBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoCardView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoViewUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopupWindow.MoPopupItemBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoPopupWindow.MoPopupWindow;
import com.moofficial.moweb.MoActivities.BookmarkActivity;
import com.moofficial.moweb.MoActivities.HistoryActivity;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoTab.MoTabSuggestion;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

public class MoTabSearchBar extends MoConstraint {

    private TextView tabsButton;
    private ProgressBar progressBar;
    private ImageButton moreTabButton;
    private TextInputEditText searchText;
    private MoFindBar moFindBar;
    private MoPopupWindow moPopupWindow;
    private MoSearchable moSearchable;
    private MoTabSuggestion suggestion;
    private MoCardView searchBarCardView;

    private MoWebView moWebView;
    private MoTab tab;

    // this is used as the parent layout to be dimmed
    // when the user is typing something
    // to make it look less distracting
    // this should be the layout that contains the web view as well
    // also we run animations on this layout as well
    @NonNull private ViewGroup parentLayout;

    // needed to make sure that the background is dimmed only once
    // and not multiple times when the user is typing
    private MoWorker dimBackgroundWorker = new MoWorker()
            .setTask(() -> MoViewUtils.dim(parentLayout))
            .setUndoTask(()-> MoViewUtils.clearDim(parentLayout));

    public MoTabSearchBar(Context context) {
        super(context);
    }

    public MoTabSearchBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoTabSearchBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public int getLayoutId() {
        return R.layout.tab_search_bar;
    }

    @Override
    public void initViews() {
        initTabSearchBarCard();
        initProgressBar();
        initMoreButton();
        initSearchText();
        initTabsButton();
        initMoSearchable();
        initSuggestion();
    }

    public MoTabSearchBar setParentLayout(ViewGroup parentLayout) {
        this.parentLayout = parentLayout;
        return this;
    }

    private void initTabSearchBarCard() {
        searchBarCardView = findViewById(R.id.tab_search_bar_card_view);
    }

    @Override
    public int[] getAttrs() {
        return new int[0];
    }

    public MoTabSearchBar setTextSearch(String s){
        this.searchText.setText(s);
        return this;
    }

    public MoTabSearchBar setNumberOfTabs(int s){
        if(s>99){
            tabsButton.setText(R.string.a_lot_of_tabs);
            this.tabsButton.setTextSize(12f);
        }else{
            this.tabsButton.setText(String.valueOf(s));
            this.tabsButton.setTextSize(14f);
        }

        return this;
    }

    public MoTabSearchBar setOnTabsButtonClicked(View.OnClickListener l){
        this.tabsButton.setOnClickListener(l);
        return this;
    }

    public TextView getTabsButton() {
        return tabsButton;
    }

    public MoTabSearchBar setTabsButton(TextView tabsButton) {
        this.tabsButton = tabsButton;
        return this;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public MoTabSearchBar setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
        return this;
    }

    public ImageButton getMoreTabButton() {
        return moreTabButton;
    }

    public MoTabSearchBar setMoreTabButton(ImageButton moreTabButton) {
        this.moreTabButton = moreTabButton;
        return this;
    }

    public TextInputEditText getSearchText() {
        return searchText;
    }

    public MoTabSearchBar setSearchText(TextInputEditText searchText) {
        this.searchText = searchText;
        return this;
    }

    public MoFindBar getMoFindBar() {
        return moFindBar;
    }

    public MoTabSearchBar setMoFindBar(MoFindBar moFindBar) {
        this.moFindBar = moFindBar;
        return this;
    }

    public MoPopupWindow getMoPopupWindow() {
        return moPopupWindow;
    }

    public MoTabSearchBar setMoPopupWindow(MoPopupWindow moPopupWindow) {
        this.moPopupWindow = moPopupWindow;
        return this;
    }

    public MoSearchable getMoSearchable() {
        return moSearchable;
    }

    public MoTabSearchBar setMoSearchable(MoSearchable moSearchable) {
        this.moSearchable = moSearchable;
        return this;
    }

    public MoTabSuggestion getSuggestion() {
        return suggestion;
    }

    public MoTabSearchBar setSuggestion(MoTabSuggestion suggestion) {
        this.suggestion = suggestion;
        return this;
    }



    public MoTab getTab() {
        return tab;
    }

    public MoTabSearchBar clearEditTextFocus(){
        searchText.clearFocus();
        return this;
    }

    public MoTabSearchBar syncWith(MoTab tab) {
        this.tab = tab;
        // sync their web view
        this.moWebView = tab.getMoWebView();
        // sync the search text, so the tab's web view can update it as well
        this.tab.setSearchText(this.searchText);
        // sync the progress bar with the tab
        this.tab.setProgressBar(this.progressBar);
        return this;
    }

    /**
     * shows the progress of the web view
     * the max is 100
     * this progress view should be passed to the
     * chrome web client of the desired web view
     */
    private void initProgressBar(){
        this.progressBar = this.findViewById(R.id.tab_progress);
        this.progressBar.setMax(100);
        this.progressBar.setIndeterminate(false);
        this.progressBar.setProgress(0);

    }


    /**
     * search edit text init
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initSearchText(){
        //search text
        searchText = findViewById(R.id.search_bar_text);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(searchText.hasFocus()){
                    // only show suggestions when user is actually editing it
                    showSuggestions(charSequence);
                    // adding dim effect
                    dimBackgroundWorker.perform();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        searchText.setOnTouchListener((v, event) -> {
            if(MotionEvent.ACTION_UP == event.getAction()) {
                // dim the background when user starts
                dimBackgroundWorker.perform();
            }
            return searchText.performClick();
        });



        searchText.setOnFocusChangeListener((view, b) -> {
            if(!b) {
                // hide the suggestions when user is not editing
                suggestion.hide();
                // removing dim effect
                dimBackgroundWorker.undo();
            }
        });
        searchText.setOnEditorActionListener((textView, i, keyEvent) -> {
            tab.search(textView.getText().toString());
            searchText.clearFocus();
            return false;
        });
    }

    private void initSuggestion() {
        this.suggestion = new MoTabSuggestion(this.context,this)
                .setOnSuggestionClicked(new MoRunnable() {
                    @Override
                    public <T> void run(T... args) {
                        String suggestion = (String)args[0];
                        if(suggestion != null){
                            tab.search(suggestion);
                        }
                    }
                });
        hideSuggestions();
    }

    /**
     * hides the suggestions
     * @return this
     */
    public MoTabSearchBar hideSuggestions(){
        this.suggestion.hide();
        return this;
    }


    /**
     * shows auto complete text when user is typing
     * their search or URL (this is obtained using the
     * current engine's auto-complete feature)
     * @param charSequence search text of user
     */
    private void showSuggestions(CharSequence charSequence){
        if(MoSearchAutoComplete.enabled){
            // only provide them with search suggestions if they want them
            // add the suggestions from history
            MoSuggestions s = new MoSuggestions();
            MoHistoryManager.addSuggestionsFromHistory(charSequence.toString(),s);
            suggestion.show(s);

//            MoHTMLAsyncTask htmlAsyncTask = new MoHTMLAsyncTask().setUrl(
//                    MoSearchEngine.instance.suggestionURL(charSequence.toString()));
//            htmlAsyncTask.setOnHtmlReceived(new MoRunnable() {
//                @SafeVarargs
//                @Override
//                public final <T> void run(T... args) {
//                    // getting suggestion from search engine
//                    MoSuggestions s = MoSearchEngine.instance.getSuggestions((String)args[0]);
//
//                    suggestion.show(s);
//                    // canceling the async task
//                    htmlAsyncTask.cancel(true);
//                }
//            });
//            htmlAsyncTask.execute();
        }
    }

    /**
     * init the tab button
     * when the user presses the tab button, we expect
     * the app to take the user to main menu
     * to show all of the web views
     */
    private void initTabsButton() {
        this.tabsButton = findViewById(R.id.tabs_button);;
    }


    // to show more things to do with the web page
    private void initMoreButton(){
        this.moreTabButton = findViewById(R.id.more_bar_button);
        this.moreTabButton.setOnClickListener(view -> {
            showPopupMenu();
            moPopupWindow.show(view);
        });
    }

    /**
     * shows a pop up menu of many different options
     * that are available
     */
    private void showPopupMenu(){
        this.moPopupWindow = new MoPopupWindow(this.context);
        this.moPopupWindow
                .groupViewsHorizontally(new MoPopupItemBuilder(this.context)
                                .setPopupWindow(moPopupWindow)
                                .buildImageButton(R.drawable.ic_baseline_chevron_right_24,
                                        view-> moWebView.goForwardIfYouCan())
                                .buildCheckedImageButton(R.drawable.ic_baseline_star_24,
                                        R.drawable.ic_baseline_star_border_24, view -> tab.bookmarkTheTab(),
                                        ()-> tab.urlIsBookmarked())
                                .buildImageButton(R.drawable.ic_baseline_refresh_24, view-> moWebView.forceReload())
                                .build())
                .setViews(
                        new MoPopupItemBuilder(this.context)
                                .setPopupWindow(moPopupWindow)
                                .buildTextButton(R.string.find_in_page,
                                        view -> moSearchable.activateSpecialMode())
                                .buildTextButton(R.string.bookmark_title,
                                        view-> BookmarkActivity.startActivity(this.context))
                                .buildTextButton(R.string.home_page_title,
                                        view -> tab.goToHomepage())
                                .buildTextButton(R.string.history,
                                        view-> HistoryActivity.launch(this.context))
                                .buildTextButton(R.string.share, view -> tab.shareTheTab())
                                .buildTextButton(R.string.desktop_mode,moWebView.isInDesktopMode()?
                                        R.drawable.ic_baseline_check_box_24:R.drawable.ic_baseline_check_box_outline_blank_24 , view -> {
                                    moWebView.enableReverseMode();
                                })
                                .build()
                ).build();
    }


    /**
     * for performing find operation
     * we need a find bar
     */
    private void initMoFindBar(){
        this.moFindBar = findViewById(R.id.tab_find_bar);
    }


    /**
     * this enables the find bar to function as expected
     * connects find bar and mo web view functionality
     */
    private void initMoSearchable(){
        initMoFindBar();
        this.moSearchable = new MoSearchable(this.context,this){
            @Override
            public void onUpFindPressed() {
                moWebView.findPrevious();
            }

            @Override
            public void onDownFindPressed() {
                moWebView.findNext();
            }
        };
        this.moSearchable.setSearchOnTextChanged(true)
                .setSearchTextView(moFindBar.getEditText())
                .setUpFind(moFindBar.getMiddleButton())
                .setDownFind(moFindBar.getRightButton())
                .setCancelButton(moFindBar.LId())
                .addNormalViews(searchBarCardView)
                .addUnNormalViews(moFindBar)
                .setOnSearchListener(s -> {
                    this.moWebView.findAllAsync(s, (index, size, finishedFinding) -> {
                        //disable or enable the buttons based on the index and size
                        this.moSearchable.updateUpDownFindButtons(index,size);
                    });
                })
                .setOnSearchCanceled(() -> {
                    // exiting find mode
                    this.moSearchable.deactivateSpecialMode();
                    this.moWebView.clearMatches();
                    this.moSearchable.clearSearch();
                });
    }

    /**
     * destroy the search tab bar
     * object
     */
    public void onDestroy(){
        if(tab!=null){
            tab.setSearchText(null);
            tab.setProgressBar(null);
            tab.setOnUpdateUrlListener(s -> {});
            tab.setOnTabBookmarkChanged(isBookmarked -> {});
        }
        tab = null;
        moWebView = null;
    }


}
