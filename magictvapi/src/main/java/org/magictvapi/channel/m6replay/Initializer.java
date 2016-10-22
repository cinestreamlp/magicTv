package org.magictvapi.channel.m6replay;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.magictvapi.Callback;
import org.magictvapi.channel.m6replay.service.SfrTvLoger;

/**
 * Created by thomas on 03/09/16.
 */
public class Initializer extends AsyncTask<Context, Void, Void> {
    public Callback<String> successCallback;

    public Initializer onSuccess(Callback<String> successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    @Override
    protected Void doInBackground(Context... params) {
        // download tf1 database
        Log.d("Initializer", "Login to sfr api");
        // refresh token
        SfrTvLoger.INSTANCE.getToken();
        return null;
    }
}
