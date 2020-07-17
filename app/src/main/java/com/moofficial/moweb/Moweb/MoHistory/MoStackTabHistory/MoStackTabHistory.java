package com.moofficial.moweb.Moweb.MoHistory.MoStackTabHistory;


import android.content.Context;

import com.moofficial.moweb.MoIO.MoFile;
import com.moofficial.moweb.MoIO.MoLoadable;
import com.moofficial.moweb.MoIO.MoSavable;
import com.moofficial.moweb.Moweb.MoWebview.MoWebView;

import java.util.Arrays;
import java.util.Stack;

// a class to save all the searches of the user
// so we can go back if stack has anything in it
// it has a mutual connection with a mo web view
public class MoStackTabHistory implements MoSavable, MoLoadable {

    private Stack<String> stack = new Stack<>();
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
        if(this.stack.isEmpty() || !this.stack.peek().equals(url)){
            this.stack.add(url);
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
        return this.webView.getWebView() != null && this.stack.size()>1;
    }

    /**
     * pops a url from the stack
     * and peeks the next url (which is basically the previous page)
     * into the web view
     */
    public void goBack(){
        this.stack.pop();
        this.webView.loadUrl(this.stack.peek());
    }


    @Override
    public void load(String data, Context context) {
        String[] c = MoFile.loadable(data);
        this.stack.addAll(Arrays.asList(c));
    }

    @Override
    public String getData() {
        return MoFile.getData(this.stack);
    }
}
