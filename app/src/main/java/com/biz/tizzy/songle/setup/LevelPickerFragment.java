package com.biz.tizzy.songle.setup;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.biz.tizzy.locatr2.R;

/**
 * Created by tizzy on 11/3/17.
 */

public class LevelPickerFragment extends DialogFragment {

    public static final String EXTRA_LEVEL = "com.biz.tizzy.songle.level";
    private static final String ARG_LEVEL = "level";

    private static Button mLetsDoThis;
    private static RadioGroup mLevels;
    private String mLevel;

    public static LevelPickerFragment newInstance(String level) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LEVEL, level);

        LevelPickerFragment fragment = new LevelPickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_level, null);

        final AlertDialog alert = new AlertDialog.Builder(getActivity(), R.style.AlertTheme)
                .setView(v)
                .setTitle(R.string.level_picker_title)
                .create();

        mLevels = (RadioGroup) v.findViewById(R.id.levels);
        mLevels.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup levels, int checkedId) {
                // Find which level was chosen
                if (checkedId == R.id.level1) {
                    mLevel = "Level1";
                } else if (checkedId == R.id.level2) {
                    mLevel = "Level2";
                } else if (checkedId == R.id.level3) {
                    mLevel = "Level3";
                } else if (checkedId == R.id.level4) {
                    mLevel = "Level4";
                } else if (checkedId == R.id.level5) {
                    mLevel = "Level5";
                }
            }
        });

        mLetsDoThis = (Button) v.findViewById(R.id.lets_do_this_button);
        mLetsDoThis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLevel != null) {
                    sendResult(Activity.RESULT_OK, mLevel);
                    alert.cancel();
                }
            }
        });
        return alert;
    }

    private void sendResult(int resultCode, String level) {
        if (getTargetFragment() == null) {
            return;
        }
        Intent intent = new Intent();
        intent.putExtra(EXTRA_LEVEL, level);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
