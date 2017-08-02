package grab.com.thuexetoancau.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.PassengerSelectActionActivity;
import grab.com.thuexetoancau.adapter.LastSearchAdapter;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.Global;
import grab.com.thuexetoancau.widget.DividerItemDecoration;

public class LastSearchFragment extends Fragment {
    private TextView txtLastSearch;
    private RecyclerView listLastSearch;
    private ArrayList<Position> arrayLastSearch;
    private LastSearchAdapter adapter;
    private GoogleApiClient mGoogleApiClient;
    private int directionPosition = -1;
    private OnAddNewDirection listener;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (getArguments().containsKey(Defines.POSITION_POINT))
            directionPosition = getArguments().getInt(Defines.POSITION_POINT);
        View view = inflater.inflate(R.layout.fragment_last_search, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View view) {
        txtLastSearch = (TextView) view.findViewById(R.id.txt_last_search);
        listLastSearch = (RecyclerView) view.findViewById(R.id.list_last_search);
        // set cardview
        listLastSearch.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        listLastSearch.setLayoutManager(llm);
        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayout.VERTICAL);
        listLastSearch.addItemDecoration(mDividerItemDecoration);
    }

    public void setAdapter(LastSearchAdapter mAdapter){
        this.adapter = mAdapter;
        listLastSearch.setAdapter(adapter);
        adapter.setOnClickListener(new LastSearchAdapter.onClickListener() {
            @Override
            public void onItemClick(final Position location) {
                final String placeId = String.valueOf(location.getPlaceId());
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
                placeResult.setResultCallback(new ResultCallback<PlaceBuffer>() {
                    @Override
                    public void onResult(PlaceBuffer places) {
                        if(places.getCount()==1){
                            Toast.makeText(getActivity(),String.valueOf(places.get(0).getLatLng()), Toast.LENGTH_SHORT).show();
                          /*  PassengerSelectActionActivity activity = (PassengerSelectActionActivity) getActivity();
                            activity.goToBookingCar(location,directionPosition);*/
                          location.setLatLng(places.get(0).getLatLng());
                          if (directionPosition == -1) {
                              if (listener != null)
                                  listener.onNewDirection(location);
                          }else
                          if (listener != null)
                              listener.onChangeLocation(location, directionPosition);
                        }
                    }
                });
            }
        });
    }

    public void setCharacter(String constraint){
        if (constraint.equals(""))
            txtLastSearch.setText(R.string.last_search);
        else
            txtLastSearch.setText(R.string.recommend_search);
    }

    public void setGoogleApiClient(GoogleApiClient mGoogleApiClient){
        this.mGoogleApiClient = mGoogleApiClient;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        listener = (PassengerSelectActionActivity) activity;
    }

    public interface OnAddNewDirection {
        void onNewDirection(Position location);
        void onChangeLocation(Position location,int directionType);
    }
}
