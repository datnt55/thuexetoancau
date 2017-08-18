package grab.com.thuexetoancau.activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.FavoriteTripAdapter;
import grab.com.thuexetoancau.adapter.ScheduleTripAdapter;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.model.User;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.Defines;

public class ScheduleTripActivity extends AppCompatActivity {
    private RecyclerView listFavorite;
    private ScheduleTripAdapter adapter;
    private Context mContext;
    private int userId;
    private Toolbar toolbar;
    private int bookingId, tripType;
    private User driver;
    private  ApiUtilities mApi;

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
        listFavorite = (RecyclerView) findViewById(R.id.list_schedule);
        listFavorite.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listFavorite.setLayoutManager(layoutManager);

        if (getIntent().hasExtra(Defines.BUNDLE_FOUND_DRIVER)) {
            mApi.checkTokenLogin(new ApiUtilities.ResponseLoginListener() {
                @Override
                public void onSuccess(Trip trip, User user) {
                    mApi.getScheduleTrip(user.getId(), new ApiUtilities.ResponseTripListener() {
                        @Override
                        public void onSuccess(ArrayList<Trip> arrayTrip) {
                            adapter = new ScheduleTripAdapter(mContext, arrayTrip);
                            listFavorite.setAdapter(adapter);
                        }
                    });
                }
            });
        }else {
            mApi.getScheduleTrip(userId, new ApiUtilities.ResponseTripListener() {
                @Override
                public void onSuccess(ArrayList<Trip> arrayTrip) {
                    adapter = new ScheduleTripAdapter(mContext, arrayTrip);
                    listFavorite.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
