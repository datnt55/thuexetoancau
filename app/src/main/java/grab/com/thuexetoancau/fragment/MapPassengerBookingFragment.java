package grab.com.thuexetoancau.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.ListPassengerBookingActivity;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;

/**
 * Created by DatNT on 7/15/2017.
 */

public class MapPassengerBookingFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Context mContext;
    private double longitude, latitude;
    private ProgressDialog dialog;
    private LatLng curLatLng;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private GPSTracker mLocation;
    private ImageView imgRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_passenger_booking, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);
        initComponents(view);
        return view;
    }

    private void initComponents(View view){
        imgRefresh  =   (ImageView) view.findViewById(R.id.img_refresh);
        imgRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeAllMarker();
                getCarAround();
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mContext = getActivity();
        ((ListPassengerBookingActivity) getActivity()).updateMap(new ListPassengerBookingActivity.MapRefreshListener() {
            @Override
            public void onLocationSuccess() {
                getCarAround();
            }

            @Override
            public void onLocationFailure() {

            }

            @Override
            public void onOffline() {
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
    }

    // Request get car around to server
    private void getCarAround() {
        mLocation = new GPSTracker(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage(getActivity().getResources().getString(R.string.getting_location));
        dialog.show();
        longitude = mLocation.getLongitude();
        latitude = mLocation.getLatitude();
        curLatLng = new LatLng(latitude, longitude);
        Marker markerTo = mMap.addMarker(new MarkerOptions().position(curLatLng).title("Vị trí của bạn"));
        markerList.add(markerTo);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));
        dialog.setMessage(getActivity().getResources().getString(R.string.loading_data));
        RequestParams params;
        params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_CAR_AROUND, params, new AsyncHttpResponseHandler() {

            @Override
            public void onStart() {

            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                // called when response HTTP status is "200 OK"
                Log.i("JSON", new String(responseBody));
                try {
                    JSONArray data = new JSONArray(new String(responseBody));
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject jsonobject = data.getJSONObject(i);
                        parseJsonResult(jsonobject);
                    }
                    dialog.dismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialog.dismiss();
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }

            @Override
            public void onRetry(int retryNo) {
                // called when request is retried
                //Toast.makeText(getContext(), getResources().getString(R.string.check_network), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }
    private void parseJsonResult(JSONObject jsonobject) {
        try {
            double lon         = jsonobject.getDouble("lon");
            double lat         = jsonobject.getDouble("lat");
            double distance    = jsonobject.getDouble("D");
            DecimalFormat df = new DecimalFormat("#.#");
            String gap;
            if ((int) distance == 0) {
                String meter = df.format(distance * 1000);
                gap = mContext.getResources().getString(R.string.distance_meter, meter);
            }else {
                String kilometer = df.format(distance);
                gap = mContext.getResources().getString(R.string.distance_kilo_meter, kilometer);
            }
            LatLng aroundLatLon = new LatLng(lat, lon);
            Marker marker = mMap.addMarker(new MarkerOptions().position(aroundLatLon).title(mContext.getResources().getString(R.string.distance_car,gap)));
            marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.car_icon));
            markerList.add(marker);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void removeAllMarker(){
        for (Marker marker : markerList)
            marker.remove();
    }
}
