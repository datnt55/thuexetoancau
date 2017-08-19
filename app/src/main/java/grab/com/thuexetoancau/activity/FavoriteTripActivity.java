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
import grab.com.thuexetoancau.adapter.FavoriteTripAdapter;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.Defines;

public class FavoriteTripActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener{
    private RecyclerView listFavorite;
    private FavoriteTripAdapter adapter;
    private Context mContext;
    private int userId;
    private Toolbar toolbar;
    private SwipeRefreshLayout swipeToRefresh;
    private RelativeLayout layoutNoTrip;
    private ApiUtilities mApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_trip);
        mContext = this;
        userId = getIntent().getIntExtra(Defines.BUNDLE_USER, 0);
        initComponents();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chuyến đi yêu thích");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        swipeToRefresh =(SwipeRefreshLayout) findViewById(R.id.swipe_view);
        layoutNoTrip = (RelativeLayout) findViewById(R.id.layout_no_trip);
        swipeToRefresh.setOnRefreshListener(this);
        listFavorite = (RecyclerView) findViewById(R.id.list_favorite);
        listFavorite.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listFavorite.setLayoutManager(layoutManager);
        mApi = new ApiUtilities(this);
        getFavoriteTrip();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void getFavoriteTrip(){
        mApi.getLikeTrip(userId, new ApiUtilities.ResponseTripListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                if (arrayTrip == null){
                    layoutNoTrip.setVisibility(View.VISIBLE);
                    listFavorite.setVisibility(View.GONE);
                }else {
                    layoutNoTrip.setVisibility(View.GONE);
                    listFavorite.setVisibility(View.VISIBLE);
                    adapter = new FavoriteTripAdapter(mContext, arrayTrip);
                    listFavorite.setAdapter(adapter);
                }
                if (swipeToRefresh.isRefreshing())
                    swipeToRefresh.setRefreshing(false);

            }
        });
    }
    @Override
    public void onRefresh() {
        getFavoriteTrip();
    }
}
