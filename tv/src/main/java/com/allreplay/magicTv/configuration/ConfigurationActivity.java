package com.allreplay.magicTv.configuration;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v17.leanback.app.GuidedStepFragment;

import com.allreplay.magicTv.R;

/**
 * Created by thomas on 03/09/16.
 */
public class ConfigurationActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_configuration);

        GuidedStepFragment.add(getFragmentManager(), new KodiGuidedStepFragment());
    }
}
