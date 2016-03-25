package org.magictvapi.channel.tf1;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.magictvapi.channel.tf1.loader.Tf1Database;

/**
 * Created by thomas on 18/03/2016.
 */
public class Initializer extends AsyncTask<Context, Void, Void> {
    @Override
    protected Void doInBackground(Context... params) {
        // download tf1 database
        Log.d("Initializer", "init database ");
        new Tf1Database(params[0]);
        return null;
    }
}
