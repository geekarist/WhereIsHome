package com.github.geekarist.whereishome;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

public class CommuteViewHolder extends RecyclerView.ViewHolder {
    private TextView mTextAddress;
    private TextView mTextTime;

    public CommuteViewHolder(View itemView) {
        super(itemView);
        mTextAddress = (TextView) itemView.findViewById(R.id.place_text_address);
        mTextTime = (TextView) itemView.findViewById(R.id.place_text_commute_time);
    }

    public void bind(Commute commute) {
        mTextAddress.setText(commute.mAddress);
        mTextTime.setText(String.valueOf(commute.mDurationText));
    }

}
