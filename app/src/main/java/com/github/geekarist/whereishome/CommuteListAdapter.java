package com.github.geekarist.whereishome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class CommuteListAdapter extends RecyclerView.Adapter<CommuteViewHolder> {
    private final List<Commute> mCommuteList = new ArrayList<>();

    @Override
    public CommuteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_place, parent, false);
        return new CommuteViewHolder(itemView, this);
    }

    @Override
    public void onBindViewHolder(CommuteViewHolder holder, int position) {
        Commute commute = mCommuteList.get(position);
        boolean deletable = position > 0 || (position == 0 && getItemCount() == 1);
        holder.bind(commute, deletable);
    }

    @Override
    public int getItemCount() {
        return mCommuteList.size();
    }

    public List<Commute> getItems() {
        return mCommuteList;
    }

    public void addItems(List<Commute> items) {
        mCommuteList.addAll(items);
        notifyDataSetChanged();
    }

    public void removeItem(Commute commute) {
        mCommuteList.remove(commute);
        notifyDataSetChanged();
    }
}

