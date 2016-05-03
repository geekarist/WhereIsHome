package com.github.geekarist.whereishome;

import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
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
    @Bind(R.id.place_remove)
    ImageButton mButtonRemove;
    @Bind(R.id.place_text_number_per_week)
    TextView mTextNumberPerWeek;
    @Bind(R.id.place_text_weekly_commute_time)
    TextView mTextWeeklyCommuteTime;

    private final CommuteListAdapter mAdapter;
    private Commute mCommute;
    private final ShowCommutingTimeActivity mActivity;

    public CommuteViewHolder(View itemView, CommuteListAdapter commuteListAdapter) {
        super(itemView);
        this.mAdapter = commuteListAdapter;
        this.mActivity = (ShowCommutingTimeActivity) itemView.getContext();
        ButterKnife.bind(this, itemView);
    }

    public void bind(Commute commute, boolean deletable) {
        mCommute = commute;
        mTextAddress.setText(commute.mAddress);

        mTextTime.setText(String.valueOf(commute.getDurationText()));
        mButtonRemove.setVisibility(deletable ? View.VISIBLE : View.GONE);

        if (mCommute.mNumberPerWeek > 0) {
            String numPerWeek = itemView.getContext().getString(
                    R.string.view_place_text_number_per_week, commute.mNumberPerWeek);
            mTextNumberPerWeek.setText(numPerWeek);
            mTextNumberPerWeek.setVisibility(View.VISIBLE);

            int weeklyTimeSeconds = commute.mNumberPerWeek * commute.getDurationSeconds();
            int weeklyTimeId = R.string.place_total_weekly_time;
            String weeklyTimeValue = DateUtils.formatElapsedTime(weeklyTimeSeconds);
            String weeklyTimeStr = itemView.getContext().getString(weeklyTimeId, weeklyTimeValue);
            mTextWeeklyCommuteTime.setText(weeklyTimeStr);
        } else {
            mTextNumberPerWeek.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.place_remove)
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

    @OnClick(R.id.place_container)
    public void onClick() {
        mActivity.startModificationActivity(mCommute);
    }

}
