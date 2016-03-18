/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.allreplay.magicTv;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.widget.AbstractDetailsDescriptionPresenter;
import android.support.v17.leanback.widget.Action;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.ControlButtonPresenterSelector;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnActionClickedListener;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.PlaybackControlsRow;
import android.support.v17.leanback.widget.PlaybackControlsRow.FastForwardAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.PlayPauseAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RepeatAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.RewindAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ShuffleAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipNextAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.SkipPreviousAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsDownAction;
import android.support.v17.leanback.widget.PlaybackControlsRow.ThumbsUpAction;
import android.support.v17.leanback.widget.PlaybackControlsRowPresenter;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.Log;
import android.widget.VideoView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.magictvapi.Callback;
import org.magictvapi.model.DirectVideo;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Calendar;

/*
 * Class for video playback with media control
 */
public class PlaybackOverlayFragment extends android.support.v17.leanback.app.PlaybackOverlayFragment {
    private static final String TAG = "PlaybackControlsFragmnt";

    private static Context sContext;

    private static final boolean SHOW_DETAIL = true;
    private static final boolean HIDE_MORE_ACTIONS = false;
    private static final int PRIMARY_CONTROLS = 3;
    private static final boolean SHOW_IMAGE = PRIMARY_CONTROLS <= 5;
    private static final int BACKGROUND_TYPE = PlaybackOverlayFragment.BG_LIGHT;
    private static final int CARD_WIDTH = 200;
    private static final int CARD_HEIGHT = 240;
    private static final int DEFAULT_UPDATE_PERIOD = 1000;
    private static final int UPDATE_PERIOD = 16;
    private static final int SIMULATED_BUFFERED_TIME = 10000;

    private ArrayObjectAdapter mRowsAdapter;
    private ArrayObjectAdapter mPrimaryActionsAdapter;
    private ArrayObjectAdapter mSecondaryActionsAdapter;
    private PlayPauseAction mPlayPauseAction;
    private RepeatAction mRepeatAction;
    private ThumbsUpAction mThumbsUpAction;
    private ThumbsDownAction mThumbsDownAction;
    private ShuffleAction mShuffleAction;
    private FastForwardAction mFastForwardAction;
    private RewindAction mRewindAction;
    private SkipNextAction mSkipNextAction;
    private SkipPreviousAction mSkipPreviousAction;
    private PlaybackControlsRow mPlaybackControlsRow;
    private Video mItem = null;
    private Handler mHandler;
    private Runnable mRunnable;
    private PicassoPlaybackControlsRowTarget mPlaybackControlsRowTarget;
    OnVideoActionListener mCallback;
    private int startOffset = 0;
    private TvProgram mTvProgram;

    // Container Activity must implement this interface
    public interface OnVideoActionListener {
         void onFragmentPlayPause(Video movie, int position, Boolean playPause);
         void onSeekTo(int position);
         void setCurrentItem(Video movie);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        sContext = getActivity();

        mItem = (Video) getActivity()
                .getIntent().getSerializableExtra(DetailsActivity.MOVIE);
        mTvProgram = (TvProgram) getActivity()
                .getIntent().getSerializableExtra(DetailsActivity.TV_PROGRAM);

        mHandler = new Handler();

        setBackgroundType(BACKGROUND_TYPE);
        setFadingEnabled(false);

        setupRows();

        setOnItemViewSelectedListener(new OnItemViewSelectedListener() {
            @Override
            public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                       RowPresenter.ViewHolder rowViewHolder, Row row) {
                Log.i(TAG, "onItemSelected: " + item + " row " + row);
            }
        });
        setOnItemViewClickedListener(new OnItemViewClickedListener() {
            @Override
            public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                      RowPresenter.ViewHolder rowViewHolder, Row row) {
                Log.i(TAG, "onItemClicked: " + item + " row " + row);
            }
        });

        // lancement de la video
        mCallback.setCurrentItem(mItem);
        startProgressAutomation();
        setFadingEnabled(true);
        mPlayPauseAction.setIndex(PlayPauseAction.PAUSE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (OnVideoActionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnVideoActionListener");
        }
    }

    private void setupRows() {

        ClassPresenterSelector ps = new ClassPresenterSelector();

        PlaybackControlsRowPresenter playbackControlsRowPresenter;
        if (SHOW_DETAIL) {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter(
                    new DescriptionPresenter());
        } else {
            playbackControlsRowPresenter = new PlaybackControlsRowPresenter();
        }
        playbackControlsRowPresenter.setOnActionClickedListener(new OnActionClickedListener() {
            public void onActionClicked(Action action) {
                if (action.getId() == mPlayPauseAction.getId()) {
                    if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
                        startProgressAutomation();
                        setFadingEnabled(true);
                        mCallback.onFragmentPlayPause(mItem,
                                mPlaybackControlsRow.getCurrentTime(), true);
                    } else {
                        stopProgressAutomation();
                        setFadingEnabled(false);
                        mCallback.onFragmentPlayPause(mItem,
                                mPlaybackControlsRow.getCurrentTime(), false);
                    }
                } else if (action.getId() == mSkipNextAction.getId()) {
                    //next();
                } else if (action.getId() == mSkipPreviousAction.getId()) {
                    // prev();
                } else if (action.getId() == mFastForwardAction.getId()) {
                    int newTime = mPlaybackControlsRow.getCurrentTime() + 10000;
                    if (newTime > mItem.getDuration()) {
                        newTime = (int) mItem.getDuration();
                    }
                    mPlaybackControlsRow.setCurrentTime(newTime);
                    mCallback.onSeekTo(newTime);
                } else if (action.getId() == mRewindAction.getId()) {
                    int newTime = mPlaybackControlsRow.getCurrentTime() - 10000;
                    if (newTime < 0) {
                        newTime = 0;
                    }
                    mPlaybackControlsRow.setCurrentTime(newTime);
                    mCallback.onSeekTo(newTime);
                }
                if (action instanceof PlaybackControlsRow.MultiAction) {
                    ((PlaybackControlsRow.MultiAction) action).nextIndex();
                    notifyChanged(action);
                }
            }
        });
        playbackControlsRowPresenter.setSecondaryActionsHidden(HIDE_MORE_ACTIONS);

        ps.addClassPresenter(PlaybackControlsRow.class, playbackControlsRowPresenter);
        ps.addClassPresenter(ListRow.class, new ListRowPresenter());
        mRowsAdapter = new ArrayObjectAdapter(ps);

        addPlaybackControlsRow();
        //addOtherRows();

        setAdapter(mRowsAdapter);
    }

    private int getDuration() {
        return (int) mItem.getDuration();
    }

    private void addPlaybackControlsRow() {
        if (SHOW_DETAIL) {
            mPlaybackControlsRow = new PlaybackControlsRow(mItem);
        } else {
            mPlaybackControlsRow = new PlaybackControlsRow();
        }
        mRowsAdapter.add(mPlaybackControlsRow);

        updatePlaybackRow();

        ControlButtonPresenterSelector presenterSelector = new ControlButtonPresenterSelector();
        mPrimaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        //mSecondaryActionsAdapter = new ArrayObjectAdapter(presenterSelector);
        mPlaybackControlsRow.setPrimaryActionsAdapter(mPrimaryActionsAdapter);
        //mPlaybackControlsRow.setSecondaryActionsAdapter(mSecondaryActionsAdapter);

        mPlayPauseAction = new PlayPauseAction(sContext);
        mRepeatAction = new RepeatAction(sContext);
        mThumbsUpAction = new ThumbsUpAction(sContext);
        mThumbsDownAction = new ThumbsDownAction(sContext);
        mShuffleAction = new ShuffleAction(sContext);
        mSkipNextAction = new PlaybackControlsRow.SkipNextAction(sContext);
        mSkipPreviousAction = new PlaybackControlsRow.SkipPreviousAction(sContext);
        mFastForwardAction = new PlaybackControlsRow.FastForwardAction(sContext);
        mRewindAction = new PlaybackControlsRow.RewindAction(sContext);

        mPrimaryActionsAdapter.add(mRewindAction);
        mPrimaryActionsAdapter.add(mPlayPauseAction);
        mPrimaryActionsAdapter.add(mFastForwardAction);

    }

    private void notifyChanged(Action action) {
        ArrayObjectAdapter adapter = mPrimaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
        adapter = mSecondaryActionsAdapter;
        if (adapter.indexOf(action) >= 0) {
            adapter.notifyArrayItemRangeChanged(adapter.indexOf(action), 1);
            return;
        }
    }

    private void updatePlaybackRow() {
        if (mPlaybackControlsRow.getItem() != null) {
            Video item = (Video) mPlaybackControlsRow.getItem();
            item.setTitle(mItem.getTitle());
            //item.setStudio(mItems.get(mCurrentItem).getStudio());
        }
        if (SHOW_IMAGE && mItem.getImageUrl() != null) {
            mPlaybackControlsRowTarget = new PicassoPlaybackControlsRowTarget(mPlaybackControlsRow);
            try {
                updateVideoImage(new URI(mItem.getImageUrl()));
            } catch (URISyntaxException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
        mRowsAdapter.notifyArrayItemRangeChanged(0, 1);
        mPlaybackControlsRow.setTotalTime(getDuration());

        // init start offset for direct input
        if (mItem instanceof DirectVideo) {
            PlaybackOverlayActivity activity = ((PlaybackOverlayActivity) PlaybackOverlayFragment.this.getActivity());
            VideoView view = activity == null ? null : activity.getVideoView();

            Calendar now = Calendar.getInstance();
            startOffset = (int)(now.getTimeInMillis() - mItem.getPublicationDate().getTimeInMillis());
            if (view != null) {
                startOffset -= view.getCurrentPosition();
            }
            mPlaybackControlsRow.setCurrentTime(startOffset);
        }
        mPlaybackControlsRow.setBufferedProgress(0);
    }

    private void addOtherRows() {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new ProgramCardPresenter());
        /*for (Video movie : mItems) {
            listRowAdapter.add(movie);
        }*/
        HeaderItem header = new HeaderItem(0, getString(R.string.related_movies));
        mRowsAdapter.add(new ListRow(header, listRowAdapter));

    }

    private int getUpdatePeriod() {
        if (getView() == null || mPlaybackControlsRow.getTotalTime() <= 0) {
            return DEFAULT_UPDATE_PERIOD;
        }
        return Math.max(UPDATE_PERIOD, mPlaybackControlsRow.getTotalTime() / getView().getWidth());
    }

    private void startProgressAutomation() {
        mRunnable = new Runnable() {
            @Override
            public void run() {
                VideoView view = this.getVideoView();
                int updatePeriod = getUpdatePeriod();
                int currentTime = view == null ? mPlaybackControlsRow.getCurrentTime() + updatePeriod : startOffset + view.getCurrentPosition();
                int totalTime = mPlaybackControlsRow.getTotalTime();
                mPlaybackControlsRow.setCurrentTime(currentTime);
                mPlaybackControlsRow.setBufferedProgress(currentTime + SIMULATED_BUFFERED_TIME);

                if (totalTime > 0 && totalTime <= currentTime) {
                    // if it's a direct video load next info
                    if (mItem instanceof DirectVideo && mTvProgram != null) {
                        loadNextVideo();
                        mHandler.postDelayed(this, updatePeriod);
                    } else {
                        stopProgressAutomation();
                    }
                } else {
                    mHandler.postDelayed(this, updatePeriod);
                }
            }

            private VideoView videoView;
            public VideoView getVideoView() {
                PlaybackOverlayActivity activity = ((PlaybackOverlayActivity) PlaybackOverlayFragment.this.getActivity());
                if (videoView == null && activity != null) {
                    videoView = activity.getVideoView();
                }
                return videoView;
            }
        };
        mHandler.postDelayed(mRunnable, getUpdatePeriod());
    }

    /**
     * load next video if current video is a direct video
     */
    private void loadNextVideo() {
        mTvProgram.getCurrentPlayedVideo(new Callback<Video>() {
            @Override
            public void call(Video param) {
                mItem = param;
                updatePlaybackRow();
            }
        });
    }

    private void next() {
        /*if (++mCurrentItem >= mItems.size()) {
            mCurrentItem = 0;
        }

        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
        */
    }

    private void prev() {
        /*
        if (--mCurrentItem < 0) {
            mCurrentItem = mItems.size() - 1;
        }
        if (mPlayPauseAction.getIndex() == PlayPauseAction.PLAY) {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, false);
        } else {
            mCallback.onFragmentPlayPause(mItems.get(mCurrentItem), 0, true);
        }
        updatePlaybackRow(mCurrentItem);
        */
    }

    private void stopProgressAutomation() {
        if (mHandler != null && mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    public void onStop() {
        stopProgressAutomation();
        super.onStop();
    }

    static class DescriptionPresenter extends AbstractDetailsDescriptionPresenter {
        @Override
        protected void onBindDescription(ViewHolder viewHolder, Object item) {
            viewHolder.getTitle().setText(((Video) item).getTitle());
           viewHolder.getSubtitle().setText(((Video) item).getSubTitle());
        }
    }

    public static class PicassoPlaybackControlsRowTarget implements Target {
        PlaybackControlsRow mPlaybackControlsRow;

        public PicassoPlaybackControlsRowTarget(PlaybackControlsRow playbackControlsRow) {
            mPlaybackControlsRow = playbackControlsRow;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            Drawable bitmapDrawable = new BitmapDrawable(sContext.getResources(), bitmap);
            mPlaybackControlsRow.setImageDrawable(bitmapDrawable);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            mPlaybackControlsRow.setImageDrawable(drawable);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            // Do nothing, default_background manager has its own transitions
        }
    }

    protected void updateVideoImage(URI uri) {
        Picasso.with(sContext)
                .load(uri.toString())
                .resize(Utils.convertDpToPixel(sContext, CARD_WIDTH),
                        Utils.convertDpToPixel(sContext, CARD_HEIGHT))
                .into(mPlaybackControlsRowTarget);
    }

}
