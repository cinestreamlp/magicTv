package com.allreplay.magicTv.replayService.http;

import android.os.AsyncTask;
import android.util.Log;

import com.allreplay.magicTv.Image;
import com.allreplay.magicTv.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by deblock on 21/02/2016.
 */
public class AsyncVideosLoader extends AsyncTask<Integer, Void, List<Categorie>> {

    private static final String API_URL = "https://android.middleware.6play.fr/6play/v1/platforms/m6group_androidmob/services/6play/programs/";
    private static final String TAG = "AsyncProgramLoader";


    @Override
    protected  List<Categorie> doInBackground(Integer... params) {
        List<Categorie> categories = new ArrayList<>();

        com.allreplay.magicTv.replayService.http.http.HttpCaller caller = new com.allreplay.magicTv.replayService.http.http.HttpCaller();
        String jsonStr = caller.makeServiceCall(API_URL + params[0] + "?csa=6", com.allreplay.magicTv.replayService.http.http.HttpCaller.GET);
        try {
            JSONObject object = new JSONObject(jsonStr);
            JSONArray subcats = object.getJSONArray("programsubcats");
            if (subcats.length() == 0) {
                createSubCat(categories, null, "Vidéos intégrales", params[0]);
            }
            for (int i = 0; i < subcats.length(); i++) {
                JSONObject subcat = (JSONObject) subcats.get(i);
                createSubCat(categories, subcat.getString("id"), subcat.getString("title"), params[0]);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return categories;
    }

    private void createSubCat(List<Categorie> categories, String subCatid, String subCatTitle, Integer param) throws JSONException {
        Categorie cat = new Categorie(subCatTitle);
        categories.add(cat);
        com.allreplay.magicTv.replayService.http.http.HttpCaller caller = new com.allreplay.magicTv.replayService.http.http.HttpCaller();
        String jsonStr2 = caller.makeServiceCall(API_URL + param + "/videos?type=all" + (subCatid != null ? "&subcatId=" + subCatid : "") + "&limit=50&csa=6", com.allreplay.magicTv.replayService.http.http.HttpCaller.GET);
        JSONArray array = new JSONArray(jsonStr2);
        for (int y = 0; y < array.length(); y++) {
            JSONObject jsonMovie = (JSONObject) array.get(y);
            Movie movie = new Movie();
            movie.setId(jsonMovie.getInt("id"));
            movie.setTitle(jsonMovie.getString("title"));
            movie.setDescription(jsonMovie.getString("description"));
            try {movie.setDuration(jsonMovie.getInt("duration"));} catch (JSONException e) {};


            try {
                movie.setImage(loadVignette(jsonMovie.getJSONArray("clip_has_images")));
            } catch (JSONException e) {
                try {
                    movie.setImage(loadVignette(jsonMovie.getJSONArray("playlist_has_images")));
                } catch (JSONException e2) {
                    Log.e(TAG, e2.getMessage(), e);
                }
            }
            cat.addMovie(movie);
        }
    }

    public Image loadVignette(JSONArray clipHashImage) throws JSONException {
        for (int i = 0; i < clipHashImage.length(); i++)  {
            JSONObject clipHasImageObject = clipHashImage.getJSONObject(i);
            if ("vignette".equals(clipHasImageObject.getString("role"))) {
                return new Image(clipHasImageObject.getJSONObject("image").getString("external_key"));
            }
        }

        return new Image(clipHashImage.getJSONObject(0).getJSONObject("image").getString("external_key"));
    }

    @Override
    protected void onPostExecute(List<Categorie> jsonObject) {
        super.onPostExecute(jsonObject);
    }
}
