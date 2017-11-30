package com.thrane.simon.passthebomb.Models.QuestionApiResponse;

/**
 * Created by Jeppe on 27-11-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Result implements Serializable
{

    @SerializedName("category")
    @Expose
    public String category;
    @SerializedName("type")
    @Expose
    public String type;
    @SerializedName("difficulty")
    @Expose
    public String difficulty;
    @SerializedName("question")
    @Expose
    public String question;
    @SerializedName("correct_answer")
    @Expose
    public String correctAnswer;
    @SerializedName("incorrect_answers")
    @Expose
    public List<String> incorrectAnswers = null;
    private final static long serialVersionUID = -5365843451853343458L;

}
