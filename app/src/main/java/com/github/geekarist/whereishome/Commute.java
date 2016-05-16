package com.github.geekarist.whereishome;

import android.os.Parcel;
import android.os.Parcelable;

public class Commute implements Parcelable {
    final String mAddress;
    final int mNumberPerWeek;

    private int mDurationSeconds;
    private String mDurationText;
    private double mLon;
    private double mLat;

    public Commute(String address, int time, String durationText, int numberPerWeek, double lat, double lon) {
        mAddress = address;
        mDurationSeconds = time;
        mDurationText = durationText;
        mNumberPerWeek = numberPerWeek;
        mLon = lon;
        mLat = lat;
    }

    protected Commute(Parcel in) {
        mAddress = in.readString();
        mNumberPerWeek = in.readInt();
        mDurationSeconds = in.readInt();
        mDurationText = in.readString();
        mLon = in.readDouble();
        mLat = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mAddress);
        dest.writeInt(mNumberPerWeek);
        dest.writeInt(mDurationSeconds);
        dest.writeString(mDurationText);
        dest.writeDouble(mLon);
        dest.writeDouble(mLat);
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

        if (mNumberPerWeek != commute.mNumberPerWeek) return false;
        if (mDurationSeconds != commute.mDurationSeconds) return false;
        if (Double.compare(commute.mLon, mLon) != 0) return false;
        if (Double.compare(commute.mLat, mLat) != 0) return false;
        if (mAddress != null ? !mAddress.equals(commute.mAddress) : commute.mAddress != null)
            return false;
        return mDurationText != null ? mDurationText.equals(commute.mDurationText) : commute.mDurationText == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = mAddress != null ? mAddress.hashCode() : 0;
        result = 31 * result + mNumberPerWeek;
        result = 31 * result + mDurationSeconds;
        result = 31 * result + (mDurationText != null ? mDurationText.hashCode() : 0);
        temp = Double.doubleToLongBits(mLon);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(mLat);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public int getDurationSeconds() {
        return mDurationSeconds;
    }

    public String getDurationText() {
        return mDurationText;
    }

    public void setDurationSeconds(int durationSeconds) {
        mDurationSeconds = durationSeconds;
    }

    public void setDurationText(String durationText) {
        mDurationText = durationText;
    }

    public double getLon() {
        return mLon;
    }

    public double getLat() {
        return mLat;
    }
}
