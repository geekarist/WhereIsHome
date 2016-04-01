package com.github.geekarist.whereishome;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.PrintWriter;
import java.io.StringWriter;

import butterknife.Bind;
import butterknife.OnClick;

public class ShowCommutingTimeActivity extends AppCompatActivity {

    private static final int REQUEST_PLACE_PICKER = 1;

    @Bind(R.id.list_commutes)
    RecyclerView mCommuteListView;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commuting_time);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mCommuteListView.setLayoutManager(layoutManager);
        CommuteListAdapter adapter = new CommuteListAdapter();
        mCommuteListView.setAdapter(adapter);

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
        try {
            Intent intent = new PlacePicker.IntentBuilder().build(ShowCommutingTimeActivity.this);
            startActivityForResult(intent, REQUEST_PLACE_PICKER);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
            StringWriter msg = new StringWriter();
            PrintWriter msgPrint = new PrintWriter(msg);
            e.printStackTrace(msgPrint);
            new AlertDialog.Builder(ShowCommutingTimeActivity.this)
                    .setTitle("Google Play Services Error")
                    .setMessage(msg.toString()).create().show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PLACE_PICKER) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(this, data);
                Toast.makeText(this, place.getAddress(), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
