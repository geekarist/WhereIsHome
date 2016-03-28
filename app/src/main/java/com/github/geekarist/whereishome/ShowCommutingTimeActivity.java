package com.github.geekarist.whereishome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

public class ShowCommutingTimeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_commuting_time);

        ListView placeListView = (ListView) findViewById(R.id.list_places);
        List<Place> places = Arrays.asList(new Place("156 boulevard Haussmann, Paris", 40), new Place("5 rue Henri Barbusse, Villejuif", 30));
        ListAdapter adapter = new ArrayAdapter<>(this, R.layout.view_place, places);
        assert placeListView != null;
        placeListView.setAdapter(adapter);
    }
}
