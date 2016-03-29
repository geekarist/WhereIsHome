package com.github.geekarist.whereishome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

public class ShowCommutingTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commuting_time);

        RecyclerView commuteListView = (RecyclerView) findViewById(R.id.list_commutes);
        CommuteListAdapter adapter = new CommuteListAdapter(this);
        assert commuteListView != null;
        commuteListView.setAdapter(adapter);
    }
}
