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

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v17.leanback.widget.ImageCardView;
import android.support.v17.leanback.widget.Presenter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.magictvapi.Callback;
import org.magictvapi.model.TvProgram;
import org.magictvapi.model.Video;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;

/*
 * A ProgramCardPresenter is used to generate Views and bind Objects to them on demand.
 * It contains an Image CardView
 */
public class MovieCardPresenter extends Presenter {
    private static final String TAG = "ProgramCardPresenter";

    private static Context mContext;
    private static int CARD_WIDTH = 400;
    private static int CARD_HEIGHT = 224;

    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    static class ViewHolder extends Presenter.ViewHolder {
        private Video mMovie;
        private ImageCardView mCardView;
        private Drawable mDefaultCardImage;
        private PicassoImageCardViewTarget mImageCardViewTarget;

        public ViewHolder(View view) {
            super(view);
            mCardView = (ImageCardView) view;
            mImageCardViewTarget = new PicassoImageCardViewTarget(mCardView);
            mDefaultCardImage = mContext.getResources().getDrawable(R.drawable.movie);
        }

        public void setVideo(Video m) {
            mMovie = m;
        }

        public Video getMovie() {
            return mMovie;
        }

        public ImageCardView getCardView() {
            return mCardView;
        }

        protected void updateCardViewImage(URI uri) {
            Picasso.with(mContext)
                    .load(uri.toString())
                    .placeholder(R.drawable.loading_image)
                    .resize(Utils.convertDpToPixel(mContext, CARD_WIDTH),
                            Utils.convertDpToPixel(mContext, CARD_HEIGHT))
                    .error(mDefaultCardImage)
                    .into(mImageCardViewTarget);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent) {
        mContext = parent.getContext();

        ImageCardView cardView = new ImageCardView(mContext);
        cardView.setFocusable(true);
        cardView.setFocusableInTouchMode(true);
        cardView.setBackgroundColor(mContext.getResources().getColor(R.color.fastlane_background));

        return new ViewHolder(cardView);
    }

    @Override
    public void onBindViewHolder(final Presenter.ViewHolder viewHolder, Object item) {
        Video movie = null;
        if (item instanceof TvProgram) {
            ((TvProgram) item).getCurrentPlayedVideo(new Callback<Video>() {
                @Override
                public void call(Video param) {
                    loadVideoInfos((ViewHolder) viewHolder, param);
                }
            });
        } else {
            loadVideoInfos((ViewHolder) viewHolder, (Video) item);
        }
    }

    private void loadVideoInfos(ViewHolder viewHolder, Video movie) {
        viewHolder.setVideo(movie);

        Log.d(TAG, "onBindViewHolder");
        viewHolder.mCardView.setTitleText(movie.getTitle());

        String formattedDuration = Utils.formatMillis(((int) movie.getDuration()));
        String publicationDate = simpleDateFormat.format(movie.getPublicationDate().getTime());

        viewHolder.mCardView.setContentText(publicationDate + " - " + formattedDuration);
        viewHolder.mCardView.setMainImageDimensions(CARD_WIDTH, CARD_HEIGHT);

        try {
            viewHolder.updateCardViewImage(new URI(movie.getImageUrl() == null ? "http://error" : movie.getImageUrl()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUnbindViewHolder(Presenter.ViewHolder viewHolder) {
    }

    @Override
    public void onViewAttachedToWindow(Presenter.ViewHolder viewHolder) {
        // TO DO
    }

    public static class PicassoImageCardViewTarget implements Target {
        private ImageCardView mImageCardView;

        public PicassoImageCardViewTarget(ImageCardView imageCardView) {
            mImageCardView = imageCardView;
        }

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom loadedFrom) {
            Drawable bitmapDrawable = new BitmapDrawable(mContext.getResources(), bitmap);
            mImageCardView.setMainImage(bitmapDrawable);
        }

        @Override
        public void onBitmapFailed(Drawable drawable) {
            mImageCardView.setMainImage(drawable);
        }

        @Override
        public void onPrepareLoad(Drawable drawable) {
            // Do nothing, default_background manager has its own transitions
        }
    }
}
