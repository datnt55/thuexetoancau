package grab.com.thuexetoancau.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.FavoriteTripAdapter;
import grab.com.thuexetoancau.model.Trip;
import grab.com.thuexetoancau.utilities.ApiUtilities;
import grab.com.thuexetoancau.utilities.Defines;

public class HistoryTripActivity extends AppCompatActivity {
    private RecyclerView listFavorite;
    private FavoriteTripAdapter adapter;
    private Context mContext;
    private int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_trip);
        mContext = this;
        userId = getIntent().getIntExtra(Defines.BUNDLE_USER, 0);
        initComponents();
    }

    private void initComponents() {
        listFavorite = (RecyclerView) findViewById(R.id.list_favorite);
        listFavorite.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        listFavorite.setLayoutManager(layoutManager);

        ApiUtilities mApi = new ApiUtilities(this);
        mApi.getHistoryTrip(userId, new ApiUtilities.ResponseTripListener() {
            @Override
            public void onSuccess(ArrayList<Trip> arrayTrip) {
                adapter = new FavoriteTripAdapter(mContext, arrayTrip);
                listFavorite.setAdapter(adapter);
            }
        });
    }
}
