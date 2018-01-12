package com.biz.tizzy.songle;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.setup.StartActivity;

/**
 * Created by tizzy on 12/11/17.
 */

public class TimesUpFragment extends DialogFragment {

    private Button mContinueButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_timesup, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        mContinueButton = (Button) v.findViewById(R.id.continue_button);
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // end game
                Intent intent = StartActivity.newIntent(getActivity());
                startActivity(intent);
                alert.cancel();
            }
        });
        return alert;
    }
}
