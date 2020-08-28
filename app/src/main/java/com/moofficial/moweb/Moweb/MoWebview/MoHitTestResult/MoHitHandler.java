package com.moofficial.moweb.Moweb.MoWebview.MoHitTestResult;


import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

public class MoHitHandler extends Handler {

    private MoOnHandleMessage onHandleMessage = message -> {};

    public MoOnHandleMessage getOnHandleMessage() {
        return onHandleMessage;
    }

    public MoHitHandler setOnHandleMessage(MoOnHandleMessage onHandleMessage) {
        this.onHandleMessage = onHandleMessage;
        return this;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        super.handleMessage(msg);
        onHandleMessage.handle(msg);
    }
}
