package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoUserPassAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoId.MoId;
import com.moofficial.moessentials.MoEssentials.MoId.MoIdController;

public class MoUserPassId extends MoId {

    private static final MoIdController moIdController = new MoIdController();

    public MoUserPassId() {
        super(moIdController);
    }

    @Override
    public void load(String s, Context context) {
        setId(Long.parseLong(s));
    }

    @Override
    public String getData() {
        return this.stringify();
    }
}
