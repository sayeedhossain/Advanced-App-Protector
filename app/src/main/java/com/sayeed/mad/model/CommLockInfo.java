package com.sayeed.mad.model;

import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.litepal.crud.DataSupport;



public class CommLockInfo extends DataSupport implements Parcelable {

    public static final Parcelable.Creator<CommLockInfo> CREATOR = new Parcelable.Creator<CommLockInfo>() {
        @Override
        public CommLockInfo createFromParcel(@NonNull Parcel source) {
            return new CommLockInfo(source);
        }

        @Override
        public CommLockInfo[] newArray(int size) {
            return new CommLockInfo[size];
        }
    };
    private long id;
    @Nullable
    private String packageName;
    @Nullable
    private String appName;
    private boolean isLocked;
    private boolean isFaviterApp;
    @Nullable
    private ApplicationInfo appInfo;
    private boolean isSysApp;
    @Nullable
    private String topTitle;
    private boolean isSetUnLock;

    public CommLockInfo() {
    }

    public CommLockInfo(String packageName, boolean isLocked, boolean isFaviterApp) {
        this.packageName = packageName;
        this.isLocked = isLocked;
        this.isFaviterApp = isFaviterApp;
    }

    protected CommLockInfo(Parcel in) {
        this.id = in.readLong();
        this.packageName = in.readString();
        this.appName = in.readString();
        this.isLocked = in.readByte() != 0;
        this.isFaviterApp = in.readByte() != 0;
        this.appInfo = in.readParcelable(ApplicationInfo.class.getClassLoader());
        this.isSysApp = in.readByte() != 0;
        this.topTitle = in.readString();
        this.isSetUnLock = in.readByte() != 0;
    }

    @Nullable
    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Nullable
    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public boolean isFaviterApp() {
        return isFaviterApp;
    }

    public void setFaviterApp(boolean faviterApp) {
        isFaviterApp = faviterApp;
    }

    @Nullable
    public ApplicationInfo getAppInfo() {
        return appInfo;
    }

    public void setAppInfo(ApplicationInfo appInfo) {
        this.appInfo = appInfo;
    }

    public boolean isSysApp() {
        return isSysApp;
    }

    public void setSysApp(boolean sysApp) {
        isSysApp = sysApp;
    }

    @Nullable
    public String getTopTitle() {
        return topTitle;
    }

    public void setTopTitle(String topTitle) {
        this.topTitle = topTitle;
    }

    public boolean isSetUnLock() {
        return isSetUnLock;
    }

    public void setSetUnLock(boolean setUnLock) {
        isSetUnLock = setUnLock;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.packageName);
        dest.writeString(this.appName);
        dest.writeByte(this.isLocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isFaviterApp ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.appInfo, flags);
        dest.writeByte(this.isSysApp ? (byte) 1 : (byte) 0);
        dest.writeString(this.topTitle);
        dest.writeByte(this.isSetUnLock ? (byte) 1 : (byte) 0);
    }
}