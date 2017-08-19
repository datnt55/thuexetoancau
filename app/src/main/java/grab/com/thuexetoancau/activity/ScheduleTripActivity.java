package grab.com.thuexetoancau.activity;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.ScheduleTripAdapter;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.Defines;

public class ScheduleTripActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView listSchedule;
    private ScheduleTripAdapter adapter;
    private Context mContext;
    private int userId;
    private Toolbar toolbar;
    private int bookingId, tripType;
    private User driver;
    private ApiUtilities mApi;
    private SwipeRefreshLayout swipeToRefresh;
    private RelativeLayout layoutNoTrip;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_trip);
        mContext = this;
        if (getIntent().hasExtra(Defines.BUNDLE_USER))
            userId = getIntent().getIntExtra(Defines.BUNDLE_USER, 0);

        // Check customer found driver
        if (getIntent().hasExtra(Defines.BUNDLE_FOUND_DRIVER)) {
            bookingId = getIntent().getIntExtra(Defines.BUNDLE_TRIP_ID,0);
            driver  = (User) getIntent().getSerializableExtra(Defines.BUNDLE_DRIVER);
            tripType = getIntent().getIntExtra(Defines.BUNDLE_TRIP_TYPE,0);
        }
        initComponents();
    }

    private void initComponents() {
        mApi = new ApiUtilities(this);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Lich trình chuyến đi");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        swipeToRefresh =(SwipeRefreshLayout) findViewById(R.id.swipe_view);
        layoutNoTrip = (RelativeLayout) findViewById(R.id.layout_no_trip);
        swipeToRefresh.setOnRefreshListener(this);
        listSchedule = (RecyclerView) findViewById(R.id.list_schedule);
        listSchedule.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listSchedule.setLayoutManager(layoutManager);

        if (getIntent().hasExtra(Defines.BUNDLE_FOUND_DRIVER)) {
            mApi.checkTokenLogin(new ApiUtilities.ResponseLoginListener() {
                @Override
                public void onSuccess(Trip trip, User user) {
                    userId = user.getId();
                    getScheduleTrip(userId);
                }
            });
        }else {
            getScheduleTrip(userId);
        }
    }

    private void getScheduleTrip(int userId){
        mApi.getScheduleTrip(userId, new ApiUtilities.ResponseTripListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                if (arrayTrip == null){
                    layoutNoTrip.setVisibility(View.VISIBLE);
                    listSchedule.setVisibility(View.GONE);
                }else {
                    layoutNoTrip.setVisibility(View.GONE);
                    listSchedule.setVisibility(View.VISIBLE);
                    adapter = new ScheduleTripAdapter(mContext, arrayTrip);
                    listSchedule.setAdapter(adapter);
                }
                if (swipeToRefresh.isRefreshing())
                    swipeToRefresh.setRefreshing(false);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        getScheduleTrip(userId);
    }
}
