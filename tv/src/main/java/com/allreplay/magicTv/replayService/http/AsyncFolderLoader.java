package com.allreplay.magicTv.replayService.http;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by deblock on 21/02/2016.
 */
public class AsyncFolderLoader extends AsyncTask<Void, Void, JSONArray> {

    private static final String API_URL = "https://android.middleware.6play.fr/6play/v1";
    private static final String TAG = "AsyncFolderLoader";


    @Override
    protected JSONArray doInBackground(Void... params) {
        HttpCaller caller = new HttpCaller();
        String jsonStr = caller.makeServiceCall(API_URL + "/platforms/m6group_androidmob/folders?serviceCode=6play", HttpCaller.GET);

        try {
            return  new JSONArray(jsonStr);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONArray jsonObject) {
        super.onPostExecute(jsonObject);
    }
}
