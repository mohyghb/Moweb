package com.moofficial.moweb.Moweb.MoWebview.MoStackTabHistory;


import android.content.Context;

import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoFileManager.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebView;

import java.util.ArrayList;
import java.util.Arrays;

// a class to save all the searches of the user
// so we can go back if stack has anything in it
// it has a mutual connection with a mo web view
public class MoStackTabHistory implements MoSavable, MoLoadable {

    // millisecond of double pressing the back button to jump back two goBacks
    private final int DOUBLE_BACK_PRESS_TOLERANCE = 1200;

    private static final int ACTION_BACK = 0;
    private static final int ACTION_FORWARD = 1;
    private static final int ACTION_NULL = 2;

    private ArrayList<String> list = new ArrayList<>();
    private int currentIndex = 0;
    private MoWebView webView;
    private int previousAction = ACTION_NULL;
    private long lastBackPressed;

    public MoStackTabHistory(){

    }

    public MoStackTabHistory(String data,Context c){
        this.load(data,c);
    }

    /**
     * adds the url to the stack of urls
     * only adds it if the current url is different
     * than the last url added
     *
     */
    public void update(){
        @SuppressWarnings("ConstantConditions")
        String url = webView.copyBackForwardList().getCurrentItem().getUrl();
        if(url!=null && list.isEmpty() || !list.get(currentIndex).equals(url) && previousAction == ACTION_NULL){
            if(currentIndex!=list.size()-1 && previousAction==ACTION_NULL){
                removeAfterCurrentIndex();
            }
            this.list.add(url);
            currentIndex = this.list.size()-1;
        }
        this.previousAction = ACTION_NULL;
    }

    private void removeAfterCurrentIndex(){
        for(int i = list.size()-1; i>currentIndex; i--){
            list.remove(i);
        }
    }

    public MoStackTabHistory setWebView(MoWebView webView) {
        this.webView = webView;
        return this;
    }

    /**
     * if the stack has more than one url, we
     * can still go back a page (if it only has one url in it,
     * that's the one that they are currently on)
     * @return true if the stack has more than one url and the web view is not null
     */
    public boolean canGoBack(){
        return this.webView != null && currentIndex>0;
    }

    /**
     * pops a url from the stack
     * and peeks the next url (which is basically the previous page)
     * into the web view
     * adds it to the popped queue
     */
    public void goBack(){
        previousAction = ACTION_BACK;
        currentIndex--;
        if(canGoBack()&& System.currentTimeMillis() - lastBackPressed <= DOUBLE_BACK_PRESS_TOLERANCE){
            // go back one more
            currentIndex--;
            //Toast.makeText(this.webView.getContext(),"double press",Toast.LENGTH_SHORT).show();
        }
        this.webView.loadUrl(getCurrentURL());
        lastBackPressed = System.currentTimeMillis();
    }

    public boolean canGoForward(){
        return this.webView!=null && currentIndex< list.size()-1;
    }

    /**
     * adds this to the
     */
    public void goForward(){
        previousAction = ACTION_FORWARD;
        currentIndex++;
        this.webView.loadUrl(getCurrentURL());
    }


    /**
     * goes forward if it can
     */
    public void goForwardIfPossible(){
        if(canGoForward()){
            goForward();
        }
    }


    public String getCurrentURL(){
        return this.list.get(this.currentIndex);
    }

    @Override
    public void load(String data, Context context) {
        String[] c = MoFile.loadable(data);
        if(MoFile.isValidData(c)){
            String[] stk = MoFile.loadable(c[0]);
            this.list.addAll(Arrays.asList(stk));
            if(c.length > 1){
                this.currentIndex = Integer.parseInt(c[1]);
            }
        }
    }

    @Override
    public String getData() {
        return MoFile.getData(this.list,this.currentIndex);
    }
}
