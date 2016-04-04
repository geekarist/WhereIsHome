package com.github.geekarist.whereishome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;

import java.util.Arrays;
import java.util.List;

public class CommuteListAdapter extends RecyclerView.Adapter<CommuteViewHolder> {
    private final List<Commute> mCommuteList;

    public CommuteListAdapter() {
        mCommuteList = Arrays.asList(new Commute("156 boulevard Haussmann, Paris", 40), new Commute("5 rue Henri Barbusse, Villejuif", 30));
        notifyDataSetChanged();
    }

    @Override
    public CommuteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_place, parent, false);
        return new CommuteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CommuteViewHolder holder, int position) {
        Commute commute = mCommuteList.get(position);
        holder.bind(commute);
    }

    @Override
    public int getItemCount() {
        return mCommuteList.size();
    }

    public void addCommute(Place place) {
        if (getItemCount() == 0) {
            // Main place
        } else {
            // Other place
        }
    }
}

