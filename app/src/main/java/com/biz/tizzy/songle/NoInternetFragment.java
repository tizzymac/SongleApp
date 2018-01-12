package com.biz.tizzy.songle;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.biz.tizzy.locatr2.R;

/**
 * Created by tizzy on 11/20/17.
 */

public class NoInternetFragment extends DialogFragment {

    private static TextView mMessage;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_nointernet, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity(), R.style.AlertTheme)
                .setView(v)
                .setPositiveButton(android.R.string.ok, null)
                .create();

        mMessage = v.findViewById(R.id.no_internet_message);

        return alert;
    }
}
