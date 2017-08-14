package grab.com.thuexetoancau.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import grab.com.thuexetoancau.DirectionFinder.DirectionFinder;
import grab.com.thuexetoancau.DirectionFinder.DirectionFinderListener;
import grab.com.thuexetoancau.DirectionFinder.Route;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.LastSearchAdapter;
import grab.com.thuexetoancau.fragment.LastSearchFragment;
import grab.com.thuexetoancau.listener.ChangeTripInfo;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.DialogUtils;
import grab.com.thuexetoancau.utilities.Global;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;
import grab.com.thuexetoancau.widget.ConfirmDialogFragment;
import grab.com.thuexetoancau.widget.DirectionLayout;
import grab.com.thuexetoancau.widget.DriverInformationLayout;
import grab.com.thuexetoancau.widget.RatingFragment;
import grab.com.thuexetoancau.widget.SearchBarLayout;
import grab.com.thuexetoancau.widget.SearchingCarLayout;
import grab.com.thuexetoancau.widget.TransportationLayout;

public class PassengerSelectActionActivity extends AppCompatActivity implements
        SearchBarLayout.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DirectionLayout.DirectionCallback,
        OnMapReadyCallback,
        LastSearchFragment.OnAddNewDirection,
        View.OnClickListener,
        DirectionFinderListener,
        ConfirmDialogFragment.ConfirmDialogListener,
        SearchingCarLayout.SearchingCallBack,
        RatingFragment.RatingCallBackListener,
        NavigationView.OnNavigationItemSelectedListener,
        TransportationLayout.OnTransportationListener {

    private Button btnBooking, btnInfor;
    private RelativeLayout layoutRoot;
    private SearchBarLayout layoutSearch;
    private FrameLayout layoutPredict, layoutFixGPS,layoutOverLay;
    private int searchBarHeight;
    private DirectionLayout layoutDirection;
    private GoogleApiClient mGoogleApiClient;       // google place api
    private LastSearchFragment mPredictFragment;
    private LastSearchAdapter mPlaceArrayAdapter; // Place adapter
    private LinearLayout layoutFindCar, layoutTransport;
    private DriverInformationLayout layoutDriveInfo;
    private SearchingCarLayout layoutSeachingCar;
    private  ConfirmDialogFragment dialogConfirm;
    private GoogleMap mMap;
    private Context mContext;
    private GPSTracker gpsTracker;
    private User user;
    private ArrayList<Position> listStopPoint = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private Marker currentLocation;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<Car> listCar = new ArrayList<>();
    private Position mFrom, mEnd;
    private int totalDistance = 0, typeTrip = 1, totalPrice = 0, carSize = 0;
    private ApiUtilities mApi;
    private ChangeTripInfo changeTrip;
    private Trip lastTrip;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_action);
        initComponents();
        mApi = new ApiUtilities(this);
        listCar = mApi.getPostage();
    }

    private void initComponents(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        btnBooking  = (Button)      findViewById(R.id.btn_booking);
        btnInfor    = (Button)      findViewById(R.id.btn_infor);
        layoutRoot  = (RelativeLayout) findViewById(R.id.root);
        layoutSearch = (SearchBarLayout) findViewById(R.id.layout_search);
        layoutPredict = (FrameLayout) findViewById(R.id.fragment_last_search);
        layoutFindCar = (LinearLayout) findViewById(R.id.layout_find_car);
        layoutOverLay = (FrameLayout) findViewById(R.id.layout_overlay);
        layoutFixGPS = (FrameLayout) findViewById(R.id.layout_fix_gps);
        layoutSearch.setCallback(this);
        btnBooking.setOnClickListener(this);
        layoutFixGPS.setOnClickListener(this);
        btnInfor.setOnClickListener(this);
        setupGoogleApi();
        SupportMapFragment map = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        mContext = this;
        setupActionBarDrawerToogle();
    }
    private void setupActionBarDrawerToogle() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if (getIntent().hasExtra(Defines.BUNDLE_USER)) {
            //receive
            user = (User) getIntent().getSerializableExtra(Defines.BUNDLE_USER);
            TextView txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
            TextView txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);
            ImageView imgAvatar = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.avatar);
            ImageView imgEdit= (ImageView) navigationView.getHeaderView(0).findViewById(R.id.img_edit);
            imgEdit.setOnClickListener(this);
            txtEmail.setText(user.getEmail());
            txtName.setText(user.getName());
            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.loading)
                    .showImageForEmptyUri(R.drawable.loading)
                    .showImageOnFail(R.drawable.loading)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance().displayImage(user.getUrl(), imgAvatar, options, new SimpleImageLoadingListener());
        }
        if (getIntent().hasExtra(Defines.BUNDLE_TRIP)) {
            lastTrip = (Trip) getIntent().getSerializableExtra(Defines.BUNDLE_TRIP);
            showCurrentTripAction();
        }
    }

    private void showCurrentTripAction() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.in_trip));
        hideLayoutSearchOrigin();
        layoutDriveInfo = new DriverInformationLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutDriveInfo.setLayoutParams(params);
        layoutRoot.addView(layoutDriveInfo);
    }

    // Init google api
    private void setupGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, Defines.GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayAdapter = new LastSearchAdapter(this, Defines.BOUNDS_MOUNTAIN_VIEW, null);
    }

    /**
     * Show layout predict location
     * If change exist location, pass to position of it
     */
    private void showLastSearchFragment(boolean changeLocation, int position) {
        mPredictFragment = new LastSearchFragment();
        Bundle bundle = new Bundle();
        if (changeLocation)
            bundle.putInt(Defines.POSITION_POINT,position );
        mPredictFragment.setArguments(bundle);

        // Add predict fragment to layout
        FragmentTransaction fragmentManager =  getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_last_search, mPredictFragment).commit();
        int height = Global.APP_SCREEN_HEIGHT - searchBarHeight;

        AnimUtils.translate(layoutPredict,0,height);
        mPredictFragment.setGoogleApiClient(mGoogleApiClient);
        AnimUtils.fadeIn(layoutOverLay,300);
        AnimUtils.fadeOut(layoutFixGPS,300);
    }

    /**
     *  Hide layout location predict
     */
    private void hideLastSearchFragment() {
        // Remove focus and hide soft keyboard
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        // Animation hide layout location predict
        int height = Global.APP_SCREEN_HEIGHT - searchBarHeight - (int)CommonUtilities.convertDpToPixel(20, this);
        AnimUtils.translate(layoutPredict,height,0);
        AnimUtils.fadeOut(layoutOverLay,300);
        AnimUtils.fadeIn(layoutFixGPS,300);
    }


    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }



    private void showCurrentLocationToMap(double latitude, double longitude){
        if (currentLocation != null)
            currentLocation.remove();
        Position lFrom = new Position(getAddress(latitude, longitude),new LatLng(latitude,longitude));
        listStopPoint.add(lFrom);
        currentLocation = mMap.addMarker(new MarkerOptions().position(lFrom.getLatLng()).title("Vị trí của bạn").icon(BitmapDescriptorFactory.fromResource(R.drawable.current)));
        markerList.add(currentLocation);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentLocation.getPosition())             // Sets the center of the map to current location
                .zoom(16)                   // Sets the zoom
                .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int index = addresses.get(0).getMaxAddressLineIndex();
                for (int i = 0 ; i < index ; i++)
                    if (address.getAddressLine(i) != null) {
                        result.append(address.getAddressLine(i));
                        if (i < index-1)
                            result.append(", ");
                    }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    /**
     * Update layout when click to item of list predict location
     */
    private void changeUIWhenChangedDirecition(){
        layoutSearch.removeSearchText();
        // Hide layout list propose location
        hideLastSearchFragment();
        // Initial and show layout select and booking car
        if (layoutTransport == null) {
            ArrayList<Car> transports = new ArrayList<>();
            for (int i = 0 ; i < listCar.size() ; i++) {
                Car car = new Car(listCar.get(i));
                transports.add(car);
            }
            layoutTransport = new TransportationLayout(this, transports);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutTransport.setLayoutParams(params);
            layoutRoot.addView(layoutTransport);
            layoutTransport.setTranslationY(-measureView(layoutTransport));
        }
        showLayoutDirection();
        hideLayoutSearchOrigin();
        // Start event receive action from layout direction
        layoutDirection.setOnCallBackDirection(this);
    }

    private void hideLayoutDirection(){
        AnimUtils.slideUp(layoutDirection,measureView(layoutDirection));
        AnimUtils.slideDown(layoutTransport,measureView(layoutTransport));
    }

    private void showLayoutDirection(){
        AnimUtils.slideDown(layoutDirection,0);
        AnimUtils.slideUp(layoutTransport,0);
    }

    private void hideLayoutSearchOrigin(){
        // Hide layout search
        AnimUtils.slideUp(layoutSearch,searchBarHeight);
        // Hide layout auction
        AnimUtils.slideDown(layoutFindCar,measureView(layoutFindCar));
    }

    private void showLayoutSearchOrigin(){
        AnimUtils.slideDown(layoutSearch,0);
        AnimUtils.slideUp(layoutFindCar,0);
    }

    private void sendRequestFindDirection() {
        removeAllMarker();
        mFrom = listStopPoint.get(0);
        mEnd = listStopPoint.get(listStopPoint.size()-1);
        try {
            for (Position location : listStopPoint) {
                if (CommonUtilities.distanceInMeter(location.getLatLng(), currentLocation.getPosition()) < Defines.MIN_CURRENT_DISTANCE ){
                    markerList.add(mMap.addMarker(new MarkerOptions()
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                            .title(currentLocation.getTitle())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.current))
                            .position(currentLocation.getPosition())));
                }else {
                    markerList.add(mMap.addMarker(new MarkerOptions()
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                            .title(location.getPrimaryText())
                            .position(location.getLatLng())));
                }
            }
            new DirectionFinder(this, listStopPoint).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void removeAllMarker(){
        totalDistance = 0;
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
        polylinePaths.clear();
        for (Marker marker : markerList)
            marker.remove();
        markerList.clear();
    }

    private void updateMapCamera(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = (int)CommonUtilities.convertDpToPixel(40,mContext); // offset from edges of the map in pixels
        mMap.setPadding(padding,measureView(layoutDirection)+(int)CommonUtilities.convertDpToPixel(50,mContext),padding, measureView(layoutTransport)+(int)CommonUtilities.convertDpToPixel(20,mContext));
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        mMap.animateCamera(cu);
    }

    private void getCurrentPosition(){
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                DialogUtils.settingRequestTurnOnLocation(PassengerSelectActionActivity.this);
            } else
                showCurrentLocationToMap(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
    }

    private void showLayoutSearchingDriver(String bookingId, Trip trip) {
        AnimUtils.fadeOut(layoutFixGPS,300);
        layoutSeachingCar = new SearchingCarLayout(this,this,bookingId , trip);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutSeachingCar.setLayoutParams(params);
        layoutRoot.addView(layoutSeachingCar);
        layoutSeachingCar.setTranslationY(Global.APP_SCREEN_HEIGHT);
        AnimUtils.slideUp(layoutSeachingCar,0);
    }

    public void setOnChangeTripListener (ChangeTripInfo changeTrip){
        this.changeTrip = changeTrip;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_LOCATION_PERMISSIONS) {
            getCurrentPosition();
        }
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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        dialog.dismiss();
                                    }
                                });
                            }
                        });
                    } else {
                        showCurrentLocationToMap(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                        dialog.dismiss();
                    }
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_booking:
                Intent intent = new Intent(PassengerSelectActionActivity.this, FormPassengerBookingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_infor:
                Intent intentInfo = new Intent(PassengerSelectActionActivity.this, ListPassengerBookingActivity.class);
                startActivity(intentInfo);
                break;
            case R.id.img_edit:
                Intent intentEdit= new Intent(PassengerSelectActionActivity.this, ConfigureAccountActivity.class);
                intentEdit.putExtra(Defines.BUNDLE_USER, user);
                startActivity(intentEdit);
                break;
            case R.id.layout_fix_gps:
                gpsTracker = new GPSTracker(this);
                if (gpsTracker.handlePermissionsAndGetLocation()) {
                    if (!gpsTracker.canGetLocation()) {
                        DialogUtils.settingRequestTurnOnLocation(PassengerSelectActionActivity.this);
                    } else{
                        for (Marker marker : markerList)
                            if (marker.getTitle().equals("Vị trí của bạn")){
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(currentLocation.getPosition())// Sets the center of the map to current location
                                        .zoom(16)                   // Sets the zoom
                                        .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                                        .build();                   // Creates a CameraPosition from the builder
                                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                return;
                            }
                        currentLocation.remove();
                        currentLocation = mMap.addMarker(new MarkerOptions()
                                .position(new LatLng(gpsTracker.getLatitude(), gpsTracker.getLongitude()))
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current))
                                .title("Vị trí của bạn"));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(currentLocation.getPosition())// Sets the center of the map to current location
                                .zoom(16)                   // Sets the zoom
                                .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                                .build();                   // Creates a CameraPosition from the builder
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
                break;
        }

    }

    //======================================== Google API implement ================================

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
        getCurrentPosition();
    }

    //======================================== Search bar implement ================================

    /**
     *  Event back button of search bar clicked
     */
    @Override
    public void onBackButtonClicked() {
        hideLastSearchFragment();
        // Check layout search show from direction layout
        if (!layoutSearch.isFinishSearchBar()){
            AnimUtils.slideUp(layoutSearch,measureView(layoutSearch));
            showLayoutDirection();
        }
        layoutSearch.setShowLastSearch(false);
    }

    @Override
    public void onMenuButtonClicked() {
        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     *  Event click to search bar
     */
    @Override
    public void onSearchViewClicked() {
        showLastSearchFragment(false,0);
    }

    @Override
    public void onSearchViewSearching() {

    }

    @Override
    public void onChangeTextSearch(CharSequence s,AutoCompleteTextView edtSearch) {
        mPlaceArrayAdapter.getFilter().filter(s.toString());
        mPredictFragment.setAdapter(mPlaceArrayAdapter);
        mPredictFragment.setCharacter(s.toString());
    }

    @Override
    public void getLayoutSearchHeight(int height) {
        searchBarHeight = height;
    }

    //======================================== Direction implement =================================

    /**
     *  Event back button of direction layout clicked
     */
    @Override
    public void onBackDirectionClicked() {
        showLayoutSearchOrigin();
        hideLayoutDirection();

        layoutSearch.setShowLastSearch(false);
        layoutSearch.setFinishSearchBar(true);

        // Remove layout direction and layout transport from main layout
        layoutDirection = null;
        layoutRoot.removeView(layoutDirection);
        layoutTransport = null;
        layoutRoot.removeView(layoutTransport);
        listStopPoint.clear();
        removeAllMarker();
        getCurrentPosition();
    }

    /**
     *  Event click to change location
     */
    @Override
    public void onDirectionTextClicked(int position) {
        AnimUtils.slideDown(layoutSearch,0);
        hideLayoutDirection();
        layoutSearch.setShowLastSearch(true);
        layoutSearch.requestForcus();
        showLastSearchFragment(true, position);
    }

    /**
     *  Event click to new stop point
     */
    @Override
    public void onNewStopPoint() {
        AnimUtils.slideDown(layoutSearch,0);
        hideLayoutDirection();
        layoutSearch.setShowLastSearch(true);
        layoutSearch.requestForcus();
        showLastSearchFragment(false,0);
    }

    @Override
    public void onRemoveStopPoint(int position) {
        listStopPoint.remove(position);
        sendRequestFindDirection();
    }

    @Override
    public void onSwapLocation(int fromPosition, int toPosition) {
        Collections.swap(listStopPoint, fromPosition, toPosition);
        sendRequestFindDirection();
    }

    @Override
    public void onSetTripType(int type) {
        typeTrip = type;
        if (changeTrip != null)
            changeTrip.onChangeTrip(typeTrip);
    }

    //======================================== Predict location implement ==========================

    @Override
    public void onNewDirection(Position location) {
        layoutSearch.setFinishSearchBar(false);
        String sLocation = location.getPrimaryText() +", "+location.getSecondText();
        if (layoutDirection == null) {
            layoutDirection = new DirectionLayout(this, sLocation);
            layoutDirection.setOnCallBackDirection(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            layoutDirection.setLayoutParams(params);
            layoutRoot.addView(layoutDirection);
            int height = measureView(layoutDirection);
            layoutDirection.setTranslationY(-height);
            listStopPoint.add(location);
        }else{
            layoutDirection.updateLocation(sLocation,-1);
            listStopPoint.add(listStopPoint.size(),location);
        }
        changeUIWhenChangedDirecition();
        sendRequestFindDirection();
    }

    @Override
    public void onChangeLocation(Position location, int position) {
        changeUIWhenChangedDirecition();
        String sLocation = location.getPrimaryText() +", "+location.getSecondText();
        layoutDirection.updateLocation(sLocation,position);
        listStopPoint.set(position, location);
        sendRequestFindDirection();
    }

    //======================================== Direction Finder implement ==========================
    @Override
    public void onDirectionFinderStart() {

    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
            totalDistance+= route.distance.value;
        }
        if (changeTrip != null)
            changeTrip.onChangeDistance(totalDistance);
        updateMapCamera();
        animateLocation();
    }
    //======================================== Select car type implement ===========================

    private void animateLocation(){
        final Marker markerMove = mMap.addMarker(new MarkerOptions().position(mFrom.getLatLng()).title("Vị trí của bạn"));
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = mMap.getProjection();
        Point startPoint = proj.toScreenLocation(markerMove.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5500;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);
                LatLng intermediatePosition = SphericalUtil.interpolate(mFrom.getLatLng(), mEnd.getLatLng(), t);
                markerMove.setPosition(intermediatePosition);


                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    markerMove.setVisible(true);
                }
            }
        });
    }
    private static final int ANIMATE_SPEEED = 1500;
    private static final int ANIMATE_SPEEED_TURN = 1000;
    private static final int BEARING_OFFSET = 20;
    private final Interpolator interpolator = new LinearInterpolator();
    @Override
    public void onBookingClicked() {
        Trip trip = new Trip(user.getId(), user.getName(), user.getPhone(),listStopPoint, typeTrip, totalDistance, carSize,totalPrice );
        FragmentManager fragmentManager = getSupportFragmentManager();
        dialogConfirm = new ConfirmDialogFragment();
        dialogConfirm.setOnCallBack(this);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Defines.DIALOG_CONFIRM_TRIP,trip);
        dialogConfirm.setArguments(bundle);
        dialogConfirm.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        dialogConfirm.setCancelable(false);
        dialogConfirm.setDialogTitle("Xác nhận thông tin");
        dialogConfirm.show(fragmentManager, "Input Dialog");
    }

    @Override
    public void onSelectVehicle(Car car) {
        totalPrice = car.getTotalPrice();
        carSize = car.getSize();
    }

    //====================================== Searching car implement================================

    @Override
    public void onSearchCarSuccess() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.in_trip));
        hideLayoutDirection();
        layoutRoot.removeView(layoutSeachingCar);
        layoutDriveInfo = new DriverInformationLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutDriveInfo.setLayoutParams(params);
        layoutRoot.addView(layoutDriveInfo);
        AnimUtils.fadeIn(layoutFixGPS,300);
        setTheme(R.style.AppTheme);
       /* FragmentManager fragmentManager = getSupportFragmentManager();
        RatingFragment dialogFragment = new RatingFragment();
        dialogFragment.setOnRatingCallBack(this);
        dialogFragment.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        dialogFragment.setCancelable(false);
        dialogFragment.setDialogTitle(getString(R.string.rating_title));
        dialogFragment.show(fragmentManager, "Input Dialog");*/

    }

    @Override
    public void onSearchCarError() {

    }

    @Override
    public void onSearchCarCancel() {
        showLayoutSearchOrigin();
        layoutSearch.setShowLastSearch(false);
        layoutSearch.setFinishSearchBar(true);

        // Remove layout direction and layout transport from main layout
        layoutRoot.removeView(layoutDirection);
        layoutRoot.removeView(layoutTransport);
        layoutTransport = null;
        layoutDirection = null;
        listStopPoint.clear();
        removeAllMarker();
        getCurrentPosition();
    }

    @Override
    public void onConfirmed(final Trip trip) {
       mApi.bookingCar(trip, new ApiUtilities.BookingCarListener() {
           @Override
           public void onSuccess(String bookingId) {
               showLayoutSearchingDriver(bookingId,trip);
           }

           @Override
           public void onFail() {
                Toast.makeText(mContext,mContext.getString(R.string.booking_car_error),Toast.LENGTH_SHORT).show();
           }
       });


    }

    @Override
    public void onRatingSuccess() {
        toolbar.setVisibility(View.GONE);
        showLayoutSearchOrigin();
        layoutRoot.removeView(layoutDriveInfo);
        layoutSearch.setShowLastSearch(false);
        layoutSearch.setFinishSearchBar(true);

        // Remove layout direction and layout transport from main layout
        layoutRoot.removeView(layoutDirection);
        layoutRoot.removeView(layoutTransport);
        layoutDirection = null;
        layoutTransport = null;
        listStopPoint.clear();
        removeAllMarker();
        getCurrentPosition();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_log_out:
                DialogUtils.showLoginDialog((Activity) mContext, new DialogUtils.YesNoListenter() {
                    @Override
                    public void onYes() {
                        mApi.logOut();
                    }

                    @Override
                    public void onNo() {

                    }
                });
                break;
            case R.id.nav_favorite:
                Intent intent = new Intent(mContext, FavoriteTripActivity.class);
                intent.putExtra(Defines.BUNDLE_USER, user.getId());
                startActivity(intent);
                break;

            case R.id.nav_history:
                Intent intentHisroty = new Intent(mContext, HistoryTripActivity.class);
                intentHisroty.putExtra(Defines.BUNDLE_USER, user.getId());
                startActivity(intentHisroty);
                break;

        }
        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }
}
