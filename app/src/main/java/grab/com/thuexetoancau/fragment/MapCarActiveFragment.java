package grab.com.thuexetoancau.fragment;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import grab.com.thuexetoancau.DirectionFinder.DirectionFinder;
import grab.com.thuexetoancau.DirectionFinder.DirectionFinderListener;
import grab.com.thuexetoancau.DirectionFinder.Route;
import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.AuctionBookingActivity;
import grab.com.thuexetoancau.model.Position;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;

/**
 * Created by DatNT on 10/7/2016.
 */

public class MapCarActiveFragment extends Fragment implements OnMapReadyCallback,DirectionFinderListener {
    private GoogleMap mMap;
    private MapView mMapView;
    private double longitude, latitude;
    private ProgressDialog dialog;
    private LatLng latLngFrom, latLngTo;
    private ArrayList<Marker> markerList = new ArrayList<>();
    private List<Marker> originMarkers = new ArrayList<>();
    private List<Marker> destinationMarkers = new ArrayList<>();
    private List<Polyline> polylinePaths = new ArrayList<>();
    private GPSTracker mLocation;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_active_car, container, false);
        SupportMapFragment map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        map.getMapAsync(this);

        return view;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((AuctionBookingActivity) getActivity()).updateMap(new AuctionBookingActivity.OnDataMap() {
            @Override
            public void OnDataMap(String location, LatLng latLng) {
                if(location.equals("from"))
                    latLngFrom = latLng;
                else
                    latLngTo = latLng;

                removeAllMarker();
                markLocationToMap();

            }

            @Override
            public void OnDataLocation(ProgressDialog dialog) {
                dialog.dismiss();
                getCarAround();
            }
        });
    }
    private void getCarAround() {
        mLocation = new GPSTracker(getActivity());
        dialog = new ProgressDialog(getActivity());
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.setMessage("Đang lấy vị trí...");
        dialog.show();
        longitude = mLocation.getLongitude();
        latitude = mLocation.getLatitude();
        LatLng curLatLng = new LatLng(latitude, longitude);
        Marker markerTo = mMap.addMarker(new MarkerOptions().position(curLatLng).title("Vị trí của bạn"));
        markerList.add(markerTo);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curLatLng, 16));
        dialog.setMessage("Đang tải dữ liệu");
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();
        //requestToGetListVehicle();
        markLocationToMap();

    }
    private void parseJsonResult(JSONObject jsonobject) {
        try {
            double lon         = jsonobject.getDouble("lon");
            double lat         = jsonobject.getDouble("lat");
            double distance    = jsonobject.getDouble("D");
            DecimalFormat df = new DecimalFormat("#.#");
            String gap = "";
            if ((int) distance == 0)
                gap = df.format(distance*1000) + " m";
            else
                gap = df.format(distance) + " km";
            LatLng aroundLatLon = new LatLng(lat, lon);
            Marker marker = mMap.addMarker(new MarkerOptions().position(aroundLatLon).title("Xe cách bạn "+gap));
            //marker.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.car_icon));
            markerList.add(marker);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    private void markLocationToMap() {
        if(latLngFrom !=null) {
            Marker markerFrom = mMap.addMarker(new MarkerOptions().position(latLngFrom).title("Điểm đi"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFrom, 16));
            originMarkers.add(markerFrom);
        }else {
            if (originMarkers != null)
                for (Marker marker : originMarkers)
                    marker.remove();

            if (polylinePaths != null)
                for (Polyline polyline : polylinePaths)
                    polyline.remove();


        }
        if(latLngTo !=null) {
            Marker markerTo = mMap.addMarker(new MarkerOptions().position(latLngTo).title("Điểm đến"));
            destinationMarkers.add(markerTo);
            if (latLngFrom == null)
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngTo, 16));
        }else{
            if (destinationMarkers != null)
                for (Marker marker : destinationMarkers)
                    marker.remove();



            if (polylinePaths != null)
                for (Polyline polyline : polylinePaths)
                    polyline.remove();


        }
        if(latLngTo !=null && latLngFrom != null)
            sendRequest();
    }

    private void sendRequest() {
        try {
            ArrayList<Position> positions = new ArrayList<>();
            positions.add(new Position("",latLngFrom));
            positions.add(new Position("",latLngTo));
            new DirectionFinder(this, positions).execute();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void removeAllMarker(){
        for (Marker marker : markerList)
            marker.remove();
    }

    @Override
    public void onDirectionFinderStart() {
        if (originMarkers != null) {
            for (Marker marker : originMarkers) {
                marker.remove();
            }
        }

        if (destinationMarkers != null) {
            for (Marker marker : destinationMarkers) {
                marker.remove();
            }
        }

        if (polylinePaths != null) {
            for (Polyline polyline : polylinePaths) {
                polyline.remove();
            }
        }
    }

    @Override
    public void onDirectionFinderSuccess(List<Route> routes) {
        if (routes.size()==0){
            originMarkers = new ArrayList<>();
            destinationMarkers = new ArrayList<>();
            Marker markerFrom = mMap.addMarker(new MarkerOptions().position(latLngFrom).title("Điểm đi"));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFrom, 16));
            originMarkers.add(markerFrom);

            Marker markerTo = mMap.addMarker(new MarkerOptions().position(latLngTo).title("Điểm đến"));
            destinationMarkers.add(markerTo);

            return;
        }
        polylinePaths = new ArrayList<>();
        originMarkers = new ArrayList<>();
        destinationMarkers = new ArrayList<>();

        for (Route route : routes) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(route.startLocation, 16));
            //((TextView) findViewById(R.id.tvDuration)).setText(route.duration.text);
            //((TextView) findViewById(R.id.tvDistance)).setText(route.distance.text);

            originMarkers.add(mMap.addMarker(new MarkerOptions()
                    .title(route.startAddress)
                    .position(route.startLocation)));
            destinationMarkers.add(mMap.addMarker(new MarkerOptions()
                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green))
                    .title(route.endAddress)
                    .position(route.endLocation)));

            PolylineOptions polylineOptions = new PolylineOptions().
                    geodesic(true).
                    color(Color.BLUE).
                    width(10);

            for (int i = 0; i < route.points.size(); i++)
                polylineOptions.add(route.points.get(i));

            polylinePaths.add(mMap.addPolyline(polylineOptions));
        }
    }
}
