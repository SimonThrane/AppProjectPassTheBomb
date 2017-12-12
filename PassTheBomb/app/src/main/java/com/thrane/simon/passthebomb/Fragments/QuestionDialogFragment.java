package com.thrane.simon.passthebomb.Fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.thrane.simon.passthebomb.Adapters.QuestionAnswerAdapter;
import com.thrane.simon.passthebomb.Models.Question;
import com.thrane.simon.passthebomb.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestionDialogFragment extends DialogFragment {

    private QuestionAnswerListener listener;
    private TextView txtTitle;
    private ListView lviAnswers;

    public interface QuestionAnswerListener {
        void onQuestionCorrectAnswer();
        void onQuestionWrongAnswer();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_dialog, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        txtTitle = view.findViewById(R.id.fragment_question_title);
        lviAnswers = view.findViewById(R.id.question_fragment_answer_list);
        listener = (QuestionAnswerListener)getActivity();
        Random rnd = new Random();
        final int rndIndex = rnd.nextInt(maxAnswers);

        ArrayList<String> answers = new ArrayList<>();

        String title = getArguments().getString(QUESTION);
        String correctAnswer = getArguments().getString(CORRECT_ANSWER);
        ArrayList<String> wrongAnswers = getArguments().getStringArrayList(INCORRECT_ANSWERS);

        for(int i = 0, wrongAnswerIndex = 0; i<maxAnswers; i++) {
            if(answers.size() == rndIndex) {
                answers.add(correctAnswer);
            } else {
                answers.add(wrongAnswers.get(wrongAnswerIndex++));
            }
        }

        correctAnswerIndex = rndIndex;

        txtTitle.setText(title);
        lviAnswers.setAdapter(new QuestionAnswerAdapter(getContext(),answers));
        lviAnswers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if(i == correctAnswerIndex) {
                    listener.onQuestionCorrectAnswer();
                } else {
                    listener.onQuestionWrongAnswer();
                }
                Log.d("QuestionDialogClick", i + " is clicked" + " correctAnswer: " + correctAnswerIndex);
            }
        });
        setCancelable(false);
    }
}
