package com.moofficial.moweb.Moweb.MoWebview;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.core.widget.NestedScrollView;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebViews.MoWebView;

// information about the state of the web view and more
// this is when we leave the tab activity
// we save these, to make sure we come back to the exact state
// we left the web view at
public class MoWebState implements MoSavable, MoLoadable {


    private int width = ViewGroup.LayoutParams.MATCH_PARENT;
    private int height = ViewGroup.LayoutParams.MATCH_PARENT;
    private int scrollX = 0, scrollY =0;



    public int getWidth() {
        return width;
    }

    public MoWebState setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return height;
    }

    public MoWebState setHeight(int height) {
        this.height = height;
        return this;
    }

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
     * @param w web view to apply the current state to
     * @param n if the web view is inside a nested scroll view
     *          pass null if it is not
     */
    public void applyState(MoWebView w, NestedScrollView n){
        w.setLayoutParams(new LinearLayout.LayoutParams(this.width, this.height));
        if(n==null){
            w.scrollTo(scrollX,scrollY);
        }else{
            n.smoothScrollTo(scrollX,scrollY);
        }
    }

    @Override
    public void load(String s, Context context) {
        String[] com = MoFile.loadable(s);
        this.width = Integer.parseInt(com[0]);
        this.height = Integer.parseInt(com[1]);
        this.scrollX = Integer.parseInt(com[2]);
        this.scrollY = Integer.parseInt(com[3]);
    }

    @Override
    public String getData() {
        return MoFile.getData(this.width,this.height,this.scrollX,this.scrollY);
    }
}
