package com.allreplay.magicTv;

import android.app.Activity;
import android.os.Bundle;

import org.magictvapi.ChannelManager;
import org.magictvapi.Config;

/**
 * Created by thomas on 16/03/2016.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.context = this;
        ChannelManager.load();
        ChannelManager.initialize(this.getBaseContext());

        setContentView(R.layout.activity_main);
    }
}
