package com.github.geekarist.whereishome;

import android.os.Parcel;
import android.os.Parcelable;

public class Commute implements Parcelable {
    final String mAddress;
    final int mDurationSeconds;
    final String mDurationText;
    final int mNumberPerWeek;

    public Commute(String address, int time, String durationText, int numberPerWeek) {
        mAddress = address;
        mDurationSeconds = time;
        mDurationText = durationText;
        mNumberPerWeek = numberPerWeek;
    }

    protected Commute(Parcel in) {
        mAddress = in.readString();
        mDurationSeconds = in.readInt();
        mDurationText = in.readString();
        mNumberPerWeek = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeInt(mDurationSeconds);
        dest.writeString(mDurationText);
        dest.writeInt(mNumberPerWeek);
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

    @SuppressWarnings("SimplifiableIfStatement")
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Commute commute = (Commute) o;

        if (mDurationSeconds != commute.mDurationSeconds) return false;
        if (mNumberPerWeek != commute.mNumberPerWeek) return false;
        if (mAddress != null ? !mAddress.equals(commute.mAddress) : commute.mAddress != null)
            return false;
        return mDurationText != null ? mDurationText.equals(commute.mDurationText) : commute.mDurationText == null;

    }

    @Override
    public int hashCode() {
        int result = mAddress != null ? mAddress.hashCode() : 0;
        result = 31 * result + mDurationSeconds;
        result = 31 * result + (mDurationText != null ? mDurationText.hashCode() : 0);
        result = 31 * result + mNumberPerWeek;
        return result;
    }
}
