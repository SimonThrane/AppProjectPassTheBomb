package com.thrane.simon.passthebomb.Models;

import java.util.ArrayList;

/**
 * Created by Jeppe on 27-11-2017.
 */

public class Question {

    public Question() {
        // Default constructor required for calls to DataSnapshot.getValue(Game.class)
    }
    public String category;
    public String question;
    public String correctAnswer;
    public ArrayList<String> incorrectAnswers;
}
