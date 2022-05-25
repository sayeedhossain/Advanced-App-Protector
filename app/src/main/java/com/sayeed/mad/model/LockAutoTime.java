package com.sayeed.mad.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


public class LockAutoTime implements Parcelable {
    public static final Parcelable.Creator<LockAutoTime> CREATOR = new Parcelable.Creator<LockAutoTime>() {
        @Override
        public LockAutoTime createFromParcel(@NonNull Parcel source) {
            return new LockAutoTime(source);
        }

        @Override
        public LockAutoTime[] newArray(int size) {
            return new LockAutoTime[size];
        }
    };
    @Nullable
    private String title;
    private long time;

    public LockAutoTime() {
    }

    protected LockAutoTime(Parcel in) {
        this.title = in.readString();
        this.time = in.readLong();
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeLong(this.time);
    }
}