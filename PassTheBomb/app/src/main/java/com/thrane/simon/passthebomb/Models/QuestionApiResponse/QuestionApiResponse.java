package com.thrane.simon.passthebomb.Models.QuestionApiResponse;

/**
 * Created by Jeppe on 27-11-2017.
 */

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class QuestionApiResponse implements Serializable
{
    @SerializedName("response_code")
    @Expose
    public Integer responseCode;
    @SerializedName("results")
    @Expose
    public List<Result> results = null;
    private final static long serialVersionUID = 1444175798019310826L;
}
