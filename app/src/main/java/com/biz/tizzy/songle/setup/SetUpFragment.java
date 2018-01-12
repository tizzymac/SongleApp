package com.biz.tizzy.songle.setup;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.Toast;

import com.biz.tizzy.songle.LyricsSingleton;
import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.map.MapActivity;
import com.biz.tizzy.songle.map.MapPoint;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by tizzy on 12/7/17.
 */

public class SetUpFragment extends Fragment {
    private static final String TAG = "SetUpFragment";

    private static final String ARG_LEVEL = "level";
    private static final String ARG_TIME = "time";
    private static final String ARG_DOC = "doc";
    private static final String ARG_LYRICS_DOC = "lyrics";
    private static final String ARG_ARTIST = "artist";
    private static final String ARG_TITLE = "title";

    private Button mStartButton;
    private String mLevel;
    private int mTime;
    private String mDocString;
    public Document mDoc;
    private String mLyricsDocumentString;
    private MapPoint[] mMapPoints;

    // Songle animation
    private View mSceneView;
    private View mSView;
    private View mOView;
    private View mNView;
    private View mGView;
    private View mLView;
    private View mEView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup, container, false);

        mLevel = (String) getArguments().getSerializable(ARG_LEVEL);
        mTime = getArguments().getInt(ARG_TIME);

        // set up url for KML doc
        mDocString = (String) getArguments().getSerializable(ARG_DOC);
        new SetUpFragment.GetDocument().execute(mDocString);

        // set lyrics url
        mLyricsDocumentString = (String) getArguments().getSerializable(ARG_LYRICS_DOC);

        // Songle animation
        mSceneView = view.findViewById(R.id.frame);
        mSView = view.findViewById(R.id.s);
        mOView = view.findViewById(R.id.o);
        mNView = view.findViewById(R.id.n);
        mGView = view.findViewById(R.id.g);
        mLView = view.findViewById(R.id.l);
        mEView = view.findViewById(R.id.e);
        startAnimation();

        // enable when everything is ready
        mStartButton = (Button) view.findViewById(R.id.start_button);
        mStartButton.setEnabled(true);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // start map
                Intent intent = MapActivity.newIntent(getActivity(), mLevel, mTime, mLyricsDocumentString);
                startActivity(intent);
            }
        });

        return view;
    }

    private void startAnimation() {
        float sYStart = mSView.getTop();
        float oYStart = mOView.getTop();
        float nYStart = mNView.getTop();
        float gYStart = mGView.getTop();
        float lYStart = mLView.getTop();
        float eYStart = mEView.getTop();

    // FIRST
        ObjectAnimator sAnimator = ObjectAnimator
                .ofFloat(mSView, "y", sYStart, sYStart+15)
                .setDuration(1500);
        sAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator oAnimator = ObjectAnimator
                .ofFloat(mOView, "y", oYStart, oYStart-15)
                .setDuration(1500);
        oAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator nAnimator = ObjectAnimator
                .ofFloat(mNView, "y", nYStart, nYStart+15)
                .setDuration(1500);
        nAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator gAnimator = ObjectAnimator
                .ofFloat(mGView, "y", gYStart, gYStart-15)
                .setDuration(1500);
        gAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator lAnimator = ObjectAnimator
                .ofFloat(mLView, "y", lYStart, lYStart+15)
                .setDuration(1500);
        lAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator eAnimator = ObjectAnimator
                .ofFloat(mEView, "y", eYStart, eYStart-15)
                .setDuration(1500);
        eAnimator.setInterpolator(new AccelerateInterpolator());

    // SECOND
        ObjectAnimator sBackAnimator = ObjectAnimator
                .ofFloat(mSView, "y", sYStart+15, sYStart)
                .setDuration(1500);
        sAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator oBackAnimator = ObjectAnimator
                .ofFloat(mOView, "y", oYStart-15, oYStart)
                .setDuration(1500);
        oAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator nBackAnimator = ObjectAnimator
                .ofFloat(mNView, "y", nYStart+15, nYStart)
                .setDuration(1500);
        nAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator gBackAnimator = ObjectAnimator
                .ofFloat(mGView, "y", gYStart-15, gYStart)
                .setDuration(1500);
        gAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator lBackAnimator = ObjectAnimator
                .ofFloat(mLView, "y", lYStart+15, lYStart)
                .setDuration(1500);
        lAnimator.setInterpolator(new AccelerateInterpolator());

        ObjectAnimator eBackAnimator = ObjectAnimator
                .ofFloat(mEView, "y", eYStart-15, eYStart)
                .setDuration(1500);
        eAnimator.setInterpolator(new AccelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(sAnimator)
                .with(oAnimator)
                .with(nAnimator)
                .with(gAnimator)
                .with(lAnimator)
                .with(eAnimator)
                .before(sBackAnimator)
                .before(oBackAnimator)
                .before(nBackAnimator)
                .before(gBackAnimator)
                .before(lBackAnimator)
                .before(eBackAnimator);
        animatorSet.start();
    }

    public static SetUpFragment newInstance(String level, int time, String doc, String lyricsDocString) {
        Bundle args = new Bundle();

        args.putSerializable(ARG_LEVEL, level);
        args.putInt(ARG_TIME, time);
        args.putSerializable(ARG_DOC, doc);
        args.putSerializable(ARG_LYRICS_DOC, lyricsDocString);

        SetUpFragment fragment = new SetUpFragment();
        fragment.setArguments(args);

        return fragment;
    }

    private class GetDocument extends AsyncTask<String,Void,Void> {
        private Document mDocument;
        private String mDocString;

        @Override
        protected Void doInBackground(String... strings) {
            mDocString = strings[0];
            try {
                Log.i(TAG, "Trying to get KML document");
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

                factory.setIgnoringComments(true);
                factory.setIgnoringElementContentWhitespace(true);

                DocumentBuilder builder = factory.newDocumentBuilder();
                mDocument = builder.parse(new InputSource(mDocString));
            }
            catch (Exception ex) {
                Log.i(TAG, "Unable to get KML document", ex);
                FragmentManager manager = getFragmentManager();
                //NoInternetFragment dialog = new NoInternetFragment();
                //dialog.show(manager, DIALOG_NOINTERNET);
                Toast.makeText(getContext(), "Unable to get KML document", Toast.LENGTH_LONG).show();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            mDoc = mDocument;

            if (mDoc != null) {
                NodeList listOfPlacemarks = mDoc.getElementsByTagName("Placemark");
                mMapPoints = KMLParser.getCoordinates(listOfPlacemarks);

                // Clear any previous DB
                LyricsSingleton.get(getActivity()).deleteAll();

                // Set up DB from mMapPoints and lyrics
                for (int i = 0; i < mMapPoints.length; i++) {
                    LyricsSingleton.get(getActivity()).addMapPoint(mMapPoints[i]);
                }
            } else {
                // try again?
            }
        }
    }
}
