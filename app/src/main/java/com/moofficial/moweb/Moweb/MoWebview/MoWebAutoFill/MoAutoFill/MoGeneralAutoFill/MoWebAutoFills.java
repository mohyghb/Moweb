package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoLog.MoLog;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

// list of auto fills
public class MoWebAutoFills implements MoFileSavable, MoLoadable {


    public static final String NULL_HOST = "null_host";



    private List<MoWebAutoFill> autoFills = new ArrayList<>();
    private MoWebAutoFillId autoFillId = new MoWebAutoFillId();



//    public MoWebAutoFills(String url){
//        extractHost(url);
//    }

    public MoWebAutoFills(String data,Context c){
        this.load(data,c);
    }

//    private void extractHost(String url) {
//        try {
//            this.host = new URL(url).getHost();
//        } catch (MalformedURLException e) {
//            this.host = NULL_HOST;
//        }
//    }

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

    /**
     * prints the structure of the auto-fill
     * inside the command line
     */
    public void printStructure() {
        for (MoWebAutoFill a: autoFills) {
            MoLog.print(a.getValue() + " " + a.getFieldType() + " " + a.getId());
        }
    }

//    /**
//     *
//     * @return the pair of username password
//     * based on the stored information inside this auto fill
//     * or null if we can not find a pair of username password
//     */
//    public Pair<MoWebAutoFill,MoWebAutoFill> getUsernamePassword () {
//        MoWebAutoFill user = null, pass = null;
//        for (MoWebAutoFill webAutoFill: autoFills) {
//            if (webAutoFill.isPassword()) {
//                pass = webAutoFill;
//            }else if (webAutoFill.isText() || webAutoFill.isEmail()) {
//                user = webAutoFill;
//            }
//        }
//        if (pass == null || user == null) {
//            // either we don't have a user name or a password
//            // that means we are not sure about the account of the user
//            return null;
//        } else {
//            return new Pair<>(user,pass);
//        }
//    }
//
//    /**
//     *
//     * @param c context
//     * @return view showing the username and password
//     * of the user's account
//     */
//    public MoPasswordAutoFill getPasswordAutoFillView(Context c) {
//        Pair<MoWebAutoFill,MoWebAutoFill> p = getUsernamePassword();
//        if (p == null) {
//            return null;
//        }
//        MoPasswordAutoFill view = new MoPasswordAutoFill(c)
//                .setUsername(p.first.getValue())
//                .setPassword(p.second.getValue());
//        return view;
//    }

    //public String getHost() {
//        return host;
//    }

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
      //  this.host = l[2];
    }

    @Override
    public String getData() {
        return MoFile.getData(this.autoFillId,this.autoFills);
    }
}
