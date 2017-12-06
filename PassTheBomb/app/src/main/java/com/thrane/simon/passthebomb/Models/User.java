package com.thrane.simon.passthebomb.Models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Søren on 26-11-2017.
 */

public class User implements Parcelable {
    public String id;
    public String name;
    public float angleAlpha;
    public boolean hasBomb;

    public User(){
        id = null;
        name = null;
    }


    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        angleAlpha = in.readFloat();
        hasBomb = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeFloat(angleAlpha);
        parcel.writeByte((byte) (hasBomb ? 1 : 0));
    }
}
