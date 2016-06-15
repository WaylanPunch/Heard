package com.way.heard.services;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.avos.avoscloud.AVException;

/**
 * Created by pc on 2016/4/26.
 */
public abstract class LeanCloudBackgroundTask extends AsyncTask<Void, Void, Void> {
    private final static String TAG = LeanCloudBackgroundTask.class.getName();

    protected Context ctx;
    AVException exception;

    protected LeanCloudBackgroundTask(Context ctx) {
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
        } catch (AVException e) {
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

    @Override
    protected void onCancelled() {
        super.onCancelled();
        onCancel();
    }

    protected abstract void onPre();

    protected abstract void doInBack() throws AVException;

    protected abstract void onPost(AVException e);

    protected abstract void onCancel();
}

