package com.github.geekarist.whereishome;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ShowCommutingTimeActivity extends AppCompatActivity {

    private static final int PLACE_PICKER_REQUEST = 1;

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commuting_time);

        RecyclerView commuteListView = (RecyclerView) findViewById(R.id.list_commutes);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        assert commuteListView != null;
        commuteListView.setLayoutManager(layoutManager);
        CommuteListAdapter adapter = new CommuteListAdapter();
        commuteListView.setAdapter(adapter);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, null)
                .build();
        mGoogleApiClient.connect();

        Button buttonAdd = (Button) findViewById(R.id.button_add_place);
        assert buttonAdd != null;
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new PlacePicker.IntentBuilder().build(ShowCommutingTimeActivity.this);
                    startActivityForResult(intent, PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    StringWriter msg = new StringWriter();
                    PrintWriter msgPrint = new PrintWriter(msg);
                    e.printStackTrace(msgPrint);
                    new AlertDialog.Builder(ShowCommutingTimeActivity.this)
                            .setTitle("Google Play Services Error")
                            .setMessage(msg.toString()).create().show();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
    }
}
