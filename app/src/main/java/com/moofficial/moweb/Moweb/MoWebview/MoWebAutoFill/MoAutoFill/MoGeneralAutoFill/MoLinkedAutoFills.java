package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFileSavable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoWebAutoFill;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * a class that links multiple auto-fills together
 */
public class MoLinkedAutoFills implements MoFileSavable, MoLoadable {

    private HashMap<Integer, MoWebAutoFill> linkedAutoFills = new HashMap<>();
    private MoWebAutoFillId id = new MoWebAutoFillId();

    /**
     * adds an auto-fill field to the
     * linked auto fills list
     *
     * @param a auto-fill data
     */
    public void add(MoWebAutoFill a) {
        linkedAutoFills.put(a.getAutoCompleteType(), a);
    }

    /**
     * removes the type of 'type'
     * from the auto-fills
     *
     * @param type of the auto-fill
     */
    public void remove(Integer type) {
        linkedAutoFills.remove(type);
    }

    public boolean isEmpty() {
        return linkedAutoFills.isEmpty();
    }

    public boolean isNotEmpty() {
        return !this.isEmpty();
    }

    @Override
    public String getDirName() {
        return MoGeneralAutoFillManager.GENERAL_AUTO_FILL_DIR;
    }

    @Override
    public String getFileName() {
        return id.stringify();
    }

    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        this.id.load(c[0], context);
        MoFile.setData(context, c[1], new ArrayList<>(), (context1, m) -> {
            MoWebAutoFill autoFill = new MoWebAutoFill();
            autoFill.load(m, context1);
            add(autoFill);
            return null;
        });
    }

    @Override
    public String getData() {
        return MoFile.getData(id, this.linkedAutoFills.values());
    }


}
