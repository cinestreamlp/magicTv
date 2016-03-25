package com.allreplay.magicTv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.ListRow;
import android.support.v17.leanback.widget.ListRowPresenter;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;

import org.magictvapi.ChannelManager;
import org.magictvapi.model.Channel;

import java.util.List;

/**
 * Created by thomas on 16/03/2016.
 */
public class TvChainListFragment extends BrowseFragment {

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupUIElements();

        loadRows();

        setupEventListeners();
    }

    private void loadRows() {
        ArrayObjectAdapter mRowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        setAdapter(mRowsAdapter);

        final ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new ChannelCardPresenter());
        List<Channel> chains = ChannelManager.getChains();
        for (Channel chain: chains) {
            listRowAdapter.add(chain);
        }
        mRowsAdapter.add(new ListRow(listRowAdapter));
    }

    private void setupEventListeners() {
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Channel) {
                Channel program = (Channel) item;
                Intent intent = new Intent(getActivity(), TvChainDetailActivity.class);
                intent.putExtra(DetailsActivity.CHAIN, program);

                getActivity().startActivity(intent);
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item,
                                   RowPresenter.ViewHolder rowViewHolder, Row row) {
        }
    }

    private void setupUIElements() {
        setHeadersState(HEADERS_DISABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(getResources().getColor(R.color.fastlane_background));
    }
}
