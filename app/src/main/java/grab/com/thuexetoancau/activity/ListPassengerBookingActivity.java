package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.PlaceArrayAdapter;
import grab.com.thuexetoancau.adapter.ViewPagerAdapter;
import grab.com.thuexetoancau.fragment.ListPassengerBookingFragment;
import grab.com.thuexetoancau.fragment.MapPassengerBookingFragment;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.Global;
import grab.com.thuexetoancau.utilities.GPSTracker;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class ListPassengerBookingActivity extends AppCompatActivity implements ListPassengerBookingFragment.CheckLocationListener,GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private Context mContext;
    private FormRefreshListener onFormRefresh;         // interface pass data from activity to list vehicle fragment
    private MapRefreshListener onMapRefresh;           // interface pass data from activity to map fragment
    private GPSTracker mLocation;                    // get latitude and longitude
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.roster,
            R.drawable.maps
    };
    private GoogleApiClient mGoogleApiClient;       // google place api
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter; // Place adapter
    private OnConnected connected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_passenger_booking);
        mContext = this;
        initComponents();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initComponents() {
        viewPager       =   (ViewPager) findViewById(R.id.viewpager);
        tabLayout       = (TabLayout)   findViewById(R.id.tabs);
        setupTabView();
        setupGoogleApi();
    }

    // Init listener pass data to list vehicle fragment
    public void updateRefresh(FormRefreshListener dataRefresh){
        this.onFormRefresh = dataRefresh;
    }

    // Init listener pass data to map fragment
    public void updateMap(MapRefreshListener mapRefresh){
        this.onMapRefresh = mapRefresh;

    }

    // Init 2 fragment
    private void setupTabView() {
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
    }

    // Init google api
    private void setupGoogleApi(){
        mGoogleApiClient = new GoogleApiClient.Builder(ListPassengerBookingActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, Defines.GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this, Defines.BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this, Defines.BOUNDS_MOUNTAIN_VIEW, null);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ListPassengerBookingFragment(),getResources().getString(R.string.information));
        adapter.addFrag(new MapPassengerBookingFragment(), getResources().getString(R.string.map));
        viewPager.setAdapter(adapter);
    }

    // Init listener contain information when connect to google api
    public void updateApi(OnConnected listener) {
        connected = listener;
    }

    // Check GPS permission
    private void requestPermission(){
        if (CommonUtilities.isOnline(mContext)) {
            mLocation = new GPSTracker(this);
            if (mLocation.handlePermissionsAndGetLocation()) {
                if (!mLocation.canGetLocation()) {
                    settingRequestTurnOnLocation();
                } else {
                    ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    if (onFormRefresh != null)
                        onFormRefresh.onLocationSuccess(dialog);
                    if (onMapRefresh != null)
                        onMapRefresh.onLocationSuccess();
                }
            }
        }else {
            if (onFormRefresh != null)
                onFormRefresh.onOffline();
            if (onMapRefresh != null)
                onMapRefresh.onOffline();
        }
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
                        if (onFormRefresh != null)
                            onFormRefresh.onLocationFailure();
                        if (onMapRefresh != null)
                            onMapRefresh.onLocationFailure();
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_LOCATION_PERMISSIONS && grantResults[0] == PERMISSION_GRANTED) {
            requestPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                mLocation = new GPSTracker(this);
                if (!mLocation.canGetLocation()) {
                    if (onFormRefresh != null)
                        onFormRefresh.onLocationFailure();
                    if (onMapRefresh != null)
                        onMapRefresh.onLocationFailure();
                }else {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage(getResources().getString(R.string.getting_location));
                    dialog.show();
                    if (mLocation.getLongitude() == 0 && mLocation.getLatitude() == 0) {
                        mLocation.getLocationCoodinate(new GPSTracker.LocateListener() {
                            @Override
                            public void onLocate(double mlongitude, double mlatitude) {
                                if (onFormRefresh != null)
                                    onFormRefresh.onLocationSuccess(dialog);
                            }
                        });
                    } else {
                        if (onFormRefresh != null)
                            onFormRefresh.onLocationSuccess(dialog);
                        if (onMapRefresh != null)
                            onMapRefresh.onLocationSuccess();
                    }
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        if (connected != null)
            connected.onConnected(mGoogleApiClient, mPlaceArrayFromAdapter, mPlaceToArrayAdapter);
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayFromAdapter.setGoogleApiClient(null);
        mPlaceToArrayAdapter.setGoogleApiClient(null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(this, R.string.check_connection, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onChecking() {
        requestPermission();
    }

    public interface FormRefreshListener {
        void onLocationSuccess(ProgressDialog dialog);
        void onLocationFailure();
        void onOffline();
    }
    public interface MapRefreshListener {
        void onLocationSuccess();
        void onLocationFailure();
        void onOffline();
    }
    public interface OnConnected {
        void onConnected(GoogleApiClient googleApi, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo);
    }
}