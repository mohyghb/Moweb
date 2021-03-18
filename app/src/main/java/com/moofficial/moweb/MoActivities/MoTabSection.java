package com.moofficial.moweb.MoActivities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.http.SslError;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moessentials.MoEssentials.MoPermissions.MoPermission;
import com.moofficial.moessentials.MoEssentials.MoString.MoString;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoActivitySettings.MoActivitySettings;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moessentials.MoEssentials.MoUI.MoFragment.MoOnBackPressed;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoBasicLayout;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoPaddingBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoAppbar.MoAppbarUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewGroupUtils.MoCoordinatorUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoFindBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardView;
import com.moofficial.moessentials.MoEssentials.MoUtils.MoKeyboardUtils.MoKeyboardUtils;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadConfirmation;
import com.moofficial.moweb.Moweb.MoDownload.MoDownloadManager;
import com.moofficial.moweb.Moweb.MoSearchEngines.MoSearchEngine;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoTabController;
import com.moofficial.moweb.Moweb.MoTab.MoTabController.MoUpdateTabActivity;
import com.moofficial.moweb.Moweb.MoTab.MoTabSearchBar.MoTabSearchBar;
import com.moofficial.moweb.Moweb.MoTab.MoTabUtils;
import com.moofficial.moweb.Moweb.MoTab.MoTabs.MoTab;
import com.moofficial.moweb.Moweb.MoTab.MoTabsManager;
import com.moofficial.moweb.Moweb.MoWebFeatures.MoWebFeatures;
import com.moofficial.moweb.Moweb.MoWebview.MoWebError.MoSSLBottomSheet;
import com.moofficial.moweb.Moweb.MoWebview.MoWebError.MoSSLUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoWebError.MoWebErrorView;
import com.moofficial.moweb.Moweb.MoWebview.MoWebError.MoWebResourceErrorUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnReceivedError;
import com.moofficial.moweb.Moweb.MoWebview.MoWebInterfaces.MoOnUpdateUrlListener;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;
import com.moofficial.moweb.R;

@SuppressWarnings("ConstantConditions")
public class MoTabSection extends MoBasicLayout implements MoUpdateTabActivity,
        MoOnBackPressed, DownloadListener, MoOnReceivedError, MoOnUpdateUrlListener {

    private static final int MAIN_MENU_REQUEST_CODE = 0;
    public static final int GO_TO_TAB_ACTIVITY_REQUEST = 1;
    public static final int DOWNLOAD_PERMISSION_REQUEST = 2;



    private MoTabSearchBar moTabSearchBar;
    private MoTab tab;
    private MoWebView webView;
    private MoToolBar moToolBar;
    private MainTransitionTo moveToMainMenu;
    private MoCardView webCard;
    private MoPermission permission;
    private MoWebErrorView webErrorView;
    private Activity activity;
    private SslErrorHandler sslErrorHandler;
    private boolean isShowingError = false;

    public MoTabSection(Context context) {
        super(context);
    }

    public MoTabSection(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MoTabSection(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    MoTabSection setMoveToMainMenu(MainTransitionTo moveToMainMenu) {
        this.moveToMainMenu = moveToMainMenu;
        return this;
    }

    public MoTabSection setActivity(Activity activity) {
        this.activity = activity;
        return this;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onAppbarLayoutHeightChanged(MoActivitySettings.MO_GOLDEN_RATIO);
    }

    protected void init() {
        MoAppbarUtils.snapNoToolbar(collapsingToolbarLayout);
        removeNestedScrollView();
        appBarLayout.setExpanded(false);
        initSearchBar();
        initToolbar();
        initWebCardView();
        MoTabController.instance.setUpdateTabActivity(this);
        initPermission();
        initWebError();
        update();
    }

    private void initWebError() {
        this.webErrorView = new MoWebErrorView(getContext());
        this.coordinatorLayout.addView(this.webErrorView, MoCoordinatorUtils.getScrollingParams(
                new MoPaddingBuilder(getContext())
                        .setBottom(8)
                        .asDp()));
    }

    private void initPermission() {
        this.permission = new MoPermission(this.activity)
                .setRequestCode(DOWNLOAD_PERMISSION_REQUEST)
                .setPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    /**
     * updates the activity so that the user
     * only sees the updated version of the
     * tab activity and everything seems normal
     */
    @Override
    public void update() {
        if(MoTabController.instance.isOutOfOptions()) {
            // if we are out of options, make a new tab
            MoTabsManager.addTab(getContext(), MoSearchEngine.instance.homePage(),false);
        }else {
            // only perform update when the new tab is different than the old tab
            removePreviousTab();
            updateTab();
            updateWebView();
            updateSearchBar();
            updateTitle();
            updateSubtitle();
            updateToolbar();
            hideErrorView();
        }
    }


    /**
     * removes the previous tab from the coordinator
     * layout before passing it to the recycler view inside
     * main menu
     * this solved the issue where we opened a tab
     * from somewhere else and it didn't remove the
     * previous web view and tab from here
     * so we only are removing it if there is a current tab
     * and the current tab is not the same as the new one
     */
    private void removePreviousTab() {
        if(webView != null) {
            // then remove it from web view when updating
            webView.removeListeners();
            webCard.removeView(webView);
        }
        moTabSearchBar.onDestroy();
    }


    private void updateTitle() {
        setTitle(MoString.capFirst(this.webView.getTitle()));
    }

    private void updateSubtitle(){
        setSubTitle(this.webView.getUrl());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void updateWebView() {
        this.webView = tab.getMoWebView();
        MoTabUtils.transitionToInTabMode(webView,webCard,
                new CardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        this.webView.onResume();
        this.webView.setOnLongClickListener(view -> {
            webView.getHitTestResultParser().createDialogOrSmartText(getContext());
            return false;
        });
        this.webView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                if (moTabSearchBar.isInSearch()) {
                    // we need to cancel it
                    moTabSearchBar.deactivateSearch();
                    MoKeyboardUtils.hideSoftKeyboard(v);
                    return true;
                }
            }
            return moTabSearchBar.isInSearch();
        });
        this.webView.setOnErrorReceived(this)
                .setOnUpdateUrlListener(this)
                .setDownloadListener(this);
    }

    private void updateTab() {
        this.tab = MoTabController.instance.getCurrent();
        this.tab.init();
        this.tab.updateWebViewIfNotUpdated();
    }

    /**
     * creates a search bar that can be
     * used multiple times over the life time
     * of the tab by changing its fields
     */
    private void initSearchBar() {
        MoFindBar findBar = new MoFindBar(getContext());
        this.moTabSearchBar = new MoTabSearchBar(getContext())
                .setParentLayout(coordinatorLayout)
                .setBottomParentLayout(linearBottom.getLinearLayout())
                .setMoFindBar(findBar);
        this.moTabSearchBar.init();
        linearBottom.setupMultipleBars(moTabSearchBar, moTabSearchBar, findBar);
    }

    /**
     * puts the home button, refresh button
     * and the book mark button inside the toolbar
     */
    private void initToolbar() {
        this.moToolBar = new MoToolBar(getContext());
        // set it as a toolbar
        toolbar.addToolbar(this.moToolBar);
    }

    private void initWebCardView() {
        webCard = new MoCardView(getContext()).makeCardMediumRound().makeTransparent().removeElevation();
        coordinatorLayout.addView(webCard,MoCoordinatorUtils.getScrollingParams(
                new MoPaddingBuilder(getContext())
                .setBottom(8)
                .asDp()));
    }

    private void updateToolbar() {
        this.moToolBar.setLeftIcon(R.drawable.ic_baseline_home_24)
                .setLeftOnClickListener(view -> tab.goToHomepage())
                .hideTitle()
                .setRightIcon(R.drawable.ic_baseline_refresh_24)
                .setRightOnClickListener(view -> webView.forceReload())
                .setMiddleOnClickListener(view -> {
                    tab.bookmarkTheTab();
                });
        this.updateBookmark();

        // on tab change bookmark listener so we know which icon to use
        tab.setOnTabBookmarkChanged(isBookmarked -> updateBookmark());
    }

    public void updateBookmark() {
        this.moToolBar.setMiddleIcon(tab.urlIsBookmarked()?
                R.drawable.ic_baseline_bookmark_24:
                R.drawable.ic_baseline_bookmark_border_24);
    }



    /**
     * removes all the listeners for the search bar
     * on the previous tab and loads this tabs
     * search bar module
     */
    private void updateSearchBar() {
        //monote hide suggestion on every update
        moTabSearchBar.syncWith(tab)
                .clearEditTextFocus()
                .setTextSearch(tab.getUrl())
                .setOnTabsButtonClicked(view -> {
                    // remove the ssl handler in case one exist
                    this.sslErrorHandler = null;
                    MoKeyboardUtils.hideSoftKeyboard(view);
                    onTabsButtonPressed();
                })
                .setNumberOfTabs(tab.isPrivate()?MoTabsManager.sizePrivate():MoTabsManager.size());
    }

    /**
     * goes to the main menu activity
     * to show all the tabs to the user
     */
    private void onTabsButtonPressed() {
        tab.captureAndSaveWebViewBitmapAsync(coordinatorLayout);
        moveToMainMenu.transition();
    }

    @Override
    public boolean onBackPressed() {
        if (this.moTabSearchBar.isInSearch()) {
            this.moTabSearchBar.deactivateSearch();
            return true;
        }else if (isShowingError) {
            hideErrorView();
            return true;
        } else {
            return tab.onBackPressed();
        }
    }

    @Override
    public void onDownloadStart(String url, String userAgent, String contentDisposition,
                                String mimetype, long contentLength) {
        String s = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(s);
        String name = URLUtil.guessFileName(url,
                contentDisposition, mimeType).replace(" ","");
        if(permission.checkAndRequestPermissions()) {
            if (MoDownloadManager.alreadyHasFile(url, contentDisposition, mimeType)) {
                Toast.makeText(getContext(), "File has already been downloaded!", Toast.LENGTH_SHORT).show();
            } else {
                Thread downloadThread = new Thread() {
                    @Override
                    public void run() {
                        MoDownloadManager.enqueueDownload(url, contentDisposition, mimeType, userAgent);
                    }
                };
                downloadConfirmationBottomSheet(name, contentLength, downloadThread);
            }
        }
    }

    /**
     * asks the user whether they want to download the file or not
     * @param name of the download
     * @param thread that runs the download when started
     */
    private void downloadConfirmationBottomSheet(String name, long contentLength,Thread thread) {
        MoDownloadConfirmation confirmation = new MoDownloadConfirmation(getContext());
        MoBottomSheet popupWindow = new MoBottomSheet(getContext());
        confirmation.onCancel(popupWindow::dismiss).onSave(()-> {
            // start download and dismiss dialog
            thread.start();
            popupWindow.dismiss();
        }).withName(name).setSize(contentLength);
        popupWindow.add(confirmation)
                .setCanceledOnTouchOutside(false)
                .setState(BottomSheetBehavior.STATE_EXPANDED)
                .build()
                .show();
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//        this.webErrorView.normalError()
//                .setTitle(MoWebResourceErrorUtils.getTitle(error))
//                .setDescription("Unfortunately we are not able to load " + view.getUrl() +
//                        ". The error description is as follows: " + error.getDescription().toString());
//        this.showErrorView();
    }

    @Override
    public void onSSLErrorReceived(WebView view, SslErrorHandler handler, SslError error) {
        this.sslErrorHandler = handler;
        this.webErrorView.sslError();
        this.webErrorView.setTitle(MoSSLUtils.getTitle(error));
        this.webErrorView.setDescription(error.toString());
        this.webErrorView.onAdvancedClicked((v) -> {
            MoBottomSheet sheet = new MoBottomSheet(getContext());
            MoSSLBottomSheet ssl = new MoSSLBottomSheet(getContext());
            ssl.setDescription("Are you sure you wanna proceed? The site seems to be dangerous")
                    .onDecline(()-> {
                        sheet.dismiss();
                        handler.cancel();
                        hideErrorView();
                    })
                    .onProceed(()-> {
                        sheet.dismiss();
                        handler.proceed();
                        hideErrorView();
                    });
            sheet.add(ssl)
                    .setState(BottomSheetBehavior.STATE_EXPANDED)
                    .build()
                    .show();
        });
        this.showErrorView();
    }

    @Override
    public void hideError() {
        hideErrorView();
    }

    public void showErrorView() {
        // todo a case where the user presses the tab button and goes to another tab
        this.isShowingError = true;
        this.webView.setVisibility(View.GONE);
        this.webErrorView.setVisibility(View.VISIBLE);
        this.moTabSearchBar.insecureWebsite();
    }

    public void hideErrorView() {
        this.isShowingError = false;
        this.webView.setVisibility(View.VISIBLE);
        this.webErrorView.setVisibility(View.GONE);
        this.moTabSearchBar.updateSecureWebsite(this.webView.getUrl());
        if (this.sslErrorHandler != null) {
            this.sslErrorHandler.cancel();
        }
        this.sslErrorHandler = null;
    }


    @Override
    public void onUrlUpdate(String url, boolean isReload) {
        if (url.equals(this.tab.getUrl())) {
            return;
        }
        updateTitle();
        updateSubtitle();
        updateToolbar();
        this.tab.updateUrl(url);
        this.moTabSearchBar.updateSecureWebsite(url);
        this.moTabSearchBar.setTextSearch(url);
        this.moTabSearchBar.deactivateSearch();
        this.tab.saveTab();
    }
}