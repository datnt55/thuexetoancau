package grab.com.thuexetoancau.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import grab.com.thuexetoancau.DirectionFinder.DirectionFinder;
import grab.com.thuexetoancau.DirectionFinder.DirectionFinderListener;
import grab.com.thuexetoancau.DirectionFinder.Route;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.LastSearchAdapter;
import grab.com.thuexetoancau.fragment.LastSearchFragment;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Constants;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;
import grab.com.thuexetoancau.widget.ConfirmDialogFragment;
import grab.com.thuexetoancau.widget.DirectionLayout;
import grab.com.thuexetoancau.widget.DriverInformationLayout;
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
        SearchingCarLayout.SearchingCallBack,
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
    private GoogleMap mMap;
    private Context mContext;
    private GPSTracker gpsTracker;
    private User user;
    private ArrayList<Position> listStopPoint = new ArrayList<>();
    private ArrayList<Marker> markerList = new ArrayList<>();
    private Marker currentLocation;
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Position mFrom, mEnd;
    private int typeTrip = 1;
    private int totalDistance = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passenger_select_action);
        initComponents();
    }

    private void initComponents(){
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
        final DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                return true;
            }
        });
        if (getIntent().hasExtra(Constants.BUNDLE_USER)) {
            //receive
            user = (User) getIntent().getSerializableExtra(Constants.BUNDLE_USER);
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

    /**
     * Show layout predict location
     * If change exist location, pass to position of it
     */
    private void showLastSearchFragment(boolean changeLocation, int position) {
        mPredictFragment = new LastSearchFragment();
        Bundle bundle = new Bundle();
        if (changeLocation)
            bundle.putInt(Constants.POSITION_POINT,position );
        mPredictFragment.setArguments(bundle);

        // Add predict fragment to layout
        FragmentTransaction fragmentManager =  getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_last_search, mPredictFragment).commit();
        int height = Defines.APP_SCREEN_HEIGHT - searchBarHeight - (int)CommonUtilities.convertDpToPixel(20, this);

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
        int height = Defines.APP_SCREEN_HEIGHT - searchBarHeight - (int)CommonUtilities.convertDpToPixel(20, this);
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
        // Show layout direction
        AnimUtils.slideDown(layoutDirection,0);
        // Hide layout search
        AnimUtils.slideUp(layoutSearch,searchBarHeight);
        // Hide layout auction
        AnimUtils.slideDown(layoutFindCar,measureView(layoutFindCar));
        // Start event receive action from layout direction
        layoutDirection.setOnCallBackDirection(this);
        // Initial and show layout select and booking car
        if (layoutTransport == null) {
            layoutTransport = new TransportationLayout(this);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            layoutTransport.setLayoutParams(params);
            layoutRoot.addView(layoutTransport);
            layoutTransport.setTranslationY(-measureView(layoutTransport));
        }
        AnimUtils.slideUp(layoutTransport,0);
    }

    private void sendRequestFindDirection() {
        removeAllMarker();
        try {
            for (int i= 0; i< listStopPoint.size()-1; i++) {
                mFrom = listStopPoint.get(i);
                Position mTo = listStopPoint.get(i+1);
                if (CommonUtilities.distanceInMeter(mFrom.getLatLng(), currentLocation.getPosition()) < Defines.MIN_CURRENT_DISTANCE ){
                    markerList.add(mMap.addMarker(new MarkerOptions()
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                            .title(currentLocation.getTitle())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.current))
                            .position(currentLocation.getPosition())));
                }else {
                    markerList.add(mMap.addMarker(new MarkerOptions()
                            //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                            .title(mFrom.getPrimaryText())
                            .position(mFrom.getLatLng())));
                }
                if (i == listStopPoint.size()-2) {
                    mEnd = listStopPoint.get(i + 1);
                    if (CommonUtilities.distanceInMeter(mEnd.getLatLng(), currentLocation.getPosition()) < Defines.MIN_CURRENT_DISTANCE ){
                        markerList.add(mMap.addMarker(new MarkerOptions()
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                                .title(currentLocation.getTitle())
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.current))
                                .position(currentLocation.getPosition())));
                    }else {
                        markerList.add(mMap.addMarker(new MarkerOptions()
                                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                                .title(mEnd.getPrimaryText())
                                .position(mEnd.getLatLng())));
                    }
                    updateMapCamera();
                }
                new DirectionFinder(this, mFrom.getLatLng(), mTo.getLatLng()).execute();
            }
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

    private void showDialogBooking() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_booking);

        TextView txtSource = (TextView) dialog.findViewById(R.id.txt_source);
        txtSource.setText(listStopPoint.get(0).getFullPlace());
        TextView txtDestination = (TextView) dialog.findViewById(R.id.txt_destination);
        txtDestination.setText(listStopPoint.get(listStopPoint.size()-1).getFullPlace());
        TextView txtTypeTrip = (TextView) dialog.findViewById(R.id.trip_type);
        txtTypeTrip.setText(CommonUtilities.getTripType(typeTrip));
        TextView txtDistance = (TextView) dialog.findViewById(R.id.distance);
        txtDistance.setText(CommonUtilities.convertToKilometer(totalDistance));

        final TextView txtStartTime = (TextView) dialog.findViewById(R.id.start_time);
        TextView txtBackTime = (TextView) dialog.findViewById(R.id.back_time);

        if (totalDistance < Defines.MAX_DISTANCE) {
            txtStartTime.setVisibility(View.GONE);
            txtBackTime.setVisibility(View.GONE);
        }else if (typeTrip == 1){
            txtBackTime.setVisibility(View.GONE);
        }

        txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(txtStartTime);
            }
        });

        txtBackTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimeDialog(txtStartTime);
            }
        });
        dialog.show();

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = dialog.getWindow();
        lp.copyFrom(window.getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(lp);
    }

    private void updateMapCamera(){
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerList) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = (int)CommonUtilities.convertDpToPixel(30,mContext); // offset from edges of the map in pixels
        mMap.setPadding(padding,measureView(layoutDirection)+(int)CommonUtilities.convertDpToPixel(50,mContext),padding, measureView(layoutTransport));
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 0);
        mMap.animateCamera(cu);
    }

    private void showDateTimeDialog(final TextView txtDate){
        final View dialogView = View.inflate(mContext, R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
        final DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.datepicker);
        datePicker.setMinDate(System.currentTimeMillis() - 1000);
        final TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.timepicker);

        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker timePicker, int mHour, int mMinute) {
                Calendar now = Calendar.getInstance();
                int year = now.get(Calendar.YEAR);
                int month = now.get(Calendar.MONTH); // Note: zero based!
                int day = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);
                if (datePicker.getYear() == year && datePicker.getMonth() == month && datePicker.getDayOfMonth() == day ){
                    if (mHour <= hour) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                            if (hour > 22)
                                timePicker.setHour(hour);
                            else
                                timePicker.setHour(hour+1);
                            timePicker.setMinute(minutes);
                        }else {
                            if (hour > 22)
                                timePicker.setCurrentHour(hour);
                            else
                                timePicker.setCurrentHour(hour+1);

                            timePicker.setCurrentMinute(minutes);
                        }
                    }
                }
            }
        });
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22){
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY));
            }else
                timePicker.setHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
        }else {
            if (calendar.get(Calendar.HOUR_OF_DAY) > 22)
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
            else
                timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY)+1);
        }
        datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                Calendar now = Calendar.getInstance();
                int cYear = now.get(Calendar.YEAR);
                int cMonth = now.get(Calendar.MONTH);
                int cDay = now.get(Calendar.DAY_OF_MONTH);
                int hour = now.get(Calendar.HOUR_OF_DAY);
                int minutes = now.get(Calendar.MINUTE);
                if (cYear == year && cMonth == month && cDay == dayOfMonth ) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (hour > 22)
                            timePicker.setHour(hour);
                        else
                            timePicker.setHour(hour+1);
                        timePicker.setMinute(minutes);
                    }else {
                        if (hour > 22)
                            timePicker.setCurrentHour(hour);
                        else
                            timePicker.setCurrentHour(hour+1);

                        timePicker.setCurrentMinute(minutes);
                    }
                }

            }
        });
        dialogView.findViewById(R.id.datetimeset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());
                SimpleDateFormat mSDF = new SimpleDateFormat("HH:mm:ss");
                String time = mSDF.format(calendar.getTime());
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth();
                int year = datePicker.getYear();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
                String formatedDate = sdf.format(new Date(year-1900, month, day));
                txtDate.setText(formatedDate + ' ' + time);
                alertDialog.dismiss();

            }
        });

        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void getCurrentPosition(){
        gpsTracker = new GPSTracker(this);
        if (gpsTracker.handlePermissionsAndGetLocation()) {
            if (!gpsTracker.canGetLocation()) {
                CommonUtilities.settingRequestTurnOnLocation(PassengerSelectActionActivity.this);
            } else
                showCurrentLocationToMap(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Constants.REQUEST_CODE_LOCATION_PERMISSIONS) {
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
                intentEdit.putExtra(Constants.BUNDLE_USER, user);
                startActivity(intentEdit);
                break;
            case R.id.layout_fix_gps:
                gpsTracker = new GPSTracker(this);
                if (gpsTracker.handlePermissionsAndGetLocation()) {
                    if (!gpsTracker.canGetLocation()) {
                        CommonUtilities.settingRequestTurnOnLocation(PassengerSelectActionActivity.this);
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
            AnimUtils.slideDown(layoutDirection,0);
            AnimUtils.slideUp(layoutSearch,measureView(layoutSearch));
            AnimUtils.slideUp(layoutTransport,0);
        }
        layoutSearch.setShowLastSearch(false);
    }

    @Override
    public void onMenuButtonClicked() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
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
        // Hide layout direction and show layout search
        AnimUtils.slideUp(layoutDirection,measureView(layoutDirection));
        AnimUtils.slideDown(layoutSearch,0);

        // Hide layout transport and show layout auction car
        AnimUtils.slideDown(layoutTransport,measureView(layoutTransport));
        AnimUtils.slideUp(layoutFindCar,0);

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
        AnimUtils.slideUp(layoutDirection, measureView(layoutDirection));
        AnimUtils.slideDown(layoutSearch,0);
        AnimUtils.slideDown(layoutTransport,measureView(layoutTransport));
        layoutSearch.setShowLastSearch(true);
        layoutSearch.requestForcus();
        showLastSearchFragment(true, position);
    }

    /**
     *  Event click to new stop point
     */
    @Override
    public void onNewStopPoint() {
        AnimUtils.slideUp(layoutDirection,measureView(layoutDirection));
        AnimUtils.slideDown(layoutSearch,0);
        AnimUtils.slideDown(layoutTransport,measureView(layoutTransport));
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
    }

    //======================================== Select car type implement ===========================

    @Override
    public void onBookingClicked() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        ConfirmDialogFragment inputNameDialog = new ConfirmDialogFragment();
        inputNameDialog.setStyle(DialogFragment.STYLE_NORMAL, R.style.CustomDialog);
        inputNameDialog.setCancelable(false);
        inputNameDialog.setDialogTitle("Xác nhận thông tin");
        inputNameDialog.show(fragmentManager, "Input Dialog");
        //showDialogBooking();
       /* AnimUtils.fadeOut(layoutFixGPS,300);
        SearchingCarLayout layout = new SearchingCarLayout(this,this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layout.setLayoutParams(params);
        layoutRoot.addView(layout);
        layout.setTranslationY(Defines.APP_SCREEN_HEIGHT);
        AnimUtils.slideUp(layout,0);*/
    }

    @Override
    public void onSelectVehicle(Car car) {

    }

    //====================================== Searching car implement================================

    @Override
    public void onSearchCarSuccess() {
        AnimUtils.slideUp(layoutDirection,measureView(layoutDirection));
        // Hide layout transport and show layout auction car
        AnimUtils.slideDown(layoutTransport,measureView(layoutTransport));
        DriverInformationLayout layoutInfo = new DriverInformationLayout(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutInfo.setLayoutParams(params);
        layoutRoot.addView(layoutInfo);
        AnimUtils.fadeIn(layoutFixGPS,300);
    }

    @Override
    public void onSearchCarError() {

    }

    @Override
    public void onSearchCarBack() {

    }
}
