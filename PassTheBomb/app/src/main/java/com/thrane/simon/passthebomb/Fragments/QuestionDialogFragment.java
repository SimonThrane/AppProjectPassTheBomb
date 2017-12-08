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

public class QuestionDialogFragment extends DialogFragment {

    public static final String QUESTION = "QUESTION";
    public static final String INCORRECT_ANSWERS = "INCORRECT_ANSWERS";
    public static final String CORRECT_ANSWER = "CORRECT_ANSWER";

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
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //ArrayList<String> questionArray = new ArrayList<String>();

        String title = getArguments().getString(QUESTION);
        ArrayList<String> answers = getArguments().getStringArrayList(INCORRECT_ANSWERS);
        answers.add(getArguments().getString(CORRECT_ANSWER));

        final CharSequence[] answersFinal = answers.toArray(new String[answers.size()]);

        builder.setTitle(title)
                .setItems(answersFinal, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Log.d("QuestionDialogClick", "It is clicked");
                    }
                });
        return builder.create();
    }
}
