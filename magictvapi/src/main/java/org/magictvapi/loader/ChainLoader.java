package org.magictvapi.loader;

import android.os.AsyncTask;

import org.magictvapi.Callback;
import org.magictvapi.model.TvChain;

/**
 * Created by thomas on 12/03/2016.
 */
public abstract class ChainLoader extends AsyncTask<Void, Void, TvChain> {

    public Callback<TvChain> successCallback;

    public ChainLoader onSuccess(Callback<TvChain> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    @Override
    protected void onPostExecute(TvChain t) {
        super.onPostExecute(t);
        if (this.successCallback != null) {
            this.successCallback.call(t);
        }
    }
}
