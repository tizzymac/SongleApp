package com.biz.tizzy.songle.guess;

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
 * Created by tizzy on 12/12/17.
 */

public class GiveUpFragment extends DialogFragment {

    private Button mKeepTryingButton;
    private Button mQuitButton;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_give_up, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        mKeepTryingButton = (Button) v.findViewById(R.id.keep_trying);
        mKeepTryingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });

        mQuitButton = (Button) v.findViewById(R.id.quit);
        mQuitButton.setOnClickListener(new View.OnClickListener() {
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
