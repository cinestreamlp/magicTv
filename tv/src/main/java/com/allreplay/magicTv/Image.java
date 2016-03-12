package com.allreplay.magicTv;

import android.net.Uri;

import java.io.Serializable;
import java.util.Locale;

/**
 * Created by deblock on 27/02/2016.
 */
public class Image implements Serializable {
    private String id;

    public Image(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Uri getUri() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https").authority("images.6play.fr").appendEncodedPath("v1/images");
        builder.appendPath(this.id);
        builder.appendPath("raw");
        builder.appendQueryParameter("width", String.valueOf("1440"));
        builder.appendQueryParameter("fit", "max");
        builder.appendQueryParameter("quality", "90");

        Uri uri = builder.build();
        builder.appendQueryParameter("hash", String.valueOf(CryptoUtils.encodeHex(CryptoUtils.sha1Digest(String.format(Locale.getDefault(), "%s?%s%s", new Object[]{uri.getEncodedPath(), uri.getEncodedQuery(), "54b55408a530954b553ff79e98"})))));
        return builder.build();
    }
}
