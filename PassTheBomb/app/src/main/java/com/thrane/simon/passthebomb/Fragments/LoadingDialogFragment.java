package com.thrane.simon.passthebomb.Fragments;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.thrane.simon.passthebomb.R;
//Heavily inspired from this stackoverflow: https://stackoverflow.com/questions/23272122/showing-progress-dialog-within-dialogfragment
public class LoadingDialogFragment extends DialogFragment {

    private String mParam1;
    private String mParam2;

    private LoadingListener loadingListener;

    public LoadingDialogFragment() {
        // Required empty public constructor
    }

    public static LoadingDialogFragment newInstance() {
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        ProgressDialog dialog = new ProgressDialog(getActivity(), getTheme());
        dialog.setTitle(getString(R.string.loading_dialog_message));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        setCancelable(false);
        return dialog;
    }

    public interface LoadingListener {

    }
}
