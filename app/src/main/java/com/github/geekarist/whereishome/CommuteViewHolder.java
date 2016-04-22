package com.github.geekarist.whereishome;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommuteViewHolder extends RecyclerView.ViewHolder {
    @Bind(R.id.place_text_address)
    TextView mTextAddress;
    @Bind(R.id.place_text_commute_time)
    TextView mTextTime;
    @Bind(R.id.view_place_remove)
    ImageButton mButtonRemove;

    private final CommuteListAdapter mAdapter;
    private Commute mCommute;

    public CommuteViewHolder(View itemView, CommuteListAdapter commuteListAdapter) {
        super(itemView);
        this.mAdapter = commuteListAdapter;
        ButterKnife.bind(this, itemView);
    }

    public void bind(Commute commute, boolean deletable) {
        mCommute = commute;
        mTextAddress.setText(commute.mAddress);
        mTextTime.setText(String.valueOf(commute.mDurationText));
        mButtonRemove.setVisibility(deletable ? View.VISIBLE : View.GONE);
    }

    @OnClick(R.id.view_place_remove)
    public void onClickRemove() {
        new AlertDialog.Builder(itemView.getContext())
                .setMessage("Sure you want to delete the commute to " + mTextAddress.getText() + "?")
                .setCancelable(true)
                .setNegativeButton(android.R.string.cancel, ((dialog, which) -> {
                    // Nothing for now
                }))
                .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                    mAdapter.removeItem(mCommute);
                })
                .show();
    }
}
