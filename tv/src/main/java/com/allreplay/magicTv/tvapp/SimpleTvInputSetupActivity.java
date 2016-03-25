/*
 * Copyright 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.allreplay.magicTv.tvapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.media.tv.TvInputInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.magictvapi.ChannelManager;
import org.magictvapi.Config;
import org.magictvapi.model.Channel;

import java.io.OutputStream;
import java.net.URL;
import java.util.List;

/**
 * The setup activity for {@link SimpleTvInputService}.
 */
public class SimpleTvInputSetupActivity extends Activity {

    private String mInputId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInputId = getIntent().getStringExtra(TvInputInfo.EXTRA_INPUT_ID);

        DialogFragment newFragment = new MyAlertDialogFragment();
        newFragment.show(getFragmentManager(), "dialog");
    }

    private void registerChannels() {
        // Check if we already registered channels.
        Uri uri = TvContract.buildChannelsUriForInput(mInputId);
        String[] projection = {TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID};

        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, projection, null, null, null);

            while (cursor.moveToNext()) {
                try {
                    getContentResolver().delete(TvContract.buildChannelUri(cursor.getInt(0)), null, null);
                } catch (Exception e) {
                    Log.e("SimpleTvInputService", e.getMessage(), e);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        ContentResolver resolver = getContentResolver();

        getContentResolver().delete(TvContract.Channels.CONTENT_URI, null, null);

        ContentValues values = new ContentValues();
        values.put(TvContract.Channels.COLUMN_INPUT_ID, mInputId);

        Config.context = this.getBaseContext();
        List<Channel> chains = ChannelManager.getChains();
        for (Channel channel: chains) {
            values.put(TvContract.Channels.COLUMN_DISPLAY_NUMBER, channel.getId());
            values.put(TvContract.Channels.COLUMN_DISPLAY_NAME, channel.getTitle());
            values.put(TvContract.Channels.COLUMN_ORIGINAL_NETWORK_ID, channel.getId());
            values.put(TvContract.Channels.COLUMN_TRANSPORT_STREAM_ID, 0);
            values.put(TvContract.Channels.COLUMN_SERVICE_ID, channel.getId());
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

                values.put(TvContract.Channels.COLUMN_APP_LINK_TEXT, "Replay : " + channel.getTitle());
                values.put(TvContract.Channels.COLUMN_APP_LINK_INTENT_URI, "intent:#Intent;component=com.allreplay.magicTv/.TvChainDetailFromLiveChannelActivity;S.chain-id=" + channel.getId() + ";end");

                values.put(TvContract.Channels.COLUMN_APP_LINK_POSTER_ART_URI, channel.getImageUrl());
                //values.put(TvContract.Channels.COLUMN_APP_LINK_ICON_URI, channel.getImageUrl());
            }
            Uri channelUri = resolver.insert(TvContract.Channels.CONTENT_URI, values);
            Log.d("SimpleTvInputSetup", "Ajout de la chaine " + channel.getId() + " <=> " + values);

            try {
                AssetFileDescriptor fd = null;
                Uri channelLogoUri = TvContract.buildChannelLogoUri(channelUri);
                URL logoUrl = new URL(channel.getImageUrl());

                fd = resolver.openAssetFileDescriptor(channelLogoUri, "rw");
                OutputStream os = fd.createOutputStream();

                new InsertLogosTask(os, logoUrl).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        SyncUtils.setUpPeriodicSync(this, mInputId);
        SyncUtils.requestSync(mInputId, true);

    }

    public static class MyAlertDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle("Configuration des chaines")
                    .setMessage("La configuration des chaines va être executée. Cela peut prendre quelques minutes")
                    .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    ((SimpleTvInputSetupActivity) getActivity()).registerChannels();
                                    // Sets the results so that the application can process the
                                    // registered channels properly.
                                    getActivity().setResult(Activity.RESULT_OK);
                                    getActivity().finish();
                                }
                            }
                    )
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    getActivity().finish();
                                }
                            }
                    )
                    .create();
        }
    }
}
