package com.moofficial.moweb.Moweb.MoWebview;

import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

// information about the state of the web view and more
// this is when we leave the tab activity
// we save these, to make sure we come back to the exact state
// we left the web view at
public class MoWebState implements MoSavable, MoLoadable {


    private int scrollX = 0, scrollY =0;





    public int getScrollX() {
        return scrollX;
    }

    public MoWebState setScrollX(int scrollX) {
        this.scrollX = scrollX;
        return this;
    }

    public int getScrollY() {
        return scrollY;
    }

    public MoWebState setScrollY(int scrollY) {
        this.scrollY = scrollY;
        return this;
    }

    /**
     * applies the current state of the web view
     * to the web view
     * scrolls it so (scrollX,scrollY)
     * where the user left off
     * @param w web view to apply the current state to
     */
    public void applyState(MoWebView w){
        w.scrollTo(scrollX,scrollY);
    }

    @Override
    public void load(String s, Context context) {
        String[] com = MoFile.loadable(s);
//        this.scrollX = Integer.parseInt(com[0]);
//        this.scrollY = Integer.parseInt(com[1]);
    }

    @Override
    public String getData() {
        return MoFile.getData(this.scrollX,this.scrollY);
    }
}
