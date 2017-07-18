package grab.com.thuexetoancau.activity;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.LastSearchAdapter;
import grab.com.thuexetoancau.fragment.LastSearchFragment;
import grab.com.thuexetoancau.model.Location;
import grab.com.thuexetoancau.utilities.AnimUtils;
import grab.com.thuexetoancau.utilities.CommonUtilities;
import grab.com.thuexetoancau.utilities.Constants;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.widget.DirectionLayout;
import grab.com.thuexetoancau.widget.SearchBarLayout;

public class PassengerSelectActionActivity extends AppCompatActivity implements
        SearchBarLayout.Callback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        DirectionLayout.DirectionCallback{
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


    private void showLastSearchFragment() {
        layoutRoot.setBackgroundColor(ContextCompat.getColor(this, R.color.bg));
        lastSearchFragment = new LastSearchFragment();
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

    public void goToBookingCar(Location location){
        layoutSearch.setTranslationY(-searchBarHeight);
        layoutSearch.setTranslationY(-searchBarHeight);
        hideLastSearchFragment();
        //layoutSearch.animate().translationY(0).setDuration(300);
        String destination = location.getPrimaryText() +", "+location.getSecondText();
        layoutDirection = new DirectionLayout(this,destination);
        layoutDirection.setOnCallBackDirection(this);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        layoutDirection.setLayoutParams(params);
        layoutRoot.addView(layoutDirection);
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
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
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
    }

    @Override
    public void onMenuButtonClicked() {

    }

    @Override
    public void onSearchViewClicked() {
        showLastSearchFragment();
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
}
