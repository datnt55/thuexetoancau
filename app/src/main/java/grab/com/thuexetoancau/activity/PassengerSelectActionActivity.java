package grab.com.thuexetoancau.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.SphericalUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
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
import grab.com.thuexetoancau.model.AroundCar;
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
import grab.com.thuexetoancau.utilities.SharePreference;
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
    private ArrayList<Marker> aroundList = new ArrayList<>();
    private Marker currentLocation;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private ArrayList<Car> listCar = new ArrayList<>();
    private Position mFrom, mEnd;
    private int typeTrip = 1,bookingId;
    private Car carSelectd;
    private int totalDistance = 0;
    private ApiUtilities mApi;
    private ChangeTripInfo changeTrip;
    private Trip lastTrip;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private TextView txtName, txtEmail;
    private boolean showRatingDialog = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_action);
        mApi = new ApiUtilities(this);
        if (getIntent().hasExtra(Defines.BUNDLE_LOGIN_USER))
            user = (User) getIntent().getSerializableExtra(Defines.BUNDLE_LOGIN_USER);
        if (user == null){
            mApi.checkTokenLogin(new ApiUtilities.ResponseLoginListener() {
                @Override
                public void onSuccess(Trip trip, User mUser) {
                    user = mUser;
                    initComponents();
                    getIntentFromFirebase();
                }

                @Override
                public void onFail() {

                }
            });
        }else
            initComponents();
        listCar = mApi.getPostage();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiveTrip, new IntentFilter(Defines.BROADCAST_RECEIVED_TRIP));
        LocalBroadcastManager.getInstance(this).registerReceiver(tripCancel, new IntentFilter(Defines.BROADCAST_CANCEL_TRIP));
        LocalBroadcastManager.getInstance(this).registerReceiver(notFoundDriver, new IntentFilter(Defines.BROADCAST_NOT_FOUND_DRIVER));
        LocalBroadcastManager.getInstance(this).registerReceiver(confirmTrip, new IntentFilter(Defines.BROADCAST_CONFFIRM_TRIP));
        if (getIntent().hasExtra(Defines.BUNDLE_LOGIN_TRIP)) {
            lastTrip = (Trip) getIntent().getSerializableExtra(Defines.BUNDLE_LOGIN_TRIP);
            if (lastTrip.getDriverId() == 0){
                hideLayoutSearchOrigin();
                showLayoutSearchingDriver(lastTrip.getId(), lastTrip);
            }else {
                showCurrentTripAction();
            }
        }
    }

    private void getIntentFromFirebase(){
        // Check customer have trip after login
        if (getIntent().hasExtra(Defines.BUNDLE_LOGIN_TRIP)) {
            lastTrip = (Trip) getIntent().getSerializableExtra(Defines.BUNDLE_LOGIN_TRIP);
            if (lastTrip.getDriverId() == 0){
                hideLayoutSearchOrigin();
                showLayoutSearchingDriver(lastTrip.getId(), lastTrip);
            }else {
                showCurrentTripAction();
            }
        }

        // Check customer finish trip status
        if (getIntent().hasExtra(Defines.BUNDLE_CONFIRM_TRIP)) {
            int bookingId = getIntent().getIntExtra(Defines.BUNDLE_TRIP_ID,0);
            String driverName = getIntent().getStringExtra(Defines.BUNDLE_DRIVER_NAME);
            showRatingDialog(bookingId, driverName);
        }

        // Check customer found driver
        if (getIntent().hasExtra(Defines.BUNDLE_FOUND_DRIVER)) {
            int bookingId = getIntent().getIntExtra(Defines.BUNDLE_TRIP_ID,0);
            User userDriver = (User) getIntent().getSerializableExtra(Defines.BUNDLE_DRIVER);
            int tripType = getIntent().getIntExtra(Defines.BUNDLE_TRIP_TYPE,0);
            if (tripType == 1) {
                foundDriverUI(userDriver, bookingId);
                hideLayoutSearchOrigin();
            }
        }
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
        if (user != null) {
            //receive
            txtName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_name);
            txtEmail = (TextView) navigationView.getHeaderView(0).findViewById(R.id.txt_email);
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
            ImageLoader.getInstance().displayImage("http://icongal.com/gallery/image/270615/admin_administrator_customer_user_person_face_admnistrator_support_custom.png", imgAvatar, options, new SimpleImageLoadingListener());
        }
    }

    private void showCurrentTripAction() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.in_trip));
        hideLayoutSearchOrigin();
        layoutDriveInfo = new DriverInformationLayout(this,new User(lastTrip.getDriverName(),lastTrip.getDriverPhone(),lastTrip.getDriverCarNumber()));
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
                if (index == 0)
                    return address.getAddressLine(0);
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
        if (layoutDirection != null)
            AnimUtils.slideUp(layoutDirection,measureView(layoutDirection));
        if (layoutTransport != null)
            AnimUtils.slideDown(layoutTransport,measureView(layoutTransport));
    }

    private void showLayoutDirection(){
        if (layoutDirection != null)
            AnimUtils.slideDown(layoutDirection,0);
        if (layoutTransport != null)
            AnimUtils.slideUp(layoutTransport,0);
    }

    private void hideLayoutSearchOrigin(){
        // Hide layout search
        AnimUtils.slideUp(layoutSearch,searchBarHeight);
        // Hide layout auction
       // AnimUtils.slideDown(layoutFindCar,measureView(layoutFindCar));
    }

    private void showLayoutSearchOrigin(){
        AnimUtils.slideDown(layoutSearch,0);
       // AnimUtils.slideUp(layoutFindCar,0);
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
        mApi.getCarAround(gpsTracker, new ApiUtilities.AroundCarListener() {
            @Override
            public void onSuccess(ArrayList<AroundCar> aroundCars) {
                for (AroundCar car : aroundCars){
                    DecimalFormat df = new DecimalFormat("#.#");
                    String gap;
                    if ((int) car.getDistance() == 0) {
                        String meter = df.format( car.getDistance() * 1000);
                        gap = mContext.getResources().getString(R.string.distance_meter, meter);
                    }else {
                        String kilometer = df.format( car.getDistance());
                        gap = mContext.getResources().getString(R.string.distance_kilo_meter, kilometer);
                    }
                    LatLng aroundLatLon = new LatLng(car.getLatitude(), car.getLongitude());
                    Marker marker = mMap.addMarker(new MarkerOptions().position(aroundLatLon).title(mContext.getResources().getString(R.string.distance_car,gap)));
                    marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car));
                    aroundList.add(marker);
                }
            }
        });
    }

    private void showLayoutSearchingDriver(int bookingId, Trip trip) {
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
            case Defines.CONFIGURE_CODE:
                if (resultCode == RESULT_OK) {
                    // get String data from Intent
                    User user = (User) data.getSerializableExtra(Defines.BUNDLE_USER);
                    this.user = user;
                    txtEmail.setText(user.getEmail());
                    txtName.setText(user.getName());
                }
                break;
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_booking:
                Intent intent = new Intent(PassengerSelectActionActivity.this, AuctionBookingActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_infor:
                Intent intentInfo = new Intent(PassengerSelectActionActivity.this, ListPassengerBookingActivity.class);
                startActivity(intentInfo);
                break;
            case R.id.img_edit:
                Intent intentEdit= new Intent(PassengerSelectActionActivity.this, ConfigureAccountActivity.class);
                intentEdit.putExtra(Defines.BUNDLE_USER, user);
                startActivityForResult(intentEdit, Defines.CONFIGURE_CODE);
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

    @Override
    protected void onResume() {
        super.onResume();
        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(Defines.NOTIFY_TAG, bookingId);
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
                    width(12);
            PolylineOptions polylineOptions1 = new PolylineOptions().
                    geodesic(true).
                    color(ContextCompat.getColor(mContext,R.color.blue_light)).
                    width(8);
            for (int i = 0; i < route.points.size(); i++) {
                polylineOptions.add(route.points.get(i));
                polylineOptions1.add(route.points.get(i));
            }

            polylinePaths.add(mMap.addPolyline(polylineOptions));
            polylinePaths.add(mMap.addPolyline(polylineOptions1));
            totalDistance+= route.distance.value;
        }
        if (changeTrip != null)
            changeTrip.onChangeDistance(totalDistance);
        updateMapCamera();
        //animateLocation();
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
        Trip trip = new Trip(user.getId(), user.getName(), user.getPhone(),listStopPoint, typeTrip, totalDistance, carSelectd.getSize(),carSelectd.getTotalPrice() );
        trip.setCar(carSelectd.isCar());
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
        carSelectd = car;
    }

    //====================================== Searching car implement================================

    @Override
    public void onSearchCarSuccess() {
        /*toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.in_trip));
        hideLayoutDirection();
        layoutRoot.removeView(layoutSeachingCar);
        layoutDriveInfo = new DriverInformationLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutDriveInfo.setLayoutParams(params);
        layoutRoot.addView(layoutDriveInfo);
        AnimUtils.fadeIn(layoutFixGPS,300);*/
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
        finishTripAndUpdateView();
    }

    @Override
    public void onConfirmed(final Trip trip) {
        if (trip.getDistance() > Defines.MAX_DISTANCE) {
            if (trip.getStartTime() != null) {
                DateTimeFormatter dtf = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
                DateTime startTime = dtf.parseDateTime(trip.getStartTime());
                final boolean isToday = CommonUtilities.isToday(startTime);
                if (!isToday) {
                    mApi.getScheduleTrip(user.getId(), new ApiUtilities.ResponseTripListener() {
                        @Override
                        public void onSuccess(ArrayList<Trip> arrayTrip) {
                            if (arrayTrip == null)
                                bookingImmediateTrip(trip,isToday);
                            else {
                                if (arrayTrip.size() > 2)
                                    Toast.makeText(mContext, "Bạn đã đăng quá 3 chuyến đi sau", Toast.LENGTH_SHORT).show();
                                else
                                    bookingImmediateTrip(trip, isToday);
                            }
                        }
                    });
                }else
                    bookingImmediateTrip(trip,isToday );
            }
        }else
            bookingImmediateTrip(trip,false);
    }

    private void bookingImmediateTrip (final Trip trip, final boolean isToday){
        mApi.bookingCar(trip, new ApiUtilities.BookingCarListener() {
            @Override
            public void onSuccess(int bookingId) {
                if (trip.getDistance() > Defines.MAX_DISTANCE && !isToday) {
                    DialogUtils.bookingLongTrip((Activity) mContext, new DialogUtils.YesNoListenter() {
                        @Override
                        public void onYes() {
                            finishTripAndUpdateView();
                        }

                        @Override
                        public void onNo() {

                        }
                    });
                } else
                    showLayoutSearchingDriver(bookingId, trip);
            }

            @Override
            public void onFail() {
                Toast.makeText(mContext, mContext.getString(R.string.booking_car_error), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void foundDriverUI(User user, int bookingId){
        if (layoutSeachingCar != null) {
            AnimUtils.slideDown(layoutSeachingCar, Global.APP_SCREEN_HEIGHT);
            layoutRoot.removeView(layoutSeachingCar);
        }
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle(getString(R.string.in_trip));
        hideLayoutDirection();
        layoutDriveInfo = new DriverInformationLayout(mContext,user);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutDriveInfo.setLayoutParams(params);
        layoutRoot.addView(layoutDriveInfo);
        AnimUtils.fadeIn(layoutFixGPS,300);
    }
    BroadcastReceiver receiveTrip = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                User userDriver = (User) intent.getSerializableExtra(Defines.BUNDLE_DRIVER);
                bookingId = intent.getIntExtra(Defines.BUNDLE_TRIP,0);
                foundDriverUI(userDriver, bookingId);
                Toast.makeText(mContext, "Chúng tôi đã tìm thấy tài xế cho bạn",Toast.LENGTH_SHORT).show();
            } catch (IllegalStateException e) {
            }
        }
    };

    BroadcastReceiver notFoundDriver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                bookingId = intent.getIntExtra(Defines.BUNDLE_TRIP,0);
                AnimUtils.slideDown(layoutSeachingCar, Global.APP_SCREEN_HEIGHT);
                Toast.makeText(mContext,"Rất tiếc, Không có tài xế nào quanh bạn", Toast.LENGTH_LONG).show();
            } catch (IllegalStateException e) {
            }
        }
    };

    BroadcastReceiver confirmTrip = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                bookingId = intent.getIntExtra(Defines.BUNDLE_TRIP,0);
                String driverName = intent.getStringExtra(Defines.BUNDLE_DRIVER_NAME);
                showRatingDialog(bookingId, driverName);

            } catch (IllegalStateException e) {
            }
        }
    };

    private void showRatingDialog(int bookingId, String driverName){
        FragmentManager fragmentManager = getSupportFragmentManager();
        RatingFragment dialogRating = new RatingFragment();
        dialogRating.setOnRatingCallBack(PassengerSelectActionActivity.this);
        dialogRating.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Defines.BUNDLE_USER,user);
        bundle.putInt(Defines.BUNDLE_TRIP,bookingId);
        bundle.putString(Defines.BUNDLE_DRIVER_NAME,driverName);
        dialogRating.setArguments(bundle);
        dialogRating.setCancelable(false);
        dialogRating.setDialogTitle(getString(R.string.rating_title));
        getSupportFragmentManager().beginTransaction().add(dialogRating, "tag")
                .commitAllowingStateLoss();
    }

    BroadcastReceiver tripCancel = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                DialogUtils.cancelTripFromDriver((Activity) mContext, new DialogUtils.YesNoListenter() {
                    @Override
                    public void onYes() {
                        finishTripAndUpdateView();
                    }

                    @Override
                    public void onNo() {

                    }
                });
            } catch (IllegalStateException e) {
            }
        }
    };


    @Override
    public void onRatingSuccess() {
        Toast.makeText(this, getString(R.string.review_message),Toast.LENGTH_SHORT).show();
        finishTripAndUpdateView();

    }

    private void finishTripAndUpdateView(){
        toolbar.setVisibility(View.GONE);
        showLayoutSearchOrigin();
        if (layoutDriveInfo != null)
            layoutRoot.removeView(layoutDriveInfo);
        layoutSearch.setShowLastSearch(false);
        layoutSearch.setFinishSearchBar(true);

        // Remove layout direction and layout transport from main layout
        if (layoutDirection != null) {
            layoutRoot.removeView(layoutDirection);
            layoutDirection = null;
        }
        if (layoutTransport != null) {
            layoutRoot.removeView(layoutTransport);
            layoutTransport = null;
        }
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
                        mApi.logOut(new ApiUtilities.ResponseRequestListener() {
                            @Override
                            public void onSuccess() {
                                SharePreference preference = new SharePreference(mContext);
                                FirebaseAuth.getInstance().signOut();
                                /*if (mGoogleApiClient.isConnected())
                                    Auth.GoogleSignInApi.signOut(mGoogleApiClient);*/
                                Toast.makeText(mContext, mContext.getResources().getString(R.string.log_out_success), Toast.LENGTH_SHORT).show();
                                preference.clearToken();
                                Intent intent = new Intent(mContext, SplashActivity.class);
                                mContext.startActivity(intent);
                                ((Activity)mContext).finish();
                            }

                            @Override
                            public void onFail() {

                            }
                        });

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
            case R.id.nav_schedule:
                Intent intentSchedule = new Intent(mContext, ScheduleTripActivity.class);
                intentSchedule.putExtra(Defines.BUNDLE_USER, user.getId());
                startActivity(intentSchedule);
                break;

            case R.id.nav_notify:
                Toast.makeText(mContext, "Chức năng đang được cập nhật",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_invite:
                Toast.makeText(mContext, "Chức năng đang được cập nhật",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_support:
                Toast.makeText(mContext, "Chức năng đang được cập nhật",Toast.LENGTH_SHORT).show();
                break;

        }
        item.setChecked(true);
        drawerLayout.closeDrawers();
        return true;
    }
}
