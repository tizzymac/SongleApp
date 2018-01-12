package com.biz.tizzy.songle.map;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.collectedLyrics.LyricsPagerActivity;

/**
 * Created by tizzy on 11/30/17.
 */

public class FoundLyricFragment extends DialogFragment {

    public static final String EXTRA_LYRIC = "com.biz.tizzy.songle.lyric";
    private static final String ARG_LYRIC = "lyric";

    private static String mLyric;
    private static TextView mLyricView;
    private static Button mToLyricsButton;
    private static Button mToMapButton;
    private static int mTime;

    public static FoundLyricFragment newInstance(String lyric, int time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LYRIC, lyric);
        mLyric = lyric;

        mTime = time;

        FoundLyricFragment fragment = new FoundLyricFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_found_lyric, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity())
                .setView(v)
                .create();

        mLyricView = (TextView) v.findViewById(R.id.found_lyric);
        mLyricView.setText(mLyric);

        mToLyricsButton = (Button) v.findViewById(R.id.check_it_out);
        mToLyricsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //sendResult(Activity.RESULT_OK, mTitleInput.getText().toString(), mArtistInput.getText().toString());
                Intent intent = LyricsPagerActivity.newIntent(getActivity(), 0, mTime);
                startActivity(intent);
                alert.cancel();
            }
        });

        mToMapButton = (Button) v.findViewById(R.id.keep_collecting);
        mToMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.cancel();
            }
        });

        return alert;
    }
}
