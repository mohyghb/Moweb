package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill;

import android.content.Context;
import android.webkit.WebView;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoMultiThread.MoThread.MoThread;
import com.moofficial.moessentials.MoEssentials.MoUI.MoInteractable.MoSelectable.MoSelectableInterface.MoSelectableItem;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoUserPassHolderView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MoUserPassAutoFill implements MoFileSavable, MoLoadable, MoSelectableItem {



    public interface MoOnUserPassClicked {
        void onClick(MoUserPassAutoFill a);
    }


    private MoWebAutoFill username, password;
    // these might be substituted for the username or password instead of the things we saved
    private List<MoWebAutoFill> suggestions = new ArrayList<>();
    private String host;
    private MoUserPassId id = new MoUserPassId();
    private boolean selected = false;
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
                .setOnAutoFillClickListener(()->onClick.onClick(this));
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
                    String formatted = String.format(sb,username.getId(),username.getValue(),
                            password.getId(),password.getValue());
                    return formatted; })
                .after(val -> webView.post( () -> webView.loadUrl(val)))
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
    }

    @Override
    public String getData() {
        return MoFile.getData(this.username,this.password,this.host,this.id);
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
        return Objects.equals(username, that.username) &&
                Objects.equals(password, that.password) &&
                Objects.equals(host, that.host);
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
}
