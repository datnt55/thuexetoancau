package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.DirectionFinder.DirectionFinder;
import grab.com.thuexetoancau.DirectionFinder.DirectionFinderListener;
import grab.com.thuexetoancau.DirectionFinder.Route;
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
import grab.com.thuexetoancau.widget.TransportationLayout;

public class PassengerSelectActionActivity extends AppCompatActivity implements
        SearchBarLayout.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DirectionLayout.DirectionCallback,
        OnMapReadyCallback,
        LastSearchFragment.OnAddNewDirection,
        View.OnClickListener,DirectionFinderListener {

    private Button btnBooking, btnInfor;
    private RelativeLayout layoutRoot;
    private SearchBarLayout layoutSearch;
    private FrameLayout layoutPredict;
    private int searchBarHeight;
    private DirectionLayout layoutDirection;
    private GoogleApiClient mGoogleApiClient;       // google place api
    private LastSearchFragment mPredictFragment;
    private LastSearchAdapter mPlaceArrayAdapter; // Place adapter
    private LinearLayout layoutFindCar, layoutTransport;
    private GoogleMap mMap;
    private Context mContext;
    private GPSTracker gpsTracker;
    private ArrayList<Position> listStopPoint = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private Position mFrom, mEnd;
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

        layoutSearch.setCallback(this);
        btnBooking.setOnClickListener(this);
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
    private void showLastSearchFragment(boolean addnew, int position) {
        mPredictFragment = new LastSearchFragment();
        Bundle bundle = new Bundle();
        if (addnew)
            bundle.putInt(Constants.POSITION_POINT,position );
        mPredictFragment.setArguments(bundle);

        // Add predict fragment to layout
        FragmentTransaction fragmentManager =  getSupportFragmentManager().beginTransaction();
        fragmentManager.replace(R.id.fragment_last_search, mPredictFragment).commit();
        int height = Defines.APP_SCREEN_HEIGHT - searchBarHeight - (int)CommonUtilities.convertDpToPixel(20, this);

        AnimUtils.translate(layoutPredict,0,height);
        mPredictFragment.setGoogleApiClient(mGoogleApiClient);
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
    }


    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }



    private void showCurrentLocationToMap(double latitude, double longitude){
        Position lFrom = new Position(new LatLng(latitude,longitude));
        listStopPoint.add(lFrom);
        Marker markerTo = mMap.addMarker(new MarkerOptions().position(lFrom.getLatLng()).title("Vị trí của bạn"));
     /*   mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));*/

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(lFrom.getLatLng())             // Sets the center of the map to current location
                .zoom(16)                   // Sets the zoom
                .tilt(45)                   // Sets the tilt of the camera to 0 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));


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
        try {
            for (int i= 0; i< listStopPoint.size()-1; i++) {
                mFrom = listStopPoint.get(i);
                Position mTo = listStopPoint.get(i+1);
                if (i == listStopPoint.size()-2)
                    mEnd = listStopPoint.get(i+1);
                new DirectionFinder(this, mFrom.getLatLng(), mTo.getLatLng()).execute();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
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
                            }
                        });
                    } else {
                        showCurrentLocationToMap(gpsTracker.getLatitude(),gpsTracker.getLongitude());
                    }
                }
                break;
        }
    }

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
                CommonUtilities.settingRequestTurnOnLocation(PassengerSelectActionActivity.this);
            } else
                showCurrentLocationToMap(gpsTracker.getLatitude(), gpsTracker.getLongitude());
        }
    }

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
            listStopPoint.add(listStopPoint.size()-1,location);
        }
        changeUIWhenChangedDirecition();
        sendRequestFindDirection();
    }

    @Override
    public void onChangeLocation(Position location, int position) {
        changeUIWhenChangedDirecition();
        String sLocation = location.getPrimaryText() +", "+location.getSecondText();
        layoutDirection.updateLocation(sLocation,position);
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
        }

    }

    @Override
    public void onDirectionFinderStart() {
        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);
            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
        mMap.addMarker(new MarkerOptions()
                //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                .title(mFrom.getPrimaryText())
                .position(mFrom.getLatLng()));
        if (mEnd != null){
            mMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(mEnd.getPrimaryText())
                    .position(mEnd.getLatLng()));
        }
    }
}
