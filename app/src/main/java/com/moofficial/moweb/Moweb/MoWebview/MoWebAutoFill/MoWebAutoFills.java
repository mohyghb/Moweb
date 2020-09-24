package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// list of auto fills
public class MoWebAutoFills implements MoFileSavable, MoLoadable {


    public static final String NULL_HOST = "null_host";

    private List<MoWebAutoFill> autoFills = new ArrayList<>();
    private MoWebAutoFillId autoFillId = new MoWebAutoFillId();
    private String host;


    public MoWebAutoFills(String url){
        extractHost(url);
    }

    public MoWebAutoFills(String data,Context c){
        this.load(data,c);
    }

    private void extractHost(String url) {
        try {
            this.host = new URL(url).getHost();
        } catch (MalformedURLException e) {
            this.host = NULL_HOST;
        }
    }

    /**
     * adds the auto fill
     * if not already added
     * @param a auto fill to be added
     */
    public MoWebAutoFills add(MoWebAutoFill a) {
        if(autoFills.contains(a))
            return this;
        autoFills.add(a);
        return this;
    }

    /**
     * removes the auto fill from data set
     * @param a auto fill to be removed
     */
    public MoWebAutoFills remove(MoWebAutoFill a) {
        autoFills.remove(a);
        return this;
    }

    /**
     * adds all the auto fills from the colleection to this
     * @param collection to be added
     */
    public MoWebAutoFills addAll(Collection<MoWebAutoFill> collection) {
        autoFills.addAll(collection);
        return this;
    }

    @Override
    public String getDirName() {
        return MoWebAutoFillManager.DIR_NAME;
    }

    @Override
    public String getFileName() {
        return autoFillId.stringify();
    }

    @Override
    public void load(String s, Context context) {
        String[] l = MoFile.loadable(s);
        this.autoFillId.load(l[0],context);
        MoFile.setData(context, l[1], this.autoFills, (context1, s1) -> {
            MoWebAutoFill a = new MoWebAutoFill();
            a.load(s1,context1);
            return a;
        });
        this.host = l[2];
    }

    @Override
    public String getData() {
        return MoFile.getData(this.autoFillId,this.autoFills,this.host);
    }
}
