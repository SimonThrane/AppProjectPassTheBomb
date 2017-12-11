package com.thrane.simon.passthebomb.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Jeppe on 27-11-2017.
 */

public class Question implements Parcelable {

    public Question() {
        // Default constructor required for calls to DataSnapshot.getValue(Game.class)
    }
    public String category;
    public String question;
    public String correctAnswer;
    public ArrayList<String> incorrectAnswers;

    protected Question(Parcel in) {
        category = in.readString();
        question = in.readString();
        correctAnswer = in.readString();
        incorrectAnswers = in.createStringArrayList();
    }

    public static final Creator<Question> CREATOR = new Creator<Question>() {
        @Override
        public Question createFromParcel(Parcel in) {
            return new Question(in);
        }

        @Override
        public Question[] newArray(int size) {
            return new Question[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(category);
        parcel.writeString(question);
        parcel.writeString(correctAnswer);
        parcel.writeStringList(incorrectAnswers);
    }
}
