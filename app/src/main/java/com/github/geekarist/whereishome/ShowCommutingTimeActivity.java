package com.github.geekarist.whereishome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.annimon.stream.Optional;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

import java.util.Collections;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ShowCommutingTimeActivity extends AppCompatActivity {

    private static final int REQUEST_PLACE = 1;

    @Bind(R.id.commuting_time_list_commutes)
    RecyclerView mCommuteListView;
    @Bind(R.id.commuting_time_text_commuting_time)
    TextView mCommutingTime;

    private GoogleApiClient mGoogleApiClient;
    private CommuteListAdapter mAdapter;
    private CommuteListPersistence mPersistence;

    private RecyclerView.AdapterDataObserver mCommuteTimeUpdate = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateCommutingTime();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commuting_time);
        ButterKnife.bind(this);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCommuteListView.setLayoutManager(layoutManager);
        mAdapter = new CommuteListAdapter();
        mCommuteListView.setAdapter(mAdapter);
        mPersistence = new CommuteListPersistence(mAdapter, this, new Gson());
        mAdapter.registerAdapterDataObserver(mPersistence);
        mAdapter.registerAdapterDataObserver(mCommuteTimeUpdate);
        mAdapter.notifyDataSetChanged();

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();
        mGoogleApiClient.connect();
    }

    private void updateCommutingTime() {
        mCommutingTime.setText(getString(R.string.commuting_time_total_label, mAdapter.getTotalTime()));
    }

    @OnClick(R.id.commuting_time_button_add_place)
    public void onClickButtonAddPlace(View v) {
        Intent intent = PickCommuteActivity.newIntent(this, getHomeAddress(), isHomeAddressToPick());
        startActivityForResult(intent, REQUEST_PLACE);
    }

    private boolean isHomeAddressToPick() {
        return mAdapter.getItemCount() == 0;
    }

    private String getHomeAddress() {
        return Optional.ofNullable(mAdapter.getItems())
                .filter(list -> list.size() > 0)
                .map(list -> list.get(0))
                .map(item -> item.mAddress)
                .orElse(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE) {
            if (resultCode == RESULT_OK) {
                Commute commute = data.getParcelableExtra(PickCommuteActivity.DATA_RESULT_COMMUTE);
                mAdapter.addItems(Collections.singletonList(commute));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mAdapter.unregisterAdapterDataObserver(mPersistence);
        mAdapter.unregisterAdapterDataObserver(mCommuteTimeUpdate);
    }
}
