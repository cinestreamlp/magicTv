package com.allreplay.magicTv;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.DetailsFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ClassPresenterSelector;
import android.support.v17.leanback.widget.DetailsOverviewRow;
import android.support.v17.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import android.support.v17.leanback.widget.HeaderItem;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.util.DisplayMetrics;
import android.util.Log;

import com.allreplay.magicTv.replayService.http.AsyncClipLoader;
import com.allreplay.magicTv.replayService.http.AsyncVideosLoader;
import com.allreplay.magicTv.replayService.http.Categorie;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.magictvapi.model.Program;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class VideoDetailsFragment extends DetailsFragment {
    private static final String TAG = "VideoDetailsFragment";

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private static final int NUM_COLS = 10;

    private static final String PROGRAM = "Program";

    private Program mSelectedProgram;

    private Drawable mDefaultBackground;
    private Target mBackgroundTarget;
    private DisplayMetrics mMetrics;
    private FullWidthDetailsOverviewRowPresenter mDorPresenter;
    private DetailRowBuilderTask mDetailRowBuilderTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        mDorPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());

        BackgroundManager backgroundManager = BackgroundManager.getInstance(getActivity());
        backgroundManager.attach(getActivity().getWindow());
        mBackgroundTarget = new PicassoBackgroundManagerTarget(backgroundManager);

        mDefaultBackground = getResources().getDrawable(R.drawable.default_background);

        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        mSelectedProgram = (Program) getActivity().getIntent().getSerializableExtra(PROGRAM);
        mDetailRowBuilderTask = (DetailRowBuilderTask) new DetailRowBuilderTask().execute(mSelectedProgram);
        //mDorPresenter.setSharedElementEnterTransition(getActivity(),
        //        DetailsActivity.SHARED_ELEMENT_NAME);

        try {
            updateBackground(new URI(mSelectedProgram.getImageUrl()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        setOnItemViewClickedListener(new ItemViewClickedListener());

    }

    @Override
    public void onStop() {
        mDetailRowBuilderTask.cancel(true);
        super.onStop();
    }

    private class DetailRowBuilderTask extends AsyncTask<Program, Integer, DetailsOverviewRow> {
        @Override
        protected DetailsOverviewRow doInBackground(Program... movies) {
            mSelectedProgram = movies[0];

            DetailsOverviewRow row = new DetailsOverviewRow(mSelectedProgram);
            try {
                Bitmap poster = Picasso.with(getActivity())
                        .load(mSelectedProgram.getImageUrl())
                        .resize(Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH),
                                Utils.convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT))
                        .centerCrop()
                        .get();
                row.setImageBitmap(getActivity(), poster);

            } catch (IOException e) {
            }

            return row;
        }

        @Override
        protected void onPostExecute(DetailsOverviewRow detailRow) {
            ClassPresenterSelector ps = new ClassPresenterSelector();
            // set detail background and style
            mDorPresenter.setBackgroundColor(getResources().getColor(R.color.detail_background));

            ps.addClassPresenter(DetailsOverviewRow.class, mDorPresenter);
            ps.addClassPresenter(ListRow.class, new ListRowPresenter());

            ArrayObjectAdapter adapter = new ArrayObjectAdapter(ps);
            adapter.add(detailRow);


            AsyncVideosLoader videoLoader = new AsyncVideosLoader();
            videoLoader.execute(mSelectedProgram.getId());
            try {
                List<Categorie> categories = videoLoader.get();


                for (Categorie categorie: categories) {
                    ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new MovieCardPresenter());

                    /*for (Movie movie: categorie.getMovies()) {
                        if (movie.getImage() == null) {
                            movie.setImage(mSelectedProgram.getImageUrl());
                        }
                        listRowAdapter.add(movie);
                    }*/

                    HeaderItem header = new HeaderItem(0, categorie.getName());
                    adapter.add(new ListRow(header, listRowAdapter));
                }
                setAdapter(adapter);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + ((Movie) item).getTitle());

                AsyncClipLoader clipLoader = new AsyncClipLoader();
                clipLoader.execute(movie.getId());
                try {
                    Clip clip = clipLoader.get();
                    Log.i(TAG, clip.getFilePath());
                    MediaPlayer mediaplayer = new MediaPlayer();
                    try {
                        mediaplayer.setDataSource("http://e114.cdn.m6web.fr/prime/" + clip.getFilePath());
                        mediaplayer.prepare();
                        mediaplayer.start();
                    } catch (Exception e) {
                        Log.e("test", e.getMessage(), e);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }
        }
    }

    protected void updateBackground(URI uri) {
        Log.d(TAG, "uri" + uri);
        Log.d(TAG, "metrics" + mMetrics.toString());
        Picasso.with(getActivity())
                .load(uri.toString())
                .resize(mMetrics.widthPixels, mMetrics.heightPixels)
                .error(mDefaultBackground)
                .into(mBackgroundTarget);
    }

}
