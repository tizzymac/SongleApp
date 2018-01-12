package com.biz.tizzy.songle.setup;

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
 * Created by tizzy on 12/5/17.
 */

public class NoNewSongFragment extends DialogFragment {

    private static TextView mMessage1;
    private static TextView mMessage2;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_nonewsongs, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity(), R.style.AlertTheme)
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        mMessage1 = v.findViewById(R.id.no_new_songs);
        mMessage2 = v.findViewById(R.id.check_back_soon);

        return alert;
    }
}
