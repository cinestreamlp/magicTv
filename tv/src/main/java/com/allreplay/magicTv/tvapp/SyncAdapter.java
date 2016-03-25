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

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.SyncResult;
import android.database.Cursor;
import android.media.tv.TvContract;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import org.magictvapi.Callback;
import org.magictvapi.ChannelManager;
import org.magictvapi.model.Channel;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A SyncAdapter implementation which updates program info periodically.
 */
class SyncAdapter extends AbstractThreadedSyncAdapter {
    public static final String TAG = "SyncAdapter";

    public static final String BUNDLE_KEY_INPUT_ID = "bundle_key_input_id";
    public static final String BUNDLE_KEY_CURRENT_PROGRAM_ONLY = "bundle_key_current_program_only";
    public static final long FULL_SYNC_FREQUENCY_SEC = 60 * 60 * 24;  // daily
    private static final int FULL_SYNC_WINDOW_SEC = 60 * 60 * 24 * 14;  // 2 weeks
    private static final int SHORT_SYNC_WINDOW_SEC = 60 * 60;  // 1 hour
    private static final int BATCH_OPERATION_COUNT = 100;

    private final Context mContext;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContext = context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContext = context;
    }

    /**
     * Called periodically by the system in every {@code FULL_SYNC_FREQUENCY_SEC}.
     */
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "onPerformSync(" + account + ", " + authority + ", " + extras + ")");
        final String inputId = extras.getString(SyncAdapter.BUNDLE_KEY_INPUT_ID);
        if (inputId == null) {
            return;
        }
        boolean currentProgramOnly = extras.getBoolean(
                SyncAdapter.BUNDLE_KEY_CURRENT_PROGRAM_ONLY, false);
        long startMs = System.currentTimeMillis();
        long endMs = startMs + FULL_SYNC_WINDOW_SEC * 1000;
        if (currentProgramOnly) {
            // This is requested from the setup activity, in this case, users don't need to wait for
            // the full sync. Sync the current programs first and do the full sync later in the
            // background.
            endMs = startMs + SHORT_SYNC_WINDOW_SEC * 1000;
        }

        Map<Long, Channel> channels = buildChannelMap(getContext().getContentResolver(), inputId);
        Log.d(TAG, "found " + channels.size() + " channels");

        for (final Map.Entry<Long, Channel> channel : channels.entrySet()) {
            channel.getValue().getTvProgram(new Callback<TvProgram>() {
                @Override
                public void call(TvProgram param) {
                    param.getTvProgram(new Callback<List<Video>>() {
                        @Override
                        public void call(List<Video> programs) {
                            Log.d(TAG, "channel " + channel.getValue().getId() + " : " + programs.size() + " programs ");
                            Uri channelUri = TvContract.buildChannelUri(channel.getKey());
                            updatePrograms(channelUri, programs, channel.getKey());
                        }
                    });
                }
            });
        }
    }

    /**
     * Updates the system database, TvProvider, with the given programs.
     *
     * <p>If there is any overlap between the given and existing programs, the existing ones
     * will be updated with the given ones if they have the same title or replaced.
     *  @param channelUri The channel where the program info will be added.
     * @param newPrograms A list of {@link Video} instances which includes program
     */
    private void updatePrograms(Uri channelUri, List<Video> newPrograms, Long channelId) {
        final int fetchedProgramsCount = newPrograms.size();
        if (fetchedProgramsCount == 0) {
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (Video video : newPrograms) {
            Log.d("SyncAdapter", "add program " + video.getTitle());
            ops.add(ContentProviderOperation
                    .newInsert(TvContract.Programs.CONTENT_URI)
                    .withValues(buildProgramValues(video, channelId))
                    .build());
        }

        try {
            mContext.getContentResolver().applyBatch(TvContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e(TAG, "Failed to insert programs.", e);
            return;
        }
        ops.clear();
    }

    /**
     * return channels loaded for this provider
     * @param resolver
     * @param inputId
     * @return
     */
    public static Map<Long, Channel> buildChannelMap(ContentResolver resolver, String inputId) {
        Uri uri = TvContract.buildChannelsUriForInput(inputId);
        String[] projection = {
                TvContract.Channels._ID,
                TvContract.Channels.COLUMN_DISPLAY_NUMBER
        };

        Map<Long, Channel> channelMap = new HashMap<>();
        try (Cursor cursor = resolver.query(uri, projection, null, null, null)) {
            if (cursor == null || cursor.getCount() == 0) {
                return null;
            }

            while (cursor.moveToNext()) {
                long channelId = cursor.getLong(0);
                int channelNumber = cursor.getInt(1);
                channelMap.put(channelId, ChannelManager.getChannel(channelNumber));
            }
        } catch (Exception e) {
            Log.d(TAG, e.getMessage(), e);
        }
        return channelMap;
    }

    private ContentValues buildProgramValues(Video video, Long channelId) {
        ContentValues values = new ContentValues();
        values.put(TvContract.Programs.COLUMN_CHANNEL_ID, channelId);
        values.put(TvContract.Programs.COLUMN_TITLE, video.getTitle());
        values.put(TvContract.Programs.COLUMN_LONG_DESCRIPTION, video.getDescription());
        values.put(TvContract.Programs.COLUMN_POSTER_ART_URI, video.getBackgroundImageUrl());
        values.put(TvContract.Programs.COLUMN_THUMBNAIL_URI, video.getImageUrl());
        values.put(TvContract.Programs.COLUMN_START_TIME_UTC_MILLIS, video.getPublicationDate().getTimeInMillis());
        values.put(TvContract.Programs.COLUMN_END_TIME_UTC_MILLIS, video.getPublicationDate().getTimeInMillis() + video.getDuration());

        return values;
    }
}
