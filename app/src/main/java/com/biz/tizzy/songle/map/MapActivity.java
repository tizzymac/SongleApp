package com.biz.tizzy.songle.map;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.biz.tizzy.songle.SingleFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class MapActivity extends SingleFragmentActivity {
    private static final String TAG = "MapActivity";
    private static final int REQUEST_ERROR = 0;
    private static final String EXTRA_LEVEL = "com.biz.tizzy.songlle.level";
    private static final String EXTRA_TIME = "com.biz.tizzy.songle.time";
    private static final String EXTRA_LYRICS = "com.biz.tizzy.songle.lyrics";

    public static Intent newIntent(Context packageContext, String level, int time, String lyrics) {
        Intent intent = new Intent(packageContext, MapActivity.class);
        intent.putExtra(EXTRA_LEVEL, level);
        intent.putExtra(EXTRA_TIME, time);
        intent.putExtra(EXTRA_LYRICS, lyrics);
        return intent;
    }

    public static Intent returnIntent(Context packageContext, int time) {
        Intent intent = new Intent(packageContext, MapActivity.class);
        intent.putExtra(EXTRA_TIME, time);
        return intent;
    }

    @NonNull
    public static Intent makeNotificationIntent(Context geofenceService, String msg)
    {
        Log.d(TAG,msg);
        return new Intent(geofenceService, MapActivity.class);
    }

    @Override
    protected Fragment createFragment() {

        String level = (String) getIntent().getSerializableExtra(EXTRA_LEVEL);
        int time = (int) getIntent().getSerializableExtra(EXTRA_TIME);
        String lyricsDocString = (String) getIntent().getSerializableExtra(EXTRA_LYRICS);

        return MapFragment.newInstance(level, time, lyricsDocString);
    }

    @Override
    protected void onResume() {
        super.onResume();

        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int errorCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (errorCode != ConnectionResult.SUCCESS) {
            Dialog errorDialog = apiAvailability.getErrorDialog(this, errorCode, REQUEST_ERROR, new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialog) {
                    // Leave if services are unavailable
                    finish();
                }
            });

            errorDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
    }
}
