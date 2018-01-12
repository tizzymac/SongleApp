package com.biz.tizzy.songle.guess;

import android.app.AlertDialog;
import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.biz.tizzy.locatr2.R;

/**
 * Created by tizzy on 12/10/17.
 */

public class HalfCorrectFragment extends DialogFragment {

    private static final String ARG_CORRECT_HALF = "correctHalf";

    private Button mContinueButton;
    private TextView mHint;
    private static String mCorrectHalf;

    public static HalfCorrectFragment newInstance(String correctHalf) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CORRECT_HALF, correctHalf);

        mCorrectHalf = correctHalf;

        HalfCorrectFragment fragment = new HalfCorrectFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_half_correct, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        mHint = (TextView) v.findViewById(R.id.hint);
        mHint.setText("I'll give you a hint cuz I'm super nice:\nYou got the " + mCorrectHalf + " right");

        mContinueButton = (Button) v.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });

        return alert;
    }
}
