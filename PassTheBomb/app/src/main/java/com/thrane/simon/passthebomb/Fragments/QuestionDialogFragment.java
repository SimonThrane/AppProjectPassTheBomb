package com.thrane.simon.passthebomb.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thrane.simon.passthebomb.Models.Question;
import com.thrane.simon.passthebomb.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionDialogFragment extends DialogFragment {

    private QuestionAnswerListener listener;

    public interface QuestionAnswerListener {
        void onCorrectAnswer();
        void onWrongAnswer();
    }

    public static final String QUESTION = "QUESTION";
    public static final String INCORRECT_ANSWERS = "INCORRECT_ANSWERS";
    public static final String CORRECT_ANSWER = "CORRECT_ANSWER";
    private int maxAnswers = 4;
    private int correctAnswerIndex = -1;

    public QuestionDialogFragment() {
    }

    public static QuestionDialogFragment newInstance(Question question) {
        QuestionDialogFragment frag = new QuestionDialogFragment();
        Bundle args = new Bundle();
        args.putString(QUESTION, question.question);
        args.putStringArrayList(INCORRECT_ANSWERS, question.incorrectAnswers);
        args.putString(CORRECT_ANSWER,question.correctAnswer);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        listener = (QuestionAnswerListener)getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        Random rnd = new Random();
        final int rndIndex = rnd.nextInt(maxAnswers);

        ArrayList<String> answers = new ArrayList<>();

        String title = getArguments().getString(QUESTION);
        String correctAnswer = getArguments().getString(CORRECT_ANSWER);

        for(String q : getArguments().getStringArrayList(INCORRECT_ANSWERS)) {
            //Handling lower bound
            if(rndIndex == 0) {
                answers.add(correctAnswer);
            }
            answers.add(q);
            if(answers.size() == rndIndex) {
                answers.add(correctAnswer);
            }
        }

        correctAnswerIndex = rndIndex;

        final CharSequence[] answersFinal = answers.toArray(new String[answers.size()]);

        builder.setTitle(title)
                .setItems(answersFinal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(i == correctAnswerIndex) {
                            listener.onCorrectAnswer();
                        } else {
                            listener.onWrongAnswer();
                        }
                        Log.d("QuestionDialogClick", i + " is clicked" + " correctAnswer: " + correctAnswerIndex);
                    }
                });
        return builder.create();
    }
}
