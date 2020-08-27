package com.moofficial.moweb.Moweb.MoTab.MoTabId;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moessentials.MoEssentials.MoId.MoId;
import com.moofficial.moessentials.MoEssentials.MoId.MoIdController;

public class MoTabId extends MoId implements MoSavable, MoLoadable {

    private static final MoIdController moTabIdController = new MoIdController();

    public MoTabId() {
        super(moTabIdController);
    }

    @Override
    public void load(String s, Context context) {
        String[] c = MoFile.loadable(s);
        setId(Long.parseLong(c[0]));
    }


    @Override
    public String getData() {
        return MoFile.getData(this.getId());
    }


}
