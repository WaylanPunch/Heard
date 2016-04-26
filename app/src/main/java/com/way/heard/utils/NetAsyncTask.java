package com.way.heard.utils;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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
        try {
            showProgress(true);
        } catch (Exception e) {
            Log.e(TAG, "onPreExecute error", e);
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            doInBack();
        } catch (Exception e) {
            Log.e(TAG, "doInBackground error", e);
            e.printStackTrace();
            exception = e;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        //showProgress(false);
        onPost(exception);
    }

    protected abstract void showProgress(boolean isDone);

    protected abstract void doInBack() throws Exception;

    protected abstract void onPost(Exception e);
}

