package com.github.geekarist.whereishome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.ui.PlacePicker;

public class ShowCommutingTimeActivity extends AppCompatActivity {

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

        Button buttonAdd = (Button) findViewById(R.id.button_add_place);
        assert buttonAdd != null;
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new PlacePicker.IntentBuilder().build(ShowCommutingTimeActivity.this);
                } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
