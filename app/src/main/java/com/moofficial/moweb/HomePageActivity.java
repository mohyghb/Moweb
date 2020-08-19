package com.moofficial.moweb;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoBottomBars.MoBottomDeleteBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoInputBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoLayouts.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePage;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageRecyclerAdapter;

public class HomePageActivity extends MoSmartActivity {

    private MoRecyclerView recyclerView;
    private MoHomePageRecyclerAdapter recyclerAdapter;
    private MoDeletable<MoHomePage> moListDelete;

    // UI fields
    private MoCardRecyclerView cardRecyclerView;
    private MoInputBar moInputBar;
    private MoToolBar moToolBar,moDeleteToolbar;
    private MoBottomDeleteBar moBottomDeleteBar;

    @Override
    protected void init() {
        initUI();
        initClass();
    }

    private void initUI() {
        setTitle(R.string.home_page_title);
        initInputBar();
        initCardRecyclerView();
        initToolbars();
        initBottomDeleteBar();
        syncTitle(moDeleteToolbar.getTitle(),moToolBar.getTitle());
        disableToolbarAnimation();
    }

    private void initBottomDeleteBar() {
        moBottomDeleteBar = new MoBottomDeleteBar(this);
        linearBottom.addView(moBottomDeleteBar.goGone());
    }

    private void initCardRecyclerView() {
        cardRecyclerView = new MoCardRecyclerView(this).makeCardRound();
        linearNested.addView(cardRecyclerView, MoMarginBuilder.getLinearParams(0,8,0,0));
    }

    private void initInputBar() {
        moInputBar = new MoInputBar(this)
                .setTitle(R.string.new_home_page)
                .setDescription(R.string.new_home_page_description)
                .showPositiveButton()
                .showDescription()
                .setPositiveButtonText(R.string.add_home_page)
                .setPositiveClickListener(view -> {
                    addHomePage();
                })
                .showDividerInvisible()
                .setHint(R.string.new_home_page_hint)
                .addTextWatcher(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        MoHomePageManager.validate(HomePageActivity.this,
                                charSequence.toString(),moInputBar.getEditText());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
        moInputBar.getCardView().makeCardRound();
        linearNested.addView(moInputBar);
    }

    private void initToolbars() {
        moToolBar = new MoToolBar(this)
                .setLeftOnClickListener(view -> onBackPressed())
                .onlyTitleAndLeftButtonVisible();
        moToolBar.getCardView().makeTransparent();
        moDeleteToolbar = new MoToolBar(this)
                .hideLeft()
                .showCheckBox()
                .hideMiddle()
                .hideRight();
        moDeleteToolbar.getCardView().makeTransparent();
        setupMultipleToolbars(moToolBar,moToolBar,moDeleteToolbar);
    }

    private void initClass(){
        initRecyclerAdapter();
        initRecyclerView();
        initMoListDeletable();
    }



    private void initRecyclerAdapter(){
        this.recyclerAdapter = new MoHomePageRecyclerAdapter(this,MoHomePageManager.get());
    }

    private void initRecyclerView(){
        this.recyclerView = MoRecyclerUtils.get(cardRecyclerView.getRecyclerView(),this.recyclerAdapter);
        this.recyclerView.show();
    }

    private void initMoListDeletable(){
        this.moListDelete = new MoDeletable<>(this,getGroupRootView(),this.recyclerAdapter);
        this.moListDelete.setCounterView(title)
                         .addUnNormalViews(moDeleteToolbar,moBottomDeleteBar)
                         .addNormalViews(moToolBar,moInputBar)
                         .setConfirmButton(moBottomDeleteBar.getDelete())
                         .setCancelButton(moBottomDeleteBar.getCancel());
        this.moListDelete.setShowOneActionAtTime(true);
    }

    /**
     * adds the text inside edit text to the home pages
     * if it does not already exist
     */
    private void addHomePage(){
        String url = this.moInputBar.getInputText();
        if(url.isEmpty()){
            Toast.makeText(this,"Please enter an url",Toast.LENGTH_SHORT).show();
            return;
        }
        boolean b = MoHomePageManager.add(this,url);
        if(b) {
            recyclerAdapter.notifyItemInsertedAtEnd();
        }else {
            Toast.makeText(this,"Could not add the home page",Toast.LENGTH_SHORT).show();
        }
    }


}