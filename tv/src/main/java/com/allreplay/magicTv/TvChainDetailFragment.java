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

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.magictvapi.Callback;
import org.magictvapi.ChannelManager;
import org.magictvapi.Config;
import org.magictvapi.model.Channel;
import org.magictvapi.model.Folder;
import org.magictvapi.model.Program;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class TvChainDetailFragment extends BrowseFragment {
    private static final String TAG = "TvChainDetailFragment";
    public static final String CHANNEL_ID = "chain-id";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    private ArrayObjectAdapter mRowsAdapter;
    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private final Handler mHandler = new Handler();
    private URI mBackgroundURI;
    ProgramCardPresenter mProgramCardPresenter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    public void loadBadge(final Channel channel) {
        AsyncTask<Void, Void, BitmapDrawable> task = new AsyncTask<Void, Void, BitmapDrawable>() {
            @Override
            protected BitmapDrawable doInBackground(Void... params) {
                try {
                    return new BitmapDrawable(null, new URL(channel.getImageUrl()).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(BitmapDrawable image) {
                super.onPostExecute(image);
                try {
                    setBadgeDrawable(image);
                } catch (Exception e) {
                    setTitle(channel.getTitle());
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        };
        task.execute();
    }

    private void loadRows() {
        // load M6 chains
        // TODO load others chains
        mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);

        Config.context = this.getActivity();
        Channel channel = null;
        // comme on from direct tv or from magic Tv
        final String channelId = getActivity().getIntent().getStringExtra(CHANNEL_ID);
        final boolean fromLiveChannel = channelId != null;
        if (channelId != null) {
            channel = ChannelManager.getChannel(Integer.parseInt(channelId));
            getView().setBackgroundColor(getResources().getColor(R.color.black));
        } else {
            channel = (Channel) getActivity().getIntent().getSerializableExtra(DetailsActivity.CHAIN);
        }
        if (channel == null) {
            Log.e("TvChainDetailFragment", "Erreur aucune paramètre de chaine ("+CHANNEL_ID+" ou "+DetailsActivity.CHAIN+" )");
        }
        loadBadge(channel);

        channel.getFolders(new Callback<List<Folder>>() {
            @Override
            public void call(final List<Folder> folders) {
                mProgramCardPresenter = new ProgramCardPresenter();

                for (Folder folder : folders) {
                    final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(mProgramCardPresenter);
                    folder.getPrograms(new Callback<List<Program>>() {
                        @Override
                        public void call(List<Program> programs) {
                            listRowAdapter.addAll(0, programs);
                        }
                    });

                    HeaderItem header = new HeaderItem(folder.getId(), folder.getTitle());
                    mRowsAdapter.add(new ListRow(header, listRowAdapter));
                }
            }
        });
        // dont load direct tile when we come from live channel app
        if (!fromLiveChannel) {
            channel.getTvProgram(new Callback<TvProgram>() {
                @Override
                public void call(final TvProgram tvProgram) {
                    tvProgram.getCurrentPlayedVideo(new Callback<Video>() {
                        @Override
                        public void call(Video currentVideo) {
                            if (currentVideo != null) {
                                HeaderItem header = new HeaderItem(0, "Direct");
                                final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
                                listRowAdapter.add(tvProgram);
                                mRowsAdapter.add(0, new ListRow(header, listRowAdapter));
                                setSelectedPosition(0);
                            }
                        }
                    });

                }
            });
        }
    }

    private void prepareBackgroundManager() {

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(getResources().getColor(R.color.search_opaque));
    }

    private void setupEventListeners() {
        /*setOnSearchClickedListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Implement your own in-app search", Toast.LENGTH_LONG)
                        .show();
            }
        });
        */
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, final Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Program) {
                Program program = (Program) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra(DetailsActivity.PROGRAM, program);

               /* Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME).toBundle();*/
                getActivity().startActivity(intent/*, bundle*/);
            } else  if (item instanceof TvProgram) {
                ((TvProgram) item).getCurrentPlayedVideo(new Callback<Video>() {
                    @Override
                    public void call(Video movie) {
                        Intent intent = new Intent(getActivity(), PlaybackOverlayActivity.class);
                        intent.putExtra(DetailsActivity.MOVIE, movie);
                        if (item instanceof TvProgram) {
                            intent.putExtra(DetailsActivity.TV_PROGRAM, (TvProgram) item);
                        }

                        getActivity().startActivity(intent);
                    }
                });

            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
            if (item instanceof Video) {/*
                try {
                    mBackgroundURI = new URI(((Video) item).getBackgroundImageUrl());
                    startBackgroundTimer();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }*/
            }

        }
    }

    protected void setDefaultBackground(Drawable background) {
        mDefaultBackground = background;
    }

    protected void setDefaultBackground(int resourceId) {
        mDefaultBackground = getResources().getDrawable(resourceId);
    }

    protected void updateBackground(URI uri) {
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .centerCrop()
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }

    protected void updateBackground(Drawable drawable) {
        BackgroundManager.getInstance(getActivity()).setDrawable(drawable);
    }

    protected void clearBackground() {
        BackgroundManager.getInstance(getActivity()).setDrawable(mDefaultBackground);
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (mBackgroundURI != null) {
                        updateBackground(mBackgroundURI);
                    }
                }
            });

        }
    }

    private class GridItemPresenter extends Presenter {
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(getResources().getColor(R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

}
