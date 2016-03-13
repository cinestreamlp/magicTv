package org.magictvapi.loader;

import android.os.AsyncTask;

import org.magictvapi.Callback;
import org.magictvapi.model.TvChain;

/**
 * Created by thomas on 12/03/2016.
 */
public abstract class Loader<T> extends AsyncTask<Void, Void, T> {

    public Callback<T> successCallback;

    public Loader onSuccess(Callback<T> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    @Override
    protected void onPostExecute(T t) {
        super.onPostExecute(t);
        if (this.successCallback != null) {
            this.successCallback.call(t);
        }
    }
}
