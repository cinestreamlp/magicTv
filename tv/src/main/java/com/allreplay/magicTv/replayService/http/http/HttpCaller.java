package com.allreplay.magicTv.replayService.http.http;


import android.util.Log;

import java.io.IOException;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpCaller {

    private static final String TAG = "HttpCaller";

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();

    public final static int GET = 1;
    public final static int POST = 2;


    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * */
    public String makeServiceCall(String url, int method)  {
        return this.makeServiceCall(url, method, null);
    }

    /**
     * Making service call
     * @url - url to make request
     * @method - http request method
     * @params - http request params
     * */
    public String makeServiceCall(String url, int method, List<HttpParam> params)  {

        Request.Builder requestBulider = new Request.Builder();

        if (params != null && !params.isEmpty()) {
            switch (method) {
                case POST :
                    FormBody.Builder formBodyBuilder = new FormBody.Builder();
                    for (HttpParam param : params) {
                        formBodyBuilder.add(param.name, param.value);
                    }

                    requestBulider.post(formBodyBuilder.build());

                    Log.d(TAG, "call POST URL " + url + " with parameters " + params);
                    break;
                case GET :
                    url += "?";
                    for (HttpParam param : params) {
                        url += param.name+"="+param.value+"&";
                    }

                    Log.d(TAG, "call GET URL " + url);
                    break;
            }
        }

        Request request = requestBulider.url(url).build();

        Response response = null;
        try {
            response = HTTP_CLIENT.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

        return "";
    }
}