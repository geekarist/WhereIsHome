package com.github.geekarist.whereishome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

public class CommuteListAdapter extends RecyclerView.Adapter<CommuteViewHolder> {
    private final List<Commute> mCommuteList = new ArrayList<>();

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
            mCommuteList.add(new Commute(placeLabel(place), 0));
        } else {
            mCommuteList.add(new Commute(placeLabel(place), 42));
        }
        notifyDataSetChanged();
    }

    private String placeLabel(Place place) {
        return String.valueOf(place.getAddress() != null ? place.getAddress() : place.getLatLng());
    }
}

