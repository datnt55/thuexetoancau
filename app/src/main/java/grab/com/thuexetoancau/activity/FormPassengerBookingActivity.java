package grab.com.thuexetoancau.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.PlaceArrayAdapter;
import grab.com.thuexetoancau.adapter.ViewPagerAdapter;
import grab.com.thuexetoancau.fragment.BookingFormFragment;
import grab.com.thuexetoancau.fragment.MapCarActiveFragment;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;

public class FormPassengerBookingActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,BookingFormFragment.DataPassListener, BookingFormFragment.OnDataResult {
    private static final String LOG_TAG = "FormPassengerBookingActivity";
    private static final int GOOGLE_API_CLIENT_ID = 0;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter;
    private static final LatLngBounds BOUNDS_MOUNTAIN_VIEW = new LatLngBounds(new LatLng(8.412730, 102.144410), new LatLng(23.393395, 109.468975));
    private OnConnected connected;
    private OnDataMap onMap;
    private Context mContext;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.roster,
            R.drawable.maps
    };
    private GPSTracker mLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_passenger_auction);
        mContext = this;
        initComponents();
    }


    private void initComponents() {

        viewPager       =   (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Đăng chuyến đấu giá");

        tabLayout       = (TabLayout)   findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
        // init google api
        mGoogleApiClient = new GoogleApiClient.Builder(FormPassengerBookingActivity.this)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, GOOGLE_API_CLIENT_ID, this)
                .addConnectionCallbacks(this)
                .build();
        mPlaceArrayFromAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);
        mPlaceToArrayAdapter = new PlaceArrayAdapter(this,BOUNDS_MOUNTAIN_VIEW, null);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.blue_light), PorterDuff.Mode.SRC_IN);
        tabLayout.getTabAt(1).getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.grey_1), PorterDuff.Mode.SRC_IN);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.blue_light), PorterDuff.Mode.SRC_IN);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                tab.getIcon().setColorFilter(ContextCompat.getColor(mContext, R.color.grey_1), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment carList = new BookingFormFragment();


        adapter.addFrag(carList, "Danh sách");
        adapter.addFrag(new MapCarActiveFragment(), "Bản đồ");
        viewPager.setAdapter(adapter);
    }

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
                    dialog.setMessage("Đang lấy vị trí...");
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    if (onMap!= null)
                        onMap.OnDataLocation(dialog);
                }
            }
        }
    }
    public void updateApi(OnConnected listener) {
        connected = listener;
    }
    public void updateMap( OnDataMap listener) {
        onMap = listener;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Defines.REQUEST_CODE_LOCATION_PERMISSIONS) {
            requestPermission();
        }
    }
    private void settingRequestTurnOnLocation() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Thông báo");  // GPS not found
        alertDialogBuilder.setMessage("Chức năng này cần lấy vị trí hiện tại của bạn.Bạn có muốn bật định vị?")
                .setCancelable(false)
                .setPositiveButton("Tiếp tục",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                startActivityForResult(callGPSSettingIntent,1000);
                            }
                        });
        alertDialogBuilder.setNegativeButton("Không",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        android.app.AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000:
                mLocation = new GPSTracker(this);
                if (!mLocation.canGetLocation()) {

                }else {
                    final ProgressDialog dialog = new ProgressDialog(mContext);
                    dialog.setIndeterminate(true);
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setMessage("Đang lấy vị trí...");
                    dialog.show();
                    if (mLocation.getLongitude() == 0 && mLocation.getLatitude() == 0) {
                        mLocation.getLocationCoodinate(new GPSTracker.LocateListener() {
                            @Override
                            public void onLocate(double mlongitude, double mlatitude) {
                                if (onMap!= null)
                                    onMap.OnDataLocation(dialog);
                            }
                        });
                    } else {
                        if (onMap!= null)
                            onMap.OnDataLocation(dialog);
                    }
                }
                break;
        }
    }


    @Override
    public void onConnected(Bundle bundle) {
        mPlaceArrayFromAdapter.setGoogleApiClient(mGoogleApiClient);
        mPlaceToArrayAdapter.setGoogleApiClient(mGoogleApiClient);
        //Log.i(LOG_TAG, "Google Places API connected.");
        if (connected != null)
            connected.onConnected(mGoogleApiClient, mPlaceArrayFromAdapter, mPlaceToArrayAdapter);
        requestPermission();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Toast.makeText(this,  "Vui lòng kiểm tra lại kết nối", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mPlaceArrayFromAdapter.setGoogleApiClient(null);
        mPlaceToArrayAdapter.setGoogleApiClient(null);
        //Log.e(LOG_TAG, "Google Places API connection suspended.");
    }

    @Override
    public void passData(String location, LatLng data) {
        if (onMap != null)
            onMap.OnDataMap(location, data);
    }

    @Override
    public void onResult(ArrayList<String> result) {
       /* Intent intent = new Intent(mContext, BookingResultActivity.class);
        intent.putExtra("RESULT",result);
        startActivity(intent);
        finish();*/
    }

    public interface OnConnected {
        public void onConnected(GoogleApiClient googleApi, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo);
    }
    public interface OnDataMap {
        public void OnDataMap(String location, LatLng latLng);
        public void OnDataLocation(ProgressDialog dialog);
    }
}