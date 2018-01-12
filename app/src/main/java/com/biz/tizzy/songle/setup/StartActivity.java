package com.biz.tizzy.songle.setup;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.biz.tizzy.songle.SingleFragmentActivity;

/**
 * Created by tizzy on 11/20/17.
 */

public class StartActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() { return new StartMenuFragment(); }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, StartActivity.class);
        return intent;
    }
}
