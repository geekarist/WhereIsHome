package com.github.geekarist.whereishome;

import android.os.Parcel;
import android.os.Parcelable;

public class Commute implements Parcelable {
    final String mAddress;
    final int mTime;
    final String mDurationText;

    public Commute(String address, int time, String durationText) {
        mAddress = address;
        mTime = time;
        mDurationText = durationText;
    }

    protected Commute(Parcel in) {
        mAddress = in.readString();
        mTime = in.readInt();
        mDurationText = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeInt(mTime);
        dest.writeString(mDurationText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Commute> CREATOR = new Creator<Commute>() {
        @Override
        public Commute createFromParcel(Parcel in) {
            return new Commute(in);
        }

        @Override
        public Commute[] newArray(int size) {
            return new Commute[size];
        }
    };
}
