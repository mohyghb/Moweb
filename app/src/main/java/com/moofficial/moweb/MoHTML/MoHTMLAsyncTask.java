package com.moofficial.moweb.MoHTML;

import android.os.AsyncTask;

import com.moofficial.moessentials.MoEssentials.MoRunnable.MoRunnable;
import com.moofficial.moweb.MoHTML.Html.MoHtml;

import java.io.IOException;

public class MoHTMLAsyncTask extends AsyncTask<Void, Void, Void> {


    private String url;
    private MoRunnable onHtmlReceived;
    private Runnable onHtmlError;
    private String html;

    public MoHTMLAsyncTask setUrl(String u) {
        this.url = u;
        return this;
    }


    public MoHTMLAsyncTask setOnHtmlReceived(MoRunnable u) {
        this.onHtmlReceived = u;
        return this;
    }

    public MoHTMLAsyncTask setOnHtmlError(Runnable u) {
        this.onHtmlError = u;
        return this;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            this.html = MoHtml.getHtml(this.url);
        } catch (IOException ignore) {
            if (this.onHtmlError != null)
                onHtmlError.run();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        if (this.onHtmlReceived != null)
            onHtmlReceived.run(this.html);
    }
}
