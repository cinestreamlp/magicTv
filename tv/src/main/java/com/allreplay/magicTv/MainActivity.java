package com.allreplay.magicTv;

import android.app.Activity;
import android.os.Bundle;

import org.magictvapi.Config;
import org.magictvapi.TvChainManager;

/**
 * Created by thomas on 16/03/2016.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Config.context = this.getBaseContext();
        TvChainManager.load();
        TvChainManager.initialize(this.getBaseContext());

        setContentView(R.layout.activity_main);
    }
}
