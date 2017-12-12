package com.thrane.simon.passthebomb.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.thrane.simon.passthebomb.R;

import java.util.List;

/**
 * Created by kaspe on 27-10-2017.
 */
//Highly inspired by Kasper Løvborgs solution for Adapter exercise
public class QuestionAnswerAdapter extends BaseAdapter {

    private Context context;
    public List<String> answers;
    private String answerItem;

    public QuestionAnswerAdapter(@NonNull Context context, @NonNull List<String> answers) {
        this.context = context;
        this.answers = answers;
    }

    @Override
    public int getCount() {
        if(answers!=null) {
            return answers.size();
        } else {
            return 0;
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public String getItem(int position) {
        if(answers!=null) {
            return answers.get(position);
        } else {
            return null;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //we only need to create the views once, if not null we will reuse the existing view and update its values
        if (convertView == null) {
            LayoutInflater answerInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = answerInflater.inflate(R.layout.fragment_question_dialog_item, null);
        }

        answerItem = answers.get(position);
        if(answerItem!=null){
            TextView txtAnswerItem = convertView.findViewById(R.id.fragment_question_answer_item);
            txtAnswerItem.setText(answerItem);
        }
        return convertView;
    }
}
