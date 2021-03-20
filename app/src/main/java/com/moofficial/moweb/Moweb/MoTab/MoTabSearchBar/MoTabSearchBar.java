package com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;
import com.moofficial.moessentials.MoEssentials.MoClipboard.MoClipboardUtils;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moessentials.MoEssentials.MoRunnable.MoWorker.MoWorker;
import com.moofficial.moessentials.MoEssentials.MoShare.MoShareUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMenuBuilder.MoMenuBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroups.MoConstraint;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoFindBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoViewUtils;
import com.moofficial.moessentials.MoEssentials.MoUtils.MoKeyboardUtils.MoKeyboardUtils;
import com.moofficial.moweb.MoActivities.Bookmark.BookmarkActivity;
import com.moofficial.moweb.MoActivities.History.HistoryActivity;
import com.moofficial.moweb.MoActivities.Download.MoDownloadActivity;
import com.moofficial.moweb.MoActivities.SettingsActivity;
import com.moofficial.moweb.MoHTML.MoHTMLAsyncTask;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSearchAutoComplete;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchAutoComplete.MoSuggestions;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabSuggestion;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoWebview.MoHistory.MoHistoryManager;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

public class MoTabSearchBar extends MoConstraint {

    private MoTabsButton tabsButton;
    private ProgressBar progressBar;
    private ImageButton moreTabButton,copyButton,shareButton;
    private EditText searchText;
    private MoFindBar moFindBar;
    //private MoPopupWindow moPopupWindow;
    private MoBottomSheet bottomSheet;
    private MoSearchable moSearchable;
    private MoTabSuggestion suggestion;

    private MoWebView moWebView;
    private MoTab tab;
    private boolean isInSearch = false;

    // this is used as the parent layout to be dimmed
    // when the user is typing something
    // to make it look less distracting
    // this should be the layout that contains the web view as well
    // also we run animations on this layout as well
    @NonNull private ViewGroup parentLayout;
    private ViewGroup bottomParentLayout;

    // needed to make sure that the background is dimmed only once
    // and not multiple times when the user is typing
    private MoWorker dimBackgroundWorker = new MoWorker();
          //  .setTask(() -> MoViewUtils.dim(parentLayout))
           // .setUndoTask(()-> MoViewUtils.clearDim(parentLayout));

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
        this.progressBar = findViewById(R.id.tab_progress);
        this.moreTabButton = findViewById(R.id.more_bar_button);
        this.shareButton = findViewById(R.id.tab_search_share);
        this.copyButton = findViewById(R.id.tab_search_copy);
        this.searchText = findViewById(R.id.search_bar_text);
        this.tabsButton = findViewById(R.id.include);
    }

    public boolean isInSearch() {
        return isInSearch;
    }


    public void setToSearchMode(boolean b) {
        this.isInSearch = b;
    }

    public void init() {
        initProgressBar();
        initMoreButton();
        initShareButton();
        initCopyButton();
        initSearchText();
        initMoSearchable();
        initSuggestion();
    }

    public MoTabSearchBar setParentLayout(ViewGroup parentLayout) {
        this.parentLayout = parentLayout;
        return this;
    }



    @Override
    public int[] getAttrs() {
        return new int[0];
    }


    /**
     * activates the search bar for searching
     * brings up suggestions
     * shows the search bar helper
     */
    public synchronized void activateSearch() {
        if (this.isInSearch)
            return;
        this.isInSearch = true;
        this.tabsButton.invisible();
        this.moreTabButton.setVisibility(View.INVISIBLE);
        this.shareButton.setVisibility(View.VISIBLE);
        this.copyButton.setVisibility(View.VISIBLE);
    }

    /**
     * deactivates the search bar for searching
     * removes the suggestions
     * removes the helper
     */
    public synchronized void deactivateSearch() {
        if (!this.isInSearch)
            return;

        this.isInSearch = false;
        hideSuggestions();

        this.tabsButton.visible();
        this.moreTabButton.setVisibility(View.VISIBLE);
        this.shareButton.setVisibility(View.GONE);
        this.copyButton.setVisibility(View.GONE);
        this.searchText.clearFocus();
        this.searchText.setText(this.moWebView.getUrl());
    }




    public MoTabSearchBar setTextSearch(String s){
        this.searchText.setText(s);
        return this;
    }


    public MoTabSearchBar setNumberOfTabs(int s){
        tabsButton.setNumberOfTabs(s);
        return this;
    }

    public MoTabSearchBar setOnTabsButtonClicked(View.OnClickListener l){
        this.tabsButton.setOnTabsButtonClicked(l);
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

    public MoTabSearchBar setBottomParentLayout(ViewGroup bottomParentLayout) {
        this.bottomParentLayout = bottomParentLayout;
        return this;
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
        // sync the progress bar with the tab
        this.tab.setProgressBar(this.progressBar);
        // make the progress bar progress equal to zero
        this.progressBar.setProgress(0);
        return this;
    }

    /**
     * shows the progress of the web view
     * the max is 100
     * this progress view should be passed to the
     * chrome web client of the desired web view
     */
    private void initProgressBar(){
        this.progressBar.setMax(100);
        this.progressBar.setIndeterminate(false);

    }


    /**
     * search edit text init
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initSearchText(){
        //search text
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(searchText.hasFocus()) {
                    // only show suggestions when user is actually editing it
                    showSuggestions(charSequence);
                    isInSearch = true;
                    // adding dim effect
                    dimBackgroundWorker.perform();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });


        searchText.setOnTouchListener((v, event) -> {
            final int DRAWABLE_LEFT = 0;


            if (MotionEvent.ACTION_DOWN == event.getAction()) {
                if(event.getRawX() <= (searchText.getLeft() +
                        searchText.getCompoundDrawables()[DRAWABLE_LEFT].getBounds().width())) {
                    // todo show the security info to the user
                    MoLog.print("clicked!!!");
                    return true;
                }
            }


            if(MotionEvent.ACTION_DOWN == event.getAction()) {
                activateSearch();
            }
            return searchText.performClick();
        });





        searchText.setOnFocusChangeListener((view, b) -> {
            if(!b) {
                deactivateSearch();
            }
        });



        searchText.setOnEditorActionListener((textView, i, keyEvent) -> {
            tab.search(textView.getText().toString());
            searchText.clearFocus();
            // hide the keyboard when the search button is clicked
            MoKeyboardUtils.hideSoftKeyboard(textView);
            return false;
        });

    }

    private void initSuggestion() {
        this.suggestion = new MoTabSuggestion(getContext(),
                findViewById(R.id.tab_search_bar_suggestion_card_recycler))
                .setOnSuggestionClicked(new MoRunnable() {
                    @Override
                    public <T> void run(T... args) {
                        String suggestion = (String)args[0];
                        if(suggestion != null) {
                            tab.search(suggestion);
                            MoKeyboardUtils.hideSoftKeyboard(searchText);
                        }
                    }
                }).init();
        hideSuggestions();
    }

    /**
     * hides the suggestions
     * @return this
     */
    public MoTabSearchBar hideSuggestions() {
        this.suggestion.hide();
        return this;
    }

    private void showSuggestions(MoSuggestions s) {
        if (!this.isInSearch) {
            return;
        }
        suggestion.show(s);
    }


    /**
     * shows auto complete text when user is typing
     * their search or URL (this is obtained using the
     * current engine's auto-complete feature)
     * @param charSequence search text of user
     */
    private void showSuggestions(CharSequence charSequence) {
        if(MoSearchAutoComplete.enabled) {
            // only provide them with search suggestions if they want them
            // add the suggestions from history
            String search = charSequence.toString();
            MoHTMLAsyncTask htmlAsyncTask = new MoHTMLAsyncTask().setUrl(
                    MoSearchEngine.instance.suggestionURL(search));
            htmlAsyncTask.setOnHtmlReceived(new MoRunnable() {
                @SafeVarargs
                @Override
                public final <T> void run(T... args) {
                    synchronized (MoTabSearchBar.this) {
                        if (!MoTabSearchBar.this.searchText.getText().toString().equals(charSequence.toString())) {
                            // if the previous text we asked suggestion for is not wanted anymore, skip showing this
                            // this fixes the problems with low speed internets
                            return;
                        }
                        // getting suggestion from search engine
                        MoSuggestions s = new MoSuggestions(search);
                        // getting suggestions from search engine
                        MoSearchEngine.instance.getSuggestions((String)args[0],s);
                        // getting suggestions from history
                        MoHistoryManager.addSuggestionsFromHistory(charSequence.toString().toLowerCase(),s);
                        // sort the suggestions
                        s.sortBySimilarityToSearch();
                        // show suggestions
                        MoTabSearchBar.this.showSuggestions(s);
                        // canceling the async task
                        htmlAsyncTask.cancel(true);
                    }
                }
            });
            htmlAsyncTask.execute();
        }
    }

    // to show more things to do with the web page
    private void initMoreButton() {
        this.moreTabButton.setOnClickListener(view -> {
            showPopupMenu();
            //moPopupWindow.show(view);
        });
    }


    public void initShareButton() {
        this.shareButton.setOnClickListener((v)-> MoShareUtils.share(getContext(),
                this.searchText.getText().toString()));
    }

    public void initCopyButton() {
        this.copyButton.setOnClickListener((v)-> MoClipboardUtils.add(getContext(),
                this.searchText.getText().toString()));
    }

    /**
     * shows a pop up menu of many different options
     * that are available
     */
    private void showPopupMenu() {
        View[] views = new MoMenuBuilder(getContext())
                .rowFill(b -> {
                    b.icon(R.drawable.ic_baseline_refresh_24,(v)-> moWebView.forceReload())
                            .icon(tab.urlIsBookmarked()?
                                            R.drawable.ic_baseline_bookmark_24:
                                            R.drawable.ic_baseline_bookmark_border_24,
                                    (v)-> tab.bookmarkTheTab())
                            .icon(R.drawable.ic_baseline_chevron_left_24, (v) -> moWebView.goBackIfYouCan())
                            .icon(R.drawable.ic_baseline_chevron_right_24, (v)-> moWebView.goForwardIfYouCan());
                })
                .text(R.string.find_in_page,R.drawable.ic_baseline_search_24,view -> moSearchable.activateSpecialMode())
                .text(R.string.bookmark_title,R.drawable.ic_baseline_bookmarks_24,view-> BookmarkActivity.startActivity(getContext()))
                .text(R.string.home_page_title,R.drawable.ic_baseline_home_24,view -> tab.goToHomepage())
                .text(R.string.history,R.drawable.ic_baseline_history_24,view-> HistoryActivity.launch(getContext()))
                .text(R.string.share, R.drawable.ic_baseline_share_24, view -> tab.shareTheTab())
                .text(R.string.downloads, R.drawable.ic_baseline_arrow_downward_24, view -> MoDownloadActivity.startActivity(getContext()))
                .text(R.string.settings, R.drawable.ic_baseline_settings_24, view -> SettingsActivity.launch(getContext()))
                .text(R.string.desktop_mode,moWebView.isInDesktopMode()?
                        R.drawable.ic_baseline_check_box_24:R.drawable.ic_baseline_check_box_outline_blank_24 ,
                        view -> moWebView.enableReverseMode())
                .textsWith(16)
                .iconsWith(0,8,0,0)
                .allWith((v) -> bottomSheet.dismiss())
                .build()
                .asArray();

        bottomSheet = new MoBottomSheet(getContext())
                .setState(BottomSheetBehavior.STATE_EXPANDED)
                .add(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT),views)
                .build()
                .show();
    }




    /**
     * this enables the find bar to function as expected
     * connects find bar and mo web view functionality
     */
    private void initMoSearchable(){
        this.moSearchable = new MoSearchable(getContext(),parentLayout){
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
                })
                .setSearchTextView(moFindBar.getEditText())
                .setUpFind(moFindBar.getMiddleButton())
                .setDownFind(moFindBar.getRightButton())
                .setCancelButton(moFindBar.getLeftButton())
                .addNormalViews(this)
                .addUnNormalViews(moFindBar);
    }

    /**
     * destroy the search tab bar
     * object
     */
    public void onDestroy(){
        if (tab!=null) {
            tab.removeListeners();
        }
        tab = null;
        moWebView = null;
    }

    /**
     * updates whether the current website they are accessing is
     * secure or not
     * @param url of the site the web view is currently showing
     */
    public void updateSecureWebsite(String url) {
        if (URLUtil.isHttpUrl(url)) {
            this.insecureWebsite();
        } else {
            this.secureWebsite();
        }
    }

    /**
     * Shows that the website is secure
     * by showing a green lock inside the search bar text view
     */
    public void secureWebsite() {
        Drawable secureDrawable = getDrawable(R.drawable.ic_baseline_lock_24);
        this.searchText.setCompoundDrawablesWithIntrinsicBounds(secureDrawable, null,null,null);
    }

    /**
     * shows that the website is not secure
     * by showing a red open lock near the search bar
     */
    public void insecureWebsite() {
        Drawable secureDrawable = getDrawable(R.drawable.ic_baseline_lock_open_24);
        DrawableCompat.setTint(secureDrawable, getColor(R.color.not_secure_site_color));
        this.searchText.setCompoundDrawablesWithIntrinsicBounds(secureDrawable, null,null,null);
    }


}
