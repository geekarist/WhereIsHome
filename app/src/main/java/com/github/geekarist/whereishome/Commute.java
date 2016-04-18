package com.github.geekarist.whereishome;

import android.os.Parcel;
import android.os.Parcelable;

public class Commute implements Parcelable {
    final String address;
    final int time;

    public Commute(String address, int time) {
        this.address = address;
        this.time = time;
    }

    protected Commute(Parcel in) {
        address = in.readString();
        time = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(address);
        dest.writeInt(time);
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
