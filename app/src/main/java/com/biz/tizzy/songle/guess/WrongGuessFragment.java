package com.biz.tizzy.songle.guess;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.biz.tizzy.locatr2.R;

/**
 * Created by tizzy on 11/18/17.
 */

public class WrongGuessFragment extends DialogFragment {

    private Button mContinueButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_wrong, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        mContinueButton = (Button) v.findViewById(R.id.wrong_continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });

        return alert;
    }
}
