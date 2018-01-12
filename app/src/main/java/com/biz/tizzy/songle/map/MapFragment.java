package com.biz.tizzy.songle.map;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.biz.tizzy.songle.LyricsSingleton;
import com.biz.tizzy.songle.NoInternetFragment;
import com.biz.tizzy.locatr2.R;
import com.biz.tizzy.songle.Song;
import com.biz.tizzy.songle.SongPreferences;
import com.biz.tizzy.songle.TimesUpFragment;
import com.biz.tizzy.songle.collectedLyrics.LyricsPagerActivity;
import com.biz.tizzy.songle.guess.CorrectGuessFragment;
import com.biz.tizzy.songle.guess.GuessFragment;
import com.biz.tizzy.songle.guess.HalfCorrectFragment;
import com.biz.tizzy.songle.guess.WrongGuessFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class MapFragment extends  SupportMapFragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = "MapFragment";
    private static final String[] LOCATION_PERMISSIONS = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION,
    };
    private static final int REQUEST_LOCATION_PERMISSIONS = 0;
    private static final String DIALOG_NOINTERNET = "DialogNoInternet";

    private static final String ARG_LEVEL = "level";
    private static final String ARG_TIME = "time";
    private static final String ARG_LYRICS_DOC = "lyrics";

    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final String REQUESTING_LOCATION_UPDATES_KEY = "RequestingLocationUpdatesKey";

// GUESSING
    private static final String DIALOG_GUESS = "DialogGuess";
    private static final String DIALOG_CORRECT_GUESS = "DialogCorrectGuess";
    private static final String DIALOG_WRONG_GUESS = "DialogWrongGuess";
    private static final String DIALOG_HALF_CORRECT = "DialogHalfCorrectGuess";
    private static final String DIALOG_TIMESUP = "DialogTimesUp";
    private static final int REQUEST_GUESS = 0;

// VARIABLES
    public static Song mSong;
    public int mAllowedTime;
    public int mTimeRemaining;
    private Location mCurrentLocation;
    private GoogleMap mMap;
    private String mLyricsDocumentString;
    private List<String> mLyricsLines = new ArrayList<String>();
    private GoogleApiClient mClient;
    private String mLevel;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;
    private boolean mRequestingLocationUpdates;
    private FusedLocationProviderClient mFusedLocationClient;
    private MapPoint[] mMapPoints;
    private PendingIntent mGeofencePendingIntent;
    //private float testRadius = 6;
    private float testRadius = 32;
    protected ArrayList<Geofence> mGeofenceList = new ArrayList<Geofence>();
    private GeofencingClient mGeofencingClient;

// Timer
    private View mTimerView;
    private TextView mTimerTextView;

// Active Quadrant
    private int mActiveQuadrant;    // 0 if inactive, 1,2,3 or 4 otherwise
    private double splitLat = 55.944425;
    private double splitLon = -3.188396;

// Collecting a word
    public static final String EXTRA_DETAILS = "com.biz.tizzy.songle.details";
    private static final String DIALOG_FOUND_LYRIC = "DialogFoundLyric";

    public static MapFragment newInstance(String level, int time, String lyricsDocString) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_LEVEL, level);
        args.putInt(ARG_TIME, time);
        args.putSerializable(ARG_LYRICS_DOC, lyricsDocString);
        //args.putInt(ARG_TIME, time);

        MapFragment fragment = new MapFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // timer
        mAllowedTime = getArguments().getInt(ARG_TIME, 0);
        //mTimerView = LayoutInflater.from(getActivity()).inflate(R.layout.timer, null);
        startTimer(mAllowedTime);

        mLevel = (String) getArguments().getSerializable(ARG_LEVEL);

        // Get song
        mSong = SongPreferences.getSong(getActivity());

        // get lyrics doc url
        mLyricsDocumentString = (String) getArguments().getSerializable(ARG_LYRICS_DOC);
        new MapFragment.GetLyrics().execute(mLyricsDocumentString);

        // Set mMapPoints from DB
        mMapPoints = LyricsSingleton.get(getActivity()).getMapPoints();
        Log.d(TAG, "Length of mMapPoints after set up : " + mMapPoints.length);
        updateActiveQuandrant(mCurrentLocation);

        // set up toolbar
        setHasOptionsMenu(true);

        mGeofencingClient = LocationServices.getGeofencingClient(getActivity());
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        // Broadcast Receiver (for seeing when geofences are triggered)
        getActivity().registerReceiver(this.receiver, new IntentFilter("GeofenceTS"));

        // initialize location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)         // 10 seconds
                .setFastestInterval(2 * 1000);  // 1 second

        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    updateUI();
                }
            }
        };

       mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                //.addOnConnectionFailedListener(getActivity())
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }
                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .build();


        // Get a reference to a GoogleMap once the fragment is created & initialized
        getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mMap = googleMap;

                    if (mClient.isConnected()) {
                        // START !!
                        getCurrentLocation();
                        startGeofence();
                    }
                    updateUI();
                }

            });



        if (savedInstanceState != null) {
            updateValuesFromBundle(savedInstanceState);
        }
    }

// GUESSING RESULT
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        if (requestCode == REQUEST_GUESS) {
            String title = (String) data.getSerializableExtra(GuessFragment.EXTRA_TITLE);
            String artist = (String) data.getSerializableExtra(GuessFragment.EXTRA_ARTIST);

            FragmentManager manager = getFragmentManager();

            switch (checkAnswer(title, artist)) {
                case 1:
                    CorrectGuessFragment correctDialog = new CorrectGuessFragment();
                    correctDialog.show(manager, DIALOG_CORRECT_GUESS);
                    break;
                case 2:
                    HalfCorrectFragment halfDialog1 = HalfCorrectFragment.newInstance("title");
                    halfDialog1.show(manager, DIALOG_HALF_CORRECT);
                    break;
                case 3:
                    HalfCorrectFragment halfDialog2 = HalfCorrectFragment.newInstance("artist");
                    halfDialog2.show(manager, DIALOG_HALF_CORRECT);
                    break;
                case 4:
                    WrongGuessFragment wrongDialog = new WrongGuessFragment();
                    wrongDialog.show(manager, DIALOG_WRONG_GUESS);
                    break;
            }
        }
    }
    private int checkAnswer(String title, String artist) {
        // capital vs lowercase doesn't matter
        boolean titleCorrect = (title.toLowerCase()).equals(mSong.getTitle().toLowerCase());
        boolean artistCorrect = (artist.toLowerCase()).equals(mSong.getArtist().toLowerCase());

        if (titleCorrect && artistCorrect) {
            return 1;
        } else {
            if (titleCorrect) {
                return 2;
            } else {
                if (artistCorrect) {
                    return 3;
                } else {
                    return 4;
                }
            }
        }
    }

// TOOLBAR
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        // Update the menu button
        MenuItem searchItem = menu.findItem(R.id.map_view);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.map_view:
                if (hasLocationPermission()) {

                    // START !!
                    getCurrentLocation();
                    startGeofence();

                } else {
                    requestPermissions(LOCATION_PERMISSIONS, REQUEST_LOCATION_PERMISSIONS);
                }
                return true;
            case R.id.puzzle_view:
                Intent intent = LyricsPagerActivity.newIntent(getActivity(), 0, mTimeRemaining);
                startActivity(intent);
                return true;
            case R.id.guess:
                FragmentManager manager = getFragmentManager();
                //GuessFragment dialog = GuessFragment.newInstance(mSong.getTitle(), mSong.getArtist());
                GuessFragment dialog = new GuessFragment();
                dialog.setTargetFragment(MapFragment.this, REQUEST_GUESS);
                dialog.show(manager, DIALOG_GUESS);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

// LOCATION
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case REQUEST_LOCATION_PERMISSIONS:
                if (hasLocationPermission()) {
                    getCurrentLocation();
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // updateUI();
        // invalidate options menu ?
        mClient.connect();
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        getActivity().registerReceiver(receiver, new IntentFilter());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mClient.isConnected()) {
            // Remove updates when finished
            LocationServices.FusedLocationApi.removeLocationUpdates(mClient, this);
            mClient.disconnect();
        }
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(TAG, "Location services connected");
        if (hasLocationPermission()) {
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mClient);

            if (mCurrentLocation == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mClient, mLocationRequest, this);
            } else {
                updateActiveQuandrant(mCurrentLocation);
                updateUI();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

   @Override
    public void onLocationChanged(Location location) {
       updateActiveQuandrant(location);
       updateUI();
    }

    // Save activity state
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);

        super.onSaveInstanceState(outState);
    }

    private void getCurrentLocation() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1000);
        request.setInterval(3000);     // 3 secs
        if (mClient.isConnected() & hasLocationPermission()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mClient, request, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    Log.i(TAG, "Got a fix : " + location);
                    //startGeofence();
                    new GetCurrentLocation().execute(location);
                }
            });
        }
    }

    private void startLocationUpdates() {
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null );
    }

    private boolean hasLocationPermission() {
        int result = ContextCompat.checkSelfPermission(getActivity(), LOCATION_PERMISSIONS[0]);
        return result == PackageManager.PERMISSION_GRANTED;
    }

// UI
    private void updateUI() {
        if (mMap == null || mCurrentLocation == null) {
            return;
        }

        mMap.clear();

        for (int i = 0; i < mMapPoints.length; i++) {
            if (mMapPoints[i] != null) {    // made null after collected
                LatLng itemPoint = new LatLng(mMapPoints[i].getLat(), mMapPoints[i].getLon());
                MarkerOptions itemMarker = new MarkerOptions().position(itemPoint);
                switch (mMapPoints[i].getInterest()) {
                    case 0:
                        itemMarker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        break;
                    case 1:
                        itemMarker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                        break;
                    case 2:
                        itemMarker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                        break;
                    case 3:
                        itemMarker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
                        break;
                    case 4:
                        itemMarker.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        break;
                }
                mMap.addMarker(itemMarker);
            }
        }

        LatLng myPoint = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        MarkerOptions myMarker = new MarkerOptions().position(myPoint);
        //myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_walker));
        myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_walker_red));
        myMarker.zIndex(1.0f);  // should always be on top
        mMap.addMarker(myMarker);

        CircleOptions circleOptions = new CircleOptions()
                .center(myMarker.getPosition())
                .strokeColor(Color.argb(50, 70, 0, 0))
                .fillColor(Color.argb(100, 150, 0, 0))
                .radius(testRadius);
        mMap.addCircle(circleOptions);


        // Zoom into playing field
        int margin = getResources().getDimensionPixelSize(R.dimen.map_inset_margin);
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(setPlayingField(), margin);
        mMap.animateCamera(update);

        // Zoom into player
        //LatLngBounds bounds = new LatLngBounds.Builder().include(myPoint).build();
        //CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds, margin);
        //mMap.animateCamera(update);

    }

    private LatLngBounds setPlayingField(){
        LatLng forrestHill = new LatLng(55.946233, -3.192473);
        LatLng topOfMeadows = new LatLng(55.942617, -3.192473);
        LatLng kfc = new LatLng(55.946233, -3.184319);
        LatLng buccleuchSt = new LatLng(55.942617, -3.184319);

        LatLngBounds bounds = new LatLngBounds.Builder()
                .include(forrestHill)
                .include(topOfMeadows)
                .include(kfc)
                .include(buccleuchSt)
                .build();
        return bounds;
    }

    private void startTimer(final int time) {
        new CountDownTimer(time, 1000) {

            public void onTick(long millisUntilFinished) {
                int mins = (int) TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                int secs = (int) TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - (mins*60);
                String displayTime = String.format("%02d : %02d", mins, secs);

                Toast timer = Toast.makeText(getContext(), displayTime, Toast.LENGTH_LONG);
                //timer.setView(mTimerView);
                timer.show();

                mTimeRemaining = (int) millisUntilFinished;
            }

            public void onFinish() {
                FragmentManager manager = getFragmentManager();
                TimesUpFragment dialog = new TimesUpFragment();
                dialog.show(manager, DIALOG_TIMESUP);
            }

        }.start();
    }

// Active Quadrant
    private void updateActiveQuandrant(Location currentLocation) {

        int prevQuadrant = mActiveQuadrant;

        if (mMapPoints.length > 100) {
            if (currentLocation != null) {

                if (currentLocation.getLatitude() > splitLat) {
                    if (currentLocation.getLongitude() > splitLon) {
                        mActiveQuadrant = 1;
                    } else {
                        mActiveQuadrant = 2;
                    }
                } else {
                    if (currentLocation.getLongitude() > splitLon) {
                        mActiveQuadrant = 3;
                    } else {
                        mActiveQuadrant = 4;
                    }
                }
            } else {
                mActiveQuadrant = 4;    // might be wrong but will be changed
            }
        } else {
            mActiveQuadrant = 0;
        }

        // if active quadrant changes, need to switch over mGeofenceList
        if ((prevQuadrant != mActiveQuadrant) & (mCurrentLocation != null)) {
            // restart
            getCurrentLocation();
            startGeofence();
        }

        Log.d(TAG, "Active Quadrant : " + mActiveQuadrant);
    }

// GeoFences
    public void createGeofence() {
        Log.d(TAG, "createGeofence");

        if (mMapPoints.length <= 100) {

            if (mGeofenceList.size() == 0) {
                for (int i = 0; i < mMapPoints.length; i++) {
                    mGeofenceList.add(new Geofence.Builder()
                            .setRequestId(mMapPoints[i].getLyricLocation())
                            .setCircularRegion(
                                    mMapPoints[i].getLat(),
                                    mMapPoints[i].getLon(),
                                    testRadius
                            )
                            .setExpirationDuration(60 * 60 * 1000)
                            .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                            .build());
                }
            }
        } else {
            // empty mGeofenceList first
            mGeofenceList = new ArrayList<Geofence>();

            // Only set up geofences in the active quadrant
            switch (mActiveQuadrant) {
                case 1:
                    for (int i = 0; i < mMapPoints.length; i++) {
                        if (mMapPoints[i] != null) {
                            if ((mMapPoints[i].getLat() > splitLat) & (mMapPoints[i].getLon() > splitLon)) {
                                mGeofenceList.add(new Geofence.Builder()
                                        .setRequestId(mMapPoints[i].getLyricLocation())
                                        .setCircularRegion(
                                                mMapPoints[i].getLat(),
                                                mMapPoints[i].getLon(),
                                                testRadius
                                        )
                                        .setExpirationDuration(60 * 60 * 1000)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                            }
                        }
                    }
                    Log.d(TAG, "Number of geofences in quadrant 1 : " + mGeofenceList.size());
                    break;
                case 2:
                    for (int i = 0; i < mMapPoints.length; i++) {
                        if (mMapPoints[i] != null) {
                            if ((mMapPoints[i].getLat() > splitLat) & (mMapPoints[i].getLon() < splitLon)) {
                                mGeofenceList.add(new Geofence.Builder()
                                        .setRequestId(mMapPoints[i].getLyricLocation())
                                        .setCircularRegion(
                                                mMapPoints[i].getLat(),
                                                mMapPoints[i].getLon(),
                                                testRadius
                                        )
                                        .setExpirationDuration(60 * 60 * 1000)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                            }
                        }
                    }
                    Log.d(TAG, "Number of geofences in quadrant 2 : " + mGeofenceList.size());
                    break;
                case 3:
                    for (int i = 0; i < mMapPoints.length; i++) {
                        if (mMapPoints[i] != null) {
                            if ((mMapPoints[i].getLat() < splitLat) & (mMapPoints[i].getLon() > splitLon)) {
                                mGeofenceList.add(new Geofence.Builder()
                                        .setRequestId(mMapPoints[i].getLyricLocation())
                                        .setCircularRegion(
                                                mMapPoints[i].getLat(),
                                                mMapPoints[i].getLon(),
                                                testRadius
                                        )
                                        .setExpirationDuration(60 * 60 * 1000)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                            }
                        }
                    }
                    Log.d(TAG, "Number of geofences in quadrant 3 : " + mGeofenceList.size());
                    break;
                case 4:
                    for (int i = 0; i < mMapPoints.length; i++) {
                        if (mMapPoints[i] != null) {
                            if ((mMapPoints[i].getLat() < splitLat) & (mMapPoints[i].getLon() < splitLon)) {
                                mGeofenceList.add(new Geofence.Builder()
                                        .setRequestId(mMapPoints[i].getLyricLocation())
                                        .setCircularRegion(
                                                mMapPoints[i].getLat(),
                                                mMapPoints[i].getLon(),
                                                testRadius
                                        )
                                        .setExpirationDuration(60 * 60 * 1000)
                                        .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                                        .build());
                            }
                        }
                    }
                    Log.d(TAG, "Number of geofences in quadrant 4 : " + mGeofenceList.size());
                    break;
            }
        }
    }

    @NonNull
    private GeofencingRequest getGeofencingRequest() {
        Log.d(TAG, "createGeoFenceRequest");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(mGeofenceList);
        return builder.build();
    }

    private PendingIntent getGeofencePendingIntent() {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        } else {
            Log.d(TAG, "createGeofencePendingIntent");

            //Intent geofenceTriggeredIntent = new Intent("GeofenceTS");
            //mGeofencePendingIntent = PendingIntent.getBroadcast(getActivity(), 0, geofenceTriggeredIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            //return mGeofencePendingIntent;

            Intent intent = new Intent(getActivity(), GeofenceTransitionService.class);
            mGeofencePendingIntent = PendingIntent.getService(getActivity(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            return mGeofencePendingIntent;
        }
    }

    private void addGeofences(GeofencingRequest request) {
        Log.d(TAG, "addGeofence");
        if (hasLocationPermission()) {
            LocationServices.GeofencingApi.addGeofences(
                    mClient,
                    request,
                    getGeofencePendingIntent()
            ).setResultCallback(new ResultCallback<Status>() {
                @Override
                public void onResult(@NonNull Status status) {
                    // um i dunno
                }
            });
        }
    }

    public void onResult(@NonNull Status status) {
        Log.i(TAG, "onResult: " + status);
        if (status.isSuccess()) {
            updateUI();
        } else {
            // inform about fail
        }
    }

    private void startGeofence() {
        Log.i(TAG, "startGeofence");

        createGeofence();
        GeofencingRequest geofencingRequest = getGeofencingRequest();
        addGeofences(geofencingRequest);
    }

    // Broadcast receiver (receives details from geofences)
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Details received from triggered fence");

            // extract lyric
            String geoFenceDetails = (String) intent.getExtras().get(EXTRA_DETAILS);
            String foundLyric;
            try {
                if (mLyricsLines != null) {
                    foundLyric = getLyricFromGeofence(geoFenceDetails);
                } else {
                    Log.d(TAG, "Unable to retreive lyrics document");
                    foundLyric = null;
                }
            } catch (IOException e) {
                foundLyric = null;
                Log.e(TAG, e.getMessage());
            }

            if (foundLyric != null) {
                List<String> geofencesToRemove = new ArrayList<>();
                geofencesToRemove.add(geoFenceDetails.substring(24));
                if (geofencesToRemove != null) {

                    // Delete Geofences
                    LocationServices.GeofencingApi.removeGeofences(mClient, geofencesToRemove);

                    for (String g:geofencesToRemove) {
                        // Delete MapPoint
                        for (int i = 0; i < mMapPoints.length; i++) {
                            if (mMapPoints[i] != null) {
                                if (g.contains(mMapPoints[i].getLyricLocation())) {
                                    mMapPoints[i] = null;
                                }
                            }
                        }

                        // set lyric to found  (also adds the lyric to the DB)
                        LyricsSingleton.get(getActivity()).setLyricToFound(g, foundLyric);

                        // start dialog
                        FragmentManager manager = getFragmentManager();
                        FoundLyricFragment dialog = FoundLyricFragment.newInstance(foundLyric, mTimeRemaining);
                        dialog.show(manager, DIALOG_FOUND_LYRIC);

                    }
                    updateUI();
                }
            }
        }
    };

    @Nullable
    private String getLyricFromGeofence(String details) throws IOException {
        // Get which geofence it was
        String geoID = details.substring(24);

        if ((mLyricsLines.size() == 0) || mLyricsLines == null) {
            Log.d(TAG, "mLyricsLines is empty");
            new MapFragment.GetLyrics().execute(mLyricsDocumentString);
        } else {

            if (geoID.length() < 7) {   // avoid SERVICE_VERSION_UPDATE_REQUIRED
                // ie. when leaving fence

                int lineNum = Integer.parseInt(geoID.split(":")[0]);
                int wordNum = Integer.parseInt(geoID.split(":")[1]);

                // get line lyric is in
                String lyric;
                if (mLyricsLines.size() < (lineNum - 1)) {
                    String line = mLyricsLines.get(lineNum - 1);

                    // get lyric
                    if (lineNum >= 10) {
                        // get lyric from line
                        int index = 4 + wordNum;
                        if (line.length() < index) {
                            lyric = line.split(" |	")[index];
                        } else {
                            //
                            Log.d(TAG, "Index for array of lyrics is out of bounds");
                            lyric = null;
                        }
                    } else {
                        // get lyric from line
                        int index = 5 + wordNum;
                        if (line.length() > index | line.contains("\t")) {
                            lyric = line.split(" |	")[index];
                        } else {
                            Log.d(TAG, "Index for array of lyrics is out of bounds");
                            lyric = null;
                        }
                    }
                } else {
                    lyric = null;
                }

                if (lyric != null) {
                    // Remove non-alphanumeric characters
                    lyric = lyric.replaceAll("[^a-zA-Z0-9]", "");

                    // Send to fridge
                    if (lineNum < mLyricsLines.size() / 3) {
                        // Send to yellow
                        LyricsSingleton.get(getActivity()).addYellowLyric(lyric, geoID);
                    } else {
                        if (lineNum < 2 * (mLyricsLines.size() / 3)) {
                            // Send to red
                            LyricsSingleton.get(getActivity()).addRedLyric(lyric, geoID);
                        } else {
                            // Send to blue
                            LyricsSingleton.get(getActivity()).addBlueLyric(lyric, geoID);
                        }
                    }

                    return lyric;
                }
            }
        }
        return null;
    }

    private void updateValuesFromBundle(Bundle savedInstanceState) {
        // Update the value of mRequestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            mRequestingLocationUpdates = savedInstanceState.getBoolean(
                    REQUESTING_LOCATION_UPDATES_KEY);
        }
        // ...

        // Update UI to match restored state
        updateUI();
    }

// Inner Classes

    private class GetCurrentLocation extends AsyncTask<Location,Void,Void> {
        private Location mLocation;


        @Override
        protected Void doInBackground(Location... params) {
            mLocation = params[0];
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {

            if (mLocation != null) {
                mCurrentLocation = mLocation;
                updateActiveQuandrant(mCurrentLocation);
                updateUI();
            } else {
                Log.d(TAG, "Unable to get current location");

                //FragmentManager manager = getFragmentManager();
                //NoInternetFragment dialog = new NoInternetFragment();
                //dialog.show(manager, DIALOG_NOINTERNET);
            }

        }
    }

    private class GetLyrics extends AsyncTask<String,Void,Void> {
        List<String> mLineList = new ArrayList<String>();
        private URL mURL;

        @Override
        protected Void doInBackground(String... strings) {
            try {
                mURL = new URL(strings[0]);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                Log.i(TAG, "Scanning lyrics document");

                Scanner scanner = new Scanner(mURL.openStream());
                while (scanner.hasNextLine()){
                    mLineList.add(scanner.nextLine());
                }
                scanner.close();
            }
            catch (Exception ex) {
                Log.i(TAG, "Unable to get lyrics document, ex");

                FragmentManager manager = getFragmentManager();
                NoInternetFragment dialog = new NoInternetFragment();
                dialog.show(manager, DIALOG_NOINTERNET);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (mLineList != null) {
                mLyricsLines = mLineList;
            } else {
                Log.d(TAG, "Unable to get lyrics document");

                FragmentManager manager = getFragmentManager();
                NoInternetFragment dialog = new NoInternetFragment();
                dialog.show(manager, DIALOG_NOINTERNET);
            }

        }
    }
}
