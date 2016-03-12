package com.allreplay.magicTv.replayService.http;

import android.os.AsyncTask;
import android.util.Log;

import com.allreplay.magicTv.Clip;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by deblock on 21/02/2016.
 */
public class AsyncClipLoader extends AsyncTask<Integer, Void, Clip> {

    private static final String API_URL = "https://android.middleware.6play.fr/6play/v1/platforms/m6group_androidmob/services/6play/clips/";
    private static final String TAG = "AsyncClipLoader";


    @Override
    protected Clip doInBackground(Integer... params) {
        HttpCaller caller = new HttpCaller();
        String jsonStr = caller.makeServiceCall(API_URL + params[0] , HttpCaller.GET);

        try {
            JSONObject object = new JSONObject(jsonStr);
            return  new Clip(object.getJSONArray("assets").getJSONObject(0).getJSONObject("_links").getJSONObject("physical").getString("href"));
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Clip jsonObject) {
        super.onPostExecute(jsonObject);
    }
}
