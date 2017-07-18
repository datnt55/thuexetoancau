package grab.com.thuexetoancau.activity;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.LastSearchAdapter;
import grab.com.thuexetoancau.fragment.LastSearchFragment;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Constants;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;
import grab.com.thuexetoancau.widget.DirectionLayout;
import grab.com.thuexetoancau.widget.SearchBarLayout;

public class PassengerSelectActionActivity extends AppCompatActivity implements
        SearchBarLayout.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DirectionLayout.DirectionCallback,
        OnMapReadyCallback{
    private Button btnBooking, btnInfor;
    private RelativeLayout layoutRoot;
    private SearchBarLayout layoutSearch;
    private FrameLayout layoutLastSearch;
    private int searchBarHeight;
    private DirectionLayout layoutDirection;
    private GoogleApiClient mGoogleApiClient;       // google place api
    private LastSearchFragment lastSearchFragment;
    private LastSearchAdapter mPlaceArrayAdapter; // Place adapter
    private LinearLayout layoutFindCar;
    private GoogleMap mMap;
    private GPSTracker gpsTracker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_action);


        btnBooking  = (Button)      findViewById(R.id.btn_booking);
        btnInfor    = (Button)      findViewById(R.id.btn_infor);
        layoutRoot  = (RelativeLayout) findViewById(R.id.root);
        layoutSearch = (SearchBarLayout) findViewById(R.id.layout_search);
        layoutSearch.setCallback(this);
        layoutLastSearch = (FrameLayout) findViewById(R.id.fragment_last_search);
        layoutFindCar = (LinearLayout) findViewById(R.id.layout_find_car);
        btnBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerSelectActionActivity.this, FormPassengerBookingActivity.class);
                startActivity(intent);
            }
        });

        btnInfor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PassengerSelectActionActivity.this, ListPassengerBookingActivity.class);
                startActivity(intent);
            }
        });
        setupGoogleApi();
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
    }


    // Init google api
    private void setupGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, Constants.GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayAdapter = new LastSearchAdapter(this, Constants.BOUNDS_MOUNTAIN_VIEW, null);
    }


    private void showLastSearchFragment(int typeLocation) {
        layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
        lastSearchFragment = new LastSearchFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constants.TYPE_POINT,typeLocation );
        lastSearchFragment.setArguments(bundle);
        FragmentTransaction fragmentManager =  getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_last_search, lastSearchFragment).commit();
        int height = Defines.APP_SCREEN_HEIGHT - searchBarHeight - (int)CommonUtilities.convertDpToPixel(20, this);
        animationTranslateView(0,height);
        lastSearchFragment.setGoogleApiClient(mGoogleApiClient);
    }

    private void hideLastSearchFragment() {
        layoutRoot.setBackgroundResource(R.drawable.bg_passenger_infor);
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        int height = Defines.APP_SCREEN_HEIGHT - searchBarHeight - (int)CommonUtilities.convertDpToPixel(20, this);
        animationTranslateView(height,0);
    }

    private void animationTranslateView (int from , int to){
        ValueAnimator mAnimator = ValueAnimator.ofFloat(from , to);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float a = (Float) animation.getAnimatedValue();
                int x = a.intValue();
                layoutLastSearch.getLayoutParams().height = x;
                layoutLastSearch.requestLayout();
            }
        });
        mAnimator.setDuration(400);
        mAnimator.start();
    }

    public void goToBookingCar(Position location, int directionType){
        layoutSearch.removeSearchText();
        layoutSearch.setTranslationY(-searchBarHeight);
        layoutSearch.setTranslationY(-searchBarHeight);
        hideLastSearchFragment();
        //layoutSearch.animate().translationY(0).setDuration(300);
        String sLocation = location.getPrimaryText() +", "+location.getSecondText();
        if (layoutDirection == null) {
            layoutDirection = new DirectionLayout(this, sLocation);
            layoutDirection.setOnCallBackDirection(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutDirection.setLayoutParams(params);
            layoutRoot.addView(layoutDirection);
        }else{
            layoutDirection.updateLocation(sLocation, directionType);
        }
        int height = measureView(layoutDirection);
        layoutDirection.setTranslationY(-height);
        layoutDirection.animate()
                .translationY(0)
                .setInterpolator(AnimUtils.EASE_OUT_EASE_IN)
                .setDuration(400)
                .start();
        layoutDirection.setOnCallBackDirection(this);
        layoutFindCar.animate()
                .alpha(0)
                .setInterpolator(AnimUtils.EASE_OUT_EASE_IN)
                .setDuration(1000)
                .start();

        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View transportationLayout = inflater.inflate(R.layout.layout_transportation, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        transportationLayout.setLayoutParams(params);
        layoutRoot.addView(transportationLayout);

    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    // Show dialog request turn on gps
    private void settingRequestTurnOnLocation() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.notice);  // GPS not found
        alertDialogBuilder.setMessage(R.string.gps_notice_content)
                .setCancelable(false)
                .setPositiveButton(R.string.gps_continue,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent,1000);
                            }
                        });
        alertDialogBuilder.setNegativeButton(R.string.gps_no,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void showCurrentLocationToMap(double latitude, double longitude){
        LatLng curLatLng = new LatLng(latitude, longitude);
        Marker markerTo = mMap.addMarker(new MarkerOptions().position(curLatLng).title("Vị trí của bạn"));
     /*   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));*/

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(curLatLng)             // Sets the center of the map to current location
                .zoom(16)                   // Sets the zoom
                .tilt(45)                   // Sets the tilt of the camera to 0 degrees

                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                gpsTracker = new GPSTracker(this);
                if (gpsTracker.canGetLocation()) {
                    final ProgressDialog dialog = new ProgressDialog(this);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage(getResources().getString(R.string.getting_location));
                    dialog.show();
                    if (gpsTracker.getLongitude() == 0 && gpsTracker.getLatitude() == 0) {
                        gpsTracker.getLocationCoodinate(new GPSTracker.LocateListener() {
                            @Override
                            public void onLocate(double mlongitude, double mlatitude) {
                                showCurrentLocationToMap(mlatitude,mlongitude);
                            }
                        });
                    } else {
                        showCurrentLocationToMap(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                    }
                }
                break;
        }
    }

    @Override
    public void onBackButtonClicked() {
        hideLastSearchFragment();

    }

    @Override
    public void onBackDirectionClicked() {
        int height = measureView(layoutDirection);
        layoutDirection.animate()
                .translationY(-height)
                .setInterpolator(AnimUtils.EASE_OUT_EASE_IN)
                .setDuration(400)
                .start();

        layoutSearch.animate()
                .translationY(0)
                .setInterpolator(AnimUtils.EASE_OUT_EASE_IN)
                .setDuration(400)
                .start();
        layoutSearch.setShowLastSearch(false);
    }

    @Override
    public void onDirectionClicked(int type) {
        int height = measureView(layoutDirection);
        layoutDirection.animate()
                .translationY(-height)
                .setInterpolator(AnimUtils.EASE_OUT_EASE_IN)
                .setDuration(400)
                .start();

        layoutSearch.animate()
                .translationY(0)
                .setInterpolator(AnimUtils.EASE_OUT_EASE_IN)
                .setDuration(400)
                .start();
        layoutSearch.setShowLastSearch(true);
        showLastSearchFragment(type);
    }

    @Override
    public void onMenuButtonClicked() {

    }

    @Override
    public void onSearchViewClicked() {
        showLastSearchFragment(Constants.DIRECTION_ENDPOINT);
    }

    @Override
    public void onSearchViewSearching() {

    }

    @Override
    public void onChangeTextSearch(CharSequence s,AutoCompleteTextView edtSearch) {
        mPlaceArrayAdapter.getFilter().filter(s.toString());
        lastSearchFragment.setAdapter(mPlaceArrayAdapter);
        lastSearchFragment.setCharacter(s.toString());
    }

    @Override
    public void getLayoutSearchHeight(int height) {
        searchBarHeight = height;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayAdapter.setGoogleApiClient(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.check_connection, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                settingRequestTurnOnLocation();
            } else
                showCurrentLocationToMap(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
    }

}
