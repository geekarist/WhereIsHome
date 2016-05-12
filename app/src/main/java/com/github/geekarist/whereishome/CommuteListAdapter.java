package com.github.geekarist.whereishome;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

public class CommuteListAdapter extends RecyclerView.Adapter<CommuteViewHolder> {
    private static final String TAG = CommuteListAdapter.class.getSimpleName();
    private final List<Commute> mCommuteList = new ArrayList<>();
    private final Context mContext;

    private DistanceCalculation mAddressSearch;

    public CommuteListAdapter(Context context) {
        mContext = context;
        mAddressSearch = new CityMapperDistanceCalculation();
    }

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

    public Integer getTotalTime() {
        return Stream.of(getItems())
                .map(commute -> commute.getDurationSeconds() * commute.mNumberPerWeek)
                .reduce((duration1, duration2) -> duration1 + duration2)
                .orElse(0);
    }

    public String getHomeAddress() {
        return optionalOfHome()
                .map(c -> c.mAddress)
                .orElse(null);
    }

    public void addItem(Commute commute) {
        mCommuteList.add(commute);
        notifyDataSetChanged();
    }

    public void replaceItem(Commute commuteToReplace, Commute newCommute) {
        int index = getItems().indexOf(commuteToReplace);
        getItems().set(index, newCommute);
        notifyDataSetChanged();
    }

    public void updateCommutingTimes(String homeAddress) {
        Stream.of(mCommuteList).skip(1).forEach(commute ->
                mAddressSearch
                        .from(homeAddress)
                        .to(commute.mAddress)
                        .complete((durationText, durationSeconds) -> {
                            commute.setDurationText(durationText);
                            commute.setDurationSeconds(durationSeconds);
                            notifyDataSetChanged();
                        }, (error, msg) -> {
                            Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();
                            Log.e(TAG, msg, error);
                        }));
    }

    public double getHomeLon() {
        return optionalOfHome().map(Commute::getLon).orElse(0d);
    }

    public double getHomeLat() {
        return optionalOfHome().map(Commute::getLat).orElse(0d);
    }

    private Optional<Commute> optionalOfHome() {
        return Optional.of(this)
                .map(CommuteListAdapter::getItems)
                .filter(l -> !l.isEmpty())
                .map(a -> a.get(0));
    }
}

