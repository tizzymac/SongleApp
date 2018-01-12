package com.biz.tizzy.songle.setup;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.biz.tizzy.songle.SingleFragmentActivity;

/**
 * Created by tizzy on 12/7/17.
 */

public class SetUpActivity extends SingleFragmentActivity {
    private static final String TAG = "SetUpActivity";

    private static final String EXTRA_LEVEL = "com.biz.tizzy.songle.level";
    private static final String EXTRA_TIME = "com.biz.tizzy.songle.time";
    private static final String EXTRA_DOC = "com.biz.tizzy.songle.doc";
    private static final String EXTRA_LYRICS = "com.biz.tizzy.songle.lyrics";

    @Override
    protected Fragment createFragment() {

        String level = (String) getIntent().getSerializableExtra(EXTRA_LEVEL);
        int time = getIntent().getIntExtra(EXTRA_TIME, 0);
        String docString = (String) getIntent().getSerializableExtra(EXTRA_DOC);
        String lyricsDocString = (String) getIntent().getSerializableExtra(EXTRA_LYRICS);

        return SetUpFragment.newInstance(level, time, docString, lyricsDocString);
    }

    public static Intent newIntent(Context packageContext, String level, int time, String doc, String lyrics) {
        Intent intent = new Intent(packageContext, SetUpActivity.class);

        intent.putExtra(EXTRA_LEVEL, level);
        intent.putExtra(EXTRA_TIME, time);
        intent.putExtra(EXTRA_DOC, doc);
        intent.putExtra(EXTRA_LYRICS, lyrics);

        return intent;
    }

    @Override
    public void onBackPressed() {
    }

}
