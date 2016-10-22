package com.deblockt.phone;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.devbrackets.android.exomedia.ui.widget.EMVideoView;

import org.magictvapi.Callback;
import org.magictvapi.channel.m6replay.Initializer;
import org.magictvapi.channel.m6replay.model.M6Channel;
import org.magictvapi.channel.m6replay.model.TevaChannel;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.util.concurrent.ExecutionException;

import org.magictvapi.cookies.CookieManager;

public class MainActivity extends AppCompatActivity {
    private EMVideoView mVideoView;

    static CookieManager defaultCookieManager;
    static {
        defaultCookieManager = new CookieManager();
        defaultCookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ORIGINAL_SERVER);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("MainActivity", Build.VERSION.CODENAME);


        /*
        try {
            new Initializer().execute(this).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        CookieHandler currentHandler = CookieHandler.getDefault();
        if (currentHandler != defaultCookieManager) {
            CookieHandler.setDefault(defaultCookieManager);
        }

        mVideoView = (EMVideoView) findViewById(R.id.videoView);

        new TevaChannel().getTvProgram(new Callback<TvProgram>() {
            @Override
            public void call(TvProgram param) {
                param.getCurrentPlayedVideo(new Callback<Video>() {
                    @Override
                    public void call(Video param) {
                        param.getDataUrl(new Callback<String>() {
                            @Override
                            public void call(String param) {
                                mVideoView.setVideoURI(Uri.parse(param));
                                mVideoView.start();
                            }
                        });
                    }
                });
            }
        });*/


    }
}
