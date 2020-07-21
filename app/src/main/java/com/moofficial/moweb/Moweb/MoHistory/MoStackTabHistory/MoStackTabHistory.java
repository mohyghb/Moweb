package com.moofficial.moweb.Moweb.MoHistory.MoStackTabHistory;


import android.content.Context;


import com.moofficial.moessentials.MoEssentials.MoIO.MoFile;
import com.moofficial.moessentials.MoEssentials.MoIO.MoLoadable;
import com.moofficial.moessentials.MoEssentials.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebView;

import java.util.Arrays;
import java.util.Stack;

// a class to save all the searches of the user
// so we can go back if stack has anything in it
// it has a mutual connection with a mo web view
public class MoStackTabHistory implements MoSavable, MoLoadable {

    private Stack<String> backwardStack = new Stack<>();
    private Stack<String> forwardStack = new Stack<>();
    private MoWebView webView;

    public MoStackTabHistory(){

    }

    public MoStackTabHistory(String data,Context c){
        this.load(data,c);
    }

    /**
     * adds the url to the stack of urls
     * only adds it if the current url is different
     * than the last url added
     * @param url
     */
    public void add(String url){
        if(this.backwardStack.isEmpty() || !this.backwardStack.peek().equals(url)){
            this.backwardStack.add(url);
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
        return this.webView.getWebView() != null && this.backwardStack.size()>1;
    }

    /**
     * pops a url from the stack
     * and peeks the next url (which is basically the previous page)
     * into the web view
     * adds it to the popped queue
     */
    public void goBack(){
        forwardStack.add(this.backwardStack.pop());
        this.webView.loadUrl(this.backwardStack.peek());
    }

    public boolean canGoForward(){
        return this.webView.getWebView()!=null && this.forwardStack.size()>0;
    }

    /**
     * adds this to the
     */
    public void goForward(){
        this.webView.loadUrl(forwardStack.pop());
    }


    /**
     * goes forward if it can
     */
    public void goForwardIfPossible(){
        if(canGoForward()){
            goForward();
        }
    }

    @Override
    public void load(String data, Context context) {
        String[] c = MoFile.loadable(data);
        if(MoFile.isValidData(c)){
            String[] stk = MoFile.loadable(c[0]);
            this.backwardStack.addAll(Arrays.asList(stk));

            if(c.length > 1){
                String[] p = MoFile.loadable(c[1]);
                this.forwardStack.addAll(Arrays.asList(p));
            }
        }
    }

    @Override
    public String getData() {
        return MoFile.getData(this.backwardStack,this.forwardStack);
    }
}
