package com.allreplay.magicTv.tvapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class InsertLogosTask extends AsyncTask<Void, Void, Void> {
    private final OutputStream os;
    private final URL logoUrl;


    public InsertLogosTask(OutputStream os, URL logoUrl) {
        this.os = os;
        this.logoUrl = logoUrl;
    }

    @Override
    public Void doInBackground(Void... logosList) {

        try {
            InputStream inputStream = logoUrl.openConnection().getInputStream();
            copy(inputStream, os);
        } catch (IOException e) {
            Log.e("InsertLogosTask", e.getMessage(), e);
        }
        return null;
    }

    public void copy(InputStream is, OutputStream os) throws IOException {
        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
    }
}