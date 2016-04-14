package com.github.geekarist.whereishome;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.places.Place;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

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
            notifyDataSetChanged();
        } else {
            findDistanceAndAddCommute(mCommuteList.get(0).address, place);
        }
    }

    private void findDistanceAndAddCommute(String fromAddress, final Place toPlace) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com")
                .build();

        DistanceMatrixService service = retrofit.create(DistanceMatrixService.class);
        service.getDistanceMatrix(
                String.valueOf(fromAddress), String.valueOf(toPlace.getAddress()),
                "AIzaSyB1FGeq0g-kv2_pa7N9J-t601V9Nj9ibfw")
                .enqueue(new Callback<DistanceMatrix>() {
                    @Override
                    public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {
                        mCommuteList.add(new Commute(placeLabel(toPlace), (int) response.body().rows.get(0).elements.get(0).duration.value));
                    }

                    @Override
                    public void onFailure(Call<DistanceMatrix> call, Throwable t) {
                    }
                });
    }

    private String placeLabel(Place place) {
        return String.valueOf(place.getAddress() != null ? place.getAddress() : place.getLatLng());
    }

    public List<Commute> getItems() {
        return mCommuteList;
    }

    public void addItems(List<Commute> items) {
        mCommuteList.addAll(items);
    }

    interface DistanceMatrixService {
        String json = "https://maps.googleapis.com/maps/api/distancematrix/json" +
                "?origins=bd%20de%20brandebourg,%20ivry&destinations=156%20bd%20haussmann,%20Paris" +
                "&mode=transit&language=fr-FR&key=AIzaSyB1FGeq0g-kv2_pa7N9J-t601V9Nj9ibfw";

        @GET("/maps/api/distancematrix/json?origins={origin}&destinations={destination}&mode=transit&key={key}")
        Call<DistanceMatrix> getDistanceMatrix(@Path("origin") String origin, @Path("destination") String destination, @Path("key") String key);
    }

    public static class DistanceMatrix {
        List<String> destinationAddresses;
        List<String> originAddresses;
        List<DistanceMatrixRow> rows;
    }

    public static class DistanceMatrixRow {
        List<DistanceMatrixElements> elements;
    }

    public static class DistanceMatrixElements {
        DistanceMatrixElement distance;
        DistanceMatrixElement duration;
    }

    public static class DistanceMatrixElement {
        String text;
        double value;
    }
}

