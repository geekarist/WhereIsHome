package com.github.geekarist.whereishome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.gson.Gson;

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
        Integer totalTime = mAdapter.getTotalTime();
        String totalTimeStr = DateUtils.formatElapsedTime(totalTime);
        mCommutingTime.setText(getString(R.string.commuting_time_total_label, totalTimeStr));
    }

    @OnClick(R.id.commuting_time_button_add_place)
    public void onClickButtonAddPlace(View v) {
        Intent intent = PickCommuteActivity.newCreationIntent(this, mAdapter.getHomeAddress(), isHomeAddressToPick());
        startActivityForResult(intent, REQUEST_PLACE);
    }

    private boolean isHomeAddressToPick() {
        return mAdapter.getItemCount() == 0;
    }

    public void startModificationActivity(Commute commute) {
        String homeAddress = mAdapter.getHomeAddress();
        boolean pickHomeAddress = mAdapter.getItemCount() == 0 || mAdapter.getItems().indexOf(commute) == 0;

        Intent intent = PickCommuteActivity.newModificationIntent(this, homeAddress, pickHomeAddress, commute);
        startActivityForResult(intent, REQUEST_PLACE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE) {
            if (resultCode == RESULT_OK) {
                Commute pickedCommute = data.getParcelableExtra(PickCommuteActivity.DATA_RESULT_COMMUTE);
                Commute commuteToModify = data.getParcelableExtra(PickCommuteActivity.EXTRA_COMMUTE_TO_MODIFY);
                if (commuteToModify == null) {
                    mAdapter.addItem(pickedCommute);
                } else {
                    mAdapter.replaceItem(commuteToModify, pickedCommute);
                }
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
