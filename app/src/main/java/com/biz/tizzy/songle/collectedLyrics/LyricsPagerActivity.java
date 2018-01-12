package com.biz.tizzy.songle.collectedLyrics;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.biz.tizzy.songle.LyricsSingleton;
import com.biz.tizzy.locatr2.R;

import java.util.List;

public class LyricsPagerActivity extends AppCompatActivity implements SetScreenLyricsListener {
    // This class will create and manage the ViewPager for the collected lyrics

    private static final String EXTRA_SCREEN_ID = "com.biz.tizzy.locatr2.screen_id";
    private static final String EXTRA_TIME = "com.biz.tizzy.locatr2.time";

    private ViewPager mViewPager;
    private FridgeScreen[] mScreens;
    public int mCurrentScreenID;
    private int mTime;

    public static Intent newIntent(Context packageContext, int screenID, int time) {
        Intent intent = new Intent(packageContext, LyricsPagerActivity.class);
        intent.putExtra(EXTRA_SCREEN_ID, screenID);
        intent.putExtra(EXTRA_TIME, time);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics_pager);

        int screenID = (int) getIntent().getSerializableExtra(EXTRA_SCREEN_ID);
        mTime = getIntent().getIntExtra(EXTRA_TIME, 0);

        mViewPager = (ViewPager) findViewById(R.id.lyrics_view_pager);

        mScreens = LyricsSingleton.get(this).getScreens();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mViewPager.setAdapter(new FragmentStatePagerAdapter(fragmentManager) {

            public int getItemPosition(Object object) {
                return POSITION_NONE;
            }

            @Override
            public Fragment getItem(int position) {
                mCurrentScreenID = position;

                // FridgeScreen screen = mScreens.get(position);
                return FridgeFragment.newInstance(position, mTime);
            }

            @Override
            public int getCount() {
                //return mScreens.size();
                return 3; // should always be three
            }
        });

        // which page to show first
        for (int i = 0; i < 3; i++) {
            if (mScreens[i].getID() == screenID) {
                mViewPager.setCurrentItem(i);
                break;
            }
        }
        mCurrentScreenID = mViewPager.getCurrentItem();
    }

    @Override
    public List<Box> setScreenLyrics() {

        // hmm when changing screen, this gets called before setCurrentItem ^^^
        return mScreens[mCurrentScreenID].getMagnets();

    }

    @Override
    public void onBackPressed() {
    }

}
