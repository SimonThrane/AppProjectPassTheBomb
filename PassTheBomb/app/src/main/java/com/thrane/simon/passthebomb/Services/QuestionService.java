package com.thrane.simon.passthebomb.Services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.thrane.simon.passthebomb.Models.Question;
import com.thrane.simon.passthebomb.Models.QuestionApiResponse.QuestionApiResponse;
import com.thrane.simon.passthebomb.Models.QuestionApiResponse.Result;
import com.thrane.simon.passthebomb.Util.Globals;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class QuestionService extends Service {
    private int amountOfQuestions = 10;
    private ArrayList<Question> questions;
    private RequestQueue mQueue;
    private String mBaseUrl = "https://opentdb.com/api.php";
    private Gson mGson;

    public QuestionService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.getQuestions(21, amountOfQuestions, "medium");
        return super.onStartCommand(intent, flags, startId);
    }

    public void getQuestions(int category, int amount, String difficulty) {
        if(mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        String url = mBaseUrl + "?amount=" + amount + "&category=" + category + "&difficulty=" + difficulty + "&type=multiple";


        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                broadcastResult(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("response is bad:", error.toString());
            }
        });

        mQueue.add(request);
    }

    private void broadcastResult(String response) {
        Log.d("response is good:", response);
        questions = responseObjToQuestionList(jsonToPojo(response));
        Intent questionsIntent = new Intent(Globals.QUESTION_EVENT);
        questionsIntent.putParcelableArrayListExtra(Globals.QUESTION_EVENT_DATA,questions);
        LocalBroadcastManager.getInstance(this).sendBroadcast(questionsIntent);
        stopSelf();
    }

    // inspired from https://stackoverflow.com/questions/5554217/google-gson-deserialize-listclass-object-generic-type
    private QuestionApiResponse jsonToPojo(String json) {
        if (mGson == null) {
            mGson = new Gson();
        }
        Type collectionType = new TypeToken<QuestionApiResponse>(){}.getType();
        return mGson.fromJson(json, collectionType);
    }

    private ArrayList<Question> responseObjToQuestionList(QuestionApiResponse responseObj) {
        ArrayList<Question> questionList = new ArrayList<Question>();
        Iterator<Result> iter = responseObj.results.iterator();

        while(iter.hasNext())
        {
            Question question = new Question();
            Result result = iter.next();

            question.question = result.question;
            question.category = result.category;
            question.correctAnswer = result.correctAnswer;
            question.incorrectAnswers = (ArrayList<String>) result.incorrectAnswers;

            questionList.add(question);
        }

        return questionList;
    }
}
