package com.github.geekarist.whereishome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

    @Bind(R.id.list_commutes)
    RecyclerView mCommuteListView;

    private GoogleApiClient mGoogleApiClient;
    private CommuteListAdapter mAdapter;
    private CommuteListPersistence mPersistence;

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

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();
        mGoogleApiClient.connect();
    }

    @OnClick(R.id.button_add_place)
    public void onClickButtonAddPlace(View v) {
        Intent intent = PickCommuteActivity.newIntent(this, getHomeAddress(), isHomeAddressToPick());
        startActivityForResult(intent, REQUEST_PLACE);
    }

    private boolean isHomeAddressToPick() {
        return mAdapter.getItemCount() == 0;
    }

    private String getHomeAddress() {
        return Optional.ofNullable(mAdapter.getItems())
                .map(l -> l.get(0))
                .map(item -> item.address)
                .orElse(null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE) {
            Commute commute = data.getParcelableExtra(PickCommuteActivity.DATA_RESULT_COMMUTE);
            mAdapter.addItems(Collections.singletonList(commute));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mAdapter.unregisterAdapterDataObserver(mPersistence);
    }
}
