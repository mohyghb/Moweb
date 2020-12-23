package com.moofficial.moweb.MoActivities;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import com.moofficial.moessentials.MoEssentials.MoMultiThread.MoThread.MoThread;
import com.moofficial.moessentials.MoEssentials.MoUI.MoActivity.MoSmartActivity;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoDelete.MoDeletable;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoRecyclerView.MoRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViewBuilder.MoMarginBuilder;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoBottomBars.MoBottomDeleteBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoInputBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoBars.MoToolBar;
import com.moofficial.moessentials.MoEssentials.MoUI.MoView.MoViews.MoNormal.MoCardRecyclerView;
import com.moofficial.moessentials.MoEssentials.MoValidate.MoTextValidate;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePage;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageManager;
import com.moofficial.moweb.Moweb.MoHomePage.MoHomePageRecyclerAdapter;
import com.moofficial.moweb.R;

public class HomePageActivity extends MoSmartActivity {

    private final int ADD_IME = 20;

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
    }

    private void initBottomDeleteBar() {
        moBottomDeleteBar = new MoBottomDeleteBar(this);
        l.linearBottom.addView(moBottomDeleteBar.gone());
    }

    private void initCardRecyclerView() {
        cardRecyclerView = new MoCardRecyclerView(this).makeCardRound();
        l.linearNested.addView(cardRecyclerView, MoMarginBuilder.getLinearParams(0,8,0,0));
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
                        if(moInputBar.getEditText().hasFocus()){
                            new MoThread<MoTextValidate>()
                                    .after(v -> validateInputText(v))
                                    .doBackground(() -> MoHomePageManager.validate(HomePageActivity.this, charSequence.toString()))
                                    .begin();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
        moInputBar.getCardView().makeCardRound();
        moInputBar.getEditText().actionDone().getTextInputEditText()
                .setOnEditorActionListener((textView, i, keyEvent) -> {
                    if(i == EditorInfo.IME_ACTION_DONE) {
                        addHomePage();
                    }
            return false;
        });
        moInputBar.getEditText().getTextInputEditText().setInputType(InputType.TYPE_CLASS_TEXT);

        l.linearNested.addView(moInputBar);
    }

    private void validateInputText(MoTextValidate v) {
        if(v.isValidate()){
            // then no problem with the entered string
            runOnUiThread(()->moInputBar.getEditText().removeError());
        }else{
            runOnUiThread(()->moInputBar.getEditText().setError(v.getErrorMessage()));
        }
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

    private void initClass() {
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
        this.moListDelete.setOnDeleteSelected(list -> recyclerAdapter.deleteSelected())
                         .setCounterView(l.title)
                         .addUnNormalViews(moDeleteToolbar,moBottomDeleteBar)
                         .addNormalViews(moToolBar,moInputBar)
                         .setSelectAllCheckBox(moDeleteToolbar.getCheckBox())
                         .setConfirmButton(moBottomDeleteBar.getDelete())
                         .setCancelButton(moBottomDeleteBar.getCancel());
        this.moListDelete.setShowOneActionAtTime(true);
    }

    /**
     * adds the text inside edit text to the home pages
     * if it does not already exist
     */
    private void addHomePage() {
        String url = this.moInputBar.getInputText();
        boolean b = MoHomePageManager.validate(this,url).isValidate();
        if(b) {
            this.moInputBar.getEditText().clearFocusAndText();
            MoHomePageManager.add(this,url);
            recyclerAdapter.notifyItemInsertedAtEnd();
            Toast.makeText(this,url+" was added!",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this,"Could not add the home page",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed() {
        if (moListDelete.hasAction()) {
            moListDelete.removeAction();
        } else {
            super.onBackPressed();
        }
    }
}