package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.Views.MoPasswordAutoFill;

public class MoUserPassAutoFill implements MoFileSavable, MoLoadable {




    private MoWebAutoFill username = new MoWebAutoFill(), password = new MoWebAutoFill();
    private String host;
    private MoUserPassId id = new MoUserPassId();

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

    /**
     *
     * @param c context
     * @return view showing the username and password
     * of the user's account
     */
    public MoPasswordAutoFill getView(Context c) {
        // todo when they click on the item, these auto-fills
        //  should apply to the correct fields inside js
        return new MoPasswordAutoFill(c)
                .setUsername(username.getValue())
                .setPassword(password.getValue());
    }

    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        this.username.load(c[0],context);
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
}
