package com.moofficial.moweb.Moweb.MoWebview.MoWebAutoFill.MoAutoFill.MoGeneralAutoFill;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoId.MoId;
import com.moofficial.moessentials.MoEssentials.MoId.MoIdController;

public class MoWebAutoFillId extends MoId {

    private static final MoIdController moIdController = new MoIdController();

    public MoWebAutoFillId() {
        super(moIdController);
    }

    @Override
    public void load(String s, Context context) {
        this.setId(Long.parseLong(s));
    }

    @Override
    public String getData() {
        return stringify();
    }
}
