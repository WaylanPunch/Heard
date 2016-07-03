package com.way.heard.utils;

import android.content.Context;
import android.os.AsyncTask;

/**
 * Created by pc on 2016/4/26.
 */
public abstract class NetAsyncTask extends AsyncTask<Void, Void, Void> {
    private final static String TAG = NetAsyncTask.class.getName();

    protected Context ctx;
    Exception exception;

    protected NetAsyncTask(Context ctx) {
        this.ctx = ctx;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        onPre();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doInBack();
        } catch (Exception e) {
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        onPost(exception);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        onCancel();
    }

    protected abstract void onPre();

    protected abstract void doInBack() throws Exception;

    protected abstract void onPost(Exception e);

    protected abstract void onCancel();
}

