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

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.tv.TvContract;
import android.media.tv.TvInputManager;
import android.media.tv.TvInputService;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.Surface;

import org.magictvapi.Callback;
import org.magictvapi.ChannelManager;
import org.magictvapi.model.Channel;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Simple TV input service which provides two sample channels.
 * <p>
 * NOTE: The purpose of this sample is to provide a really simple TV input sample to the developers
 * so that they can understand the core APIs and when/how/where they should use them with ease.
 * This means lots of features including EPG, subtitles, multi-audio, parental controls, and overlay
 * view are missing here. So, to check the example codes for them, see
 * </p>
 */
public class SimpleTvInputService extends TvInputService {
    @Override
    public Session onCreateSession(String inputId) {
        return new SimpleSessionImpl(this);
    }

    /**
     * Simple session implementation which plays local videos on the application's tune request.
     */
    private class SimpleSessionImpl extends Session {
        private final Context context;
        //private static final int RESOURCE_1 =
        //        R.raw.video_176x144_3gp_h263_300kbps_25fps_aac_stereo_128kbps_22050hz;
        //private static final int RESOURCE_2 =
        //        R.raw.video_480x360_mp4_h264_1350kbps_30fps_aac_stereo_192kbps_44100hz;

        private MediaPlayer mPlayer;
        private float mVolume;
        private Surface mSurface;

        SimpleSessionImpl(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public void onRelease() {
            if (mPlayer != null) {
                mPlayer.release();
            }
        }

        @Override
        public boolean onSetSurface(Surface surface) {
            if (mPlayer != null) {
                mPlayer.setSurface(surface);
            }
            mSurface = surface;
            return true;
        }

        @Override
        public void onSetStreamVolume(float volume) {
            if (mPlayer != null) {
                mPlayer.setVolume(volume, volume);
            }
            mVolume = volume;
        }

        @Override
        public boolean onTune(final Uri channelUri) {
            String[] projection = {TvContract.Channels.COLUMN_SERVICE_ID, BaseColumns._ID};
            Log.d("SimpleTvInputService", "channelUri = " + channelUri);

            Cursor cursor = null;
            final ContentResolver contentResolver = getContentResolver();
            try {
                cursor = getContentResolver().query(channelUri, projection, null, null, null);
                if (cursor == null || cursor.getCount() == 0) {
                    Log.e("SimpleTvInputService", "No channel found");
                    return false;
                }
                cursor.moveToNext();
                final Long channelId = cursor.getLong(1);
                // get channel from id

                final Integer channelNumber = cursor.getInt(0);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                        final Channel channel = ChannelManager.getChannel(channelNumber);
                        if (channel != null) {
                            channel.getTvProgram(new Callback<TvProgram>() {
                                @Override
                                public void call(TvProgram param) {

                                    param.getCurrentPlayedVideo(new Callback<Video>() {
                                        @Override
                                        public void call(Video video) {
                                            ArrayList<Video> videos = new ArrayList<Video>();
                                            videos.add(video);
                                            updatePrograms(channelUri, videos, channelId);
                                            video.getDataUrl(new Callback<String>() {
                                                @Override
                                                public void call(String param) {
                                                    startPlayback(param);

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        } else {
                            Log.e("SimpleTvInputService", "erreur la chaine " + channelNumber + "n'existe pas");
                        }
                        return null;
                    }
                }.execute();

            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }

            return true;
            //return startPlayback(resource);
            // NOTE: To display the program information (e.g. title) properly in the channel banner,
            // The implementation needs to register the program metadata on TvProvider.
            // For the example implementation, please see {@link RichTvInputService}.
        }

        @Override
        public void onSetCaptionEnabled(boolean enabled) {
            // The sample content does not have caption. Nothing to do in this sample input.
            // NOTE: If the channel has caption, the implementation should turn on/off the caption
            // based on {@code enabled}.
            // For the example implementation for the case, please see {@link RichTvInputService}.
        }

        private boolean startPlayback(String url) {
            if (mPlayer == null) {
                mPlayer = new MediaPlayer();
                mPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                    @Override
                    public boolean onInfo(MediaPlayer player, int what, int arg) {
                        // NOTE: TV input should notify the video playback state by using
                        // {@code notifyVideoAvailable()} and {@code notifyVideoUnavailable() so
                        // that the application can display back screen or spinner properly.
                        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                            notifyVideoUnavailable(
                                    TvInputManager.VIDEO_UNAVAILABLE_REASON_BUFFERING);
                            return true;
                        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END
                                || what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            notifyVideoAvailable();
                            return true;
                        }
                        return false;
                    }
                });
                mPlayer.setSurface(mSurface);
                mPlayer.setVolume(mVolume, mVolume);
            } else {
                mPlayer.reset();
            }
            mPlayer.setLooping(true);
            try {
                mPlayer.setDataSource(url);
                mPlayer.prepare();
                mPlayer.start();
            } catch (IOException e) {
                return false;
            }
            // The sample content does not have rating information. Just allow the content here.
            // NOTE: If the content might include problematic scenes, it should not be allowed.
            // Also, if the content has rating information, the implementation should allow the
            // content based on the current rating settings by using
            // {@link android.media.tv.TvInputManager#isRatingBlocked()}.
            // For the example implementation for the case, please see {@link RichTvInputService}.
            notifyContentAllowed();
            return true;
        }
    }




    /****** DE LA MERDE *****/
    private ContentValues buildProgramValues(Video video, long channelId) {
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

    /**
     * Updates the system database, TvProvider, with the given programs.
     *
     * <p>If there is any overlap between the given and existing programs, the existing ones
     * will be updated with the given ones if they have the same title or replaced.
     * @param channelUri The channel where the program info will be added.
     * @param newPrograms A list of {@link Video} instances which includes program
     * @param channelId
     */
    private void updatePrograms(Uri channelUri, List<Video> newPrograms, long channelId) {
        final int fetchedProgramsCount = newPrograms.size();
        if (fetchedProgramsCount == 0) {
            return;
        }

        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        for (Video video : newPrograms) {
            Log.d("SimpleTvInputService", "add program " + video.getTitle());
            ops.add(ContentProviderOperation
                    .newInsert(TvContract.Programs.CONTENT_URI)
                    .withValues(buildProgramValues(video, channelId))
                    .build());
        }

        try {
            this.getContentResolver().applyBatch(TvContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            Log.e("SimpleTvInputService", "Failed to insert programs.", e);
            return;
        }
        ops.clear();
    }
}
