package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill;

import android.content.Context;
import android.view.View;
import android.webkit.WebView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoMultiThread.MoThread.MoThread;
import com.moofficial.moessentials.MoEssentials.MoUI.MoBottomSheet.MoBottomSheet;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableInterface.MoSearchableItem;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSearchable.MoSearchableUtils;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;
import com.moofficial.moessentials.MoEssentials.MoUtils.MoKeyboardUtils.MoKeyboardUtils;
import com.moofficial.moweb.Moweb.MoUrl.MoUrlUtils;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill.Views.MoUserPassTitle;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoUserPassHolderView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class MoUserPassAutoFill implements MoFileSavable, MoLoadable, MoSelectableItem, MoSearchableItem {




    public interface MoOnUserPassClicked {
        void onClick(MoUserPassAutoFill a);
    }


    private MoWebAutoFill username, password;
    // these might be substituted for the username or password instead of the things we saved
    private List<MoWebAutoFill> suggestions = new ArrayList<>();
    private String host;
    private MoUserPassId id = new MoUserPassId();
    private boolean selected = false;
    private long dateTime = Calendar.getInstance().getTimeInMillis();
    private boolean searched = true;
    /**
     *
     * @param u username of the user's account
     * @param p password of user's account
     * @param h host to save the user password for
     */
    public MoUserPassAutoFill(MoWebAutoFill u, MoWebAutoFill p, String h) {
        this.username = u;
        this.password = p;
        this.host = h;
    }

    public MoUserPassAutoFill(){}

    public MoWebAutoFill getUsername() {
        return username;
    }

    public MoWebAutoFill getPassword() {
        return password;
    }

    public String getHost() {
        return host;
    }

    public MoUserPassAutoFill setUsername(MoWebAutoFill username) {
        this.username = username;
        return this;
    }

    public MoUserPassAutoFill setPassword(MoWebAutoFill password) {
        this.password = password;
        return this;
    }

    public MoUserPassAutoFill setHost(String host) {
        this.host = host;
        return this;
    }

    public long getDateTime() {
        return dateTime;
    }

    public MoUserPassAutoFill add (MoWebAutoFill autoFill) {
        if (autoFill.isPassword()) {
            // todo if the type of the password is NEW_PASSWORD,  then we can just update the ref
            //  if we have it
            this.setPassword(autoFill);
        } else if (autoFill.isUsername()) {
            this.setUsername(autoFill);
        } else {
            // add this to suggestions
            suggestions.add(autoFill);
        }
        return this;
    }

    /**
     * chooses the top suggestion as the
     * username
     */
    public void chooseTopSuggestionIfNoUsername(){
        if (this.username == null && !this.suggestions.isEmpty()) {
            this.username = suggestions.get(0);
        }
    }

    /**
     *
     * @return true if the
     * username and password are not null
     * and host is not null or empty
     */
    public boolean isValid() {
        return this.password!=null && this.username!=null && this.host!=null && !this.host.isEmpty();
    }

    /**
     *
     * @param c context
     * @return view showing the username and password
     * of the user's account
     */
    public MoUserPassHolderView getView(Context c, MoOnUserPassClicked onClick) {
        return new MoUserPassHolderView(c)
                .setUsername(username.getValue())
                .setPassword(password.getValue())
                .setHost(this.host)
                .setOnAutoFillClickListener(()->onClick.onClick(this))
                .hideCopies();
    }

    /**
     * fills in the password and username
     * inside the given web view
     * @param webView to fill the values in
     */
    public void fill(WebView webView) {
        new MoThread<String>()
                .doBackground(() -> {
                    String sb = "javascript:fillUserPass('%s','%s','%s','%s')";
                    return String.format(sb,username.getId(),username.getValue(),
                            password.getId(),password.getValue()); })
                .after(val -> webView.post( () -> webView.evaluateJavascript(val,null)))
                .begin();
    }


    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        this.username = new MoWebAutoFill();
        this.username.load(c[0],context);
        this.password = new MoWebAutoFill();
        this.password.load(c[1],context);
        this.host = c[2];
        this.id.load(c[3],context);
        parseDateTime(c);
    }

    private void parseDateTime(String[] c) {
        if (c.length >= 5) {
            this.dateTime = Long.parseLong(c[4]);
        }
    }

    @Override
    public String getData() {
        return MoFile.getData(this.username,this.password,this.host,this.id,this.dateTime);
    }

    @Override
    public String getDirName() {
        return MoUserPassManager.DIR_NAME;
    }

    @Override
    public String getFileName() {
        return id.stringify();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MoUserPassAutoFill that = (MoUserPassAutoFill) o;
        boolean b = Objects.equals(username, that.username);
        boolean b1 = Objects.equals(password, that.password);
        return  b && b1 && Objects.equals(host, that.host);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username, password, host);
    }


    @Override
    public void setSelected(boolean b) {
        this.selected = b;
    }

    @Override
    public boolean isSelected() {
        return this.selected;
    }

    @Override
    public boolean updateSearchable(Object... objects) {
        return MoSearchableUtils.isSearchable(true,objects,this.username.getValue(),this.host);
    }

    @Override
    public boolean isSearchable() {
        return this.searched;
    }

    @Override
    public void setSearchable(boolean b) {
        this.searched = b;
    }


    /**
     * shows a bottom sheet in which all the
     * user pass for the host are shown
     * @param context of app
     * @param webView to find out what host is and where
     *               the fields are for filling them in
     */
    public static void showUserPassAutoFill(Context context,WebView webView) {
        String host = MoUrlUtils.getHost(webView.getUrl());
        List<MoUserPassAutoFill> autoFills = MoUserPassManager.get(host);
        if (autoFills == null)
            return;
        MoBottomSheet bottomSheet = new MoBottomSheet(context);

        // add title
        MoUserPassTitle title = new MoUserPassTitle(context);
        bottomSheet.addTitle(title);


        boolean atLeastOneChild = false;
        for (MoUserPassAutoFill a : autoFills) {
            View v = a.getView(context, a1 -> {
                // what happens when the click the user pass auto fill
                a1.fill(webView);
                bottomSheet.dismissWithoutListener();
            });
            if (v != null) {
                bottomSheet.add(v);
                atLeastOneChild = true;
            }
        }

        // todo opening another tab then trying to use password auto fill causes crash
        //  error: is your activity still running
        if (atLeastOneChild) {
            // bring the keyboard up on web view when dismissed
            bottomSheet.setState(BottomSheetBehavior.STATE_EXPANDED)
                    .setOnDismissedListener(()-> MoKeyboardUtils.showKeyboard(webView,context))
                    .build()
                    .show();
        }
    }

}
