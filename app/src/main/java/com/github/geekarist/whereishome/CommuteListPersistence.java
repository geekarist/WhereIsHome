package com.github.geekarist.whereishome;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

public class CommuteListPersistence extends RecyclerView.AdapterDataObserver {
    private final CommuteListAdapter mAdapter;
    private Context mContext;
    private final Gson mGson;

    public CommuteListPersistence(CommuteListAdapter adapter, Context context, Gson gson) {
        mAdapter = adapter;
        mContext = context;
        mGson = gson;

        String json = PreferenceManager.getDefaultSharedPreferences(mContext).getString("COMMUTE_LIST", "[]");
        List<Commute> commuteList = deserialize(json);
        mAdapter.addItems(commuteList);
    }

    private List<Commute> deserialize(String json) {
        return mGson.fromJson(json, new TypeToken<List<Commute>>() {
        }.getType());
    }

    @Override
    public void onChanged() {
        List<Commute> items = mAdapter.getItems();
        PreferenceManager.getDefaultSharedPreferences(mContext).edit().putString("COMMUTE_LIST", serialize(items)).apply();
    }

    private String serialize(List<Commute> items) {
        return mGson.toJson(items);
    }
}
