package grab.com.thuexetoancau.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.ListPassengerBookingActivity;
import grab.com.thuexetoancau.adapter.PassengerCarAdapter;
import grab.com.thuexetoancau.adapter.PlaceArrayAdapter;
import grab.com.thuexetoancau.model.Booking;
import grab.com.thuexetoancau.utilities.BaseService;
import grab.com.thuexetoancau.utilities.Defines;
import grab.com.thuexetoancau.utilities.GPSTracker;

/**
 * Created by DatNT on 12/6/2016.
 */

public class ListPassengerBookingFragment extends Fragment {
    private RecyclerView vehicleView;
    private ArrayList<Booking> vehicles;
    private TextView txtNoResult;
    private double longitude, latitude;
    private PassengerCarAdapter adapter;
    private SwipeRefreshLayout swipeToRefresh;
    private ProgressDialog dialog;
    private GPSTracker mLocation;
    private FloatingActionButton btnFilter;
    private CheckLocationListener mCallback;
    private GoogleApiClient mGoogleApiClient;
    private PlaceArrayAdapter mPlaceArrayFromAdapter , mPlaceToArrayAdapter;
    private AutoCompleteTextView autoPlaceFrom, autoPlaceTo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_get_passenger_booking, container, false);
        vehicleView                 =   (RecyclerView)          view.findViewById(R.id.vehicle_view);
        txtNoResult                 =   (TextView)              view.findViewById(R.id.txt_no_result);
        swipeToRefresh              =   (SwipeRefreshLayout)    view.findViewById(R.id.swipe_view);
        btnFilter                   =   (FloatingActionButton)  view.findViewById(R.id.btn_filter);

        ((ListPassengerBookingActivity) getActivity()).updateRefresh(new ListPassengerBookingActivity.FormRefreshListener() {
            @Override
            public void onLocationSuccess(ProgressDialog mdialog) {
                dialog = mdialog;
                getBooking("","");
            }

            @Override
            public void onLocationFailure() {
                showOffline("Bạn phải bật chức năng định vị để dùng chức năng này");

            }

            @Override
            public void onOffline() {
                showOffline("Không có kết nối mạng");
            }
        });
        ((ListPassengerBookingActivity) getActivity()).updateApi(new ListPassengerBookingActivity.OnConnected() {
            @Override
            public void onConnected(GoogleApiClient googleApiClient, PlaceArrayAdapter placeFrom, PlaceArrayAdapter placeTo) {
                mPlaceArrayFromAdapter = placeFrom;
                mPlaceToArrayAdapter = placeTo;
                mGoogleApiClient = googleApiClient;
                initComponents();
            }
        });
        return view;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCallback!= null)
            mCallback.onChecking();
    }
    private void initComponents() {

        swipeToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mCallback!= null)
                    mCallback.onChecking();
            }
        });
        // set cardview
        vehicleView.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        vehicleView.setLayoutManager(llm);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSheetFilter();
            }
        });
    }

    private void showSheetFilter() {
        final BottomSheetDialog mBottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialog);
        View sheetView = getActivity().getLayoutInflater().inflate(R.layout.filter_passenger_driver, null);
        mBottomSheetDialog.setContentView(sheetView);

        BottomSheetBehavior behavior = BottomSheetBehavior.from((View) sheetView.getParent());
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        autoPlaceFrom = (AutoCompleteTextView) sheetView.findViewById(R.id.auto_place_from);
        autoPlaceFrom.setThreshold(1);
        autoPlaceFrom.setOnItemClickListener(mAutocompleteFromClickListener);
        autoPlaceFrom.setAdapter(mPlaceArrayFromAdapter);

        autoPlaceTo = (AutoCompleteTextView) sheetView.findViewById(R.id.auto_place_to);
        autoPlaceTo.setThreshold(1);
        autoPlaceTo.setOnItemClickListener(mAutocompleteToClickListener);
        autoPlaceTo.setAdapter(mPlaceToArrayAdapter);

        final ImageView imgFrom         = (ImageView) sheetView.findViewById(R.id.img_from);
        final ImageView imgTo           = (ImageView) sheetView.findViewById(R.id.img_to);


        imgFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlaceFrom.setText("");
                imgFrom.setVisibility(View.GONE);
                autoPlaceFrom.setFocusable(true);
                autoPlaceFrom.setFocusableInTouchMode(true);
            }
        });
        imgTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                autoPlaceTo.setText("");
                imgTo.setVisibility(View.GONE);

                autoPlaceTo.setFocusable(true);
                autoPlaceTo.setFocusableInTouchMode(true);
            }
        });
        autoPlaceFrom.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 >0) {
                    imgFrom.setVisibility(View.VISIBLE);
                }else
                    imgFrom.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        autoPlaceTo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (i2 >0) {
                    imgTo.setVisibility(View.VISIBLE);
                }else
                    imgTo.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Button btnConfirm      = (Button)          sheetView.findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBooking(autoPlaceFrom.getText().toString(), autoPlaceTo.getText().toString());
                mBottomSheetDialog.dismiss();
            }
        });
        mBottomSheetDialog.show();
    }
    private AdapterView.OnItemClickListener mAutocompleteFromClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //hideSoftKeyboard(getActivity());
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceArrayFromAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsFromCallback);
            //Log.i(LOG_TAG, "Fetching details for ID: " + item.placeId);
        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsFromCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                //Log.e(LOG_TAG, "Place query did not complete. Error: " + places.getStatus().toString());
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            autoPlaceFrom.setSelection(0);

            autoPlaceFrom.setFocusable(false);
            autoPlaceFrom.setFocusableInTouchMode(false);

        }
    };
    private AdapterView.OnItemClickListener mAutocompleteToClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            //hideSoftKeyboard(getActivity());
            final PlaceArrayAdapter.PlaceAutocomplete item = mPlaceToArrayAdapter.getItem(position);
            final String placeId = String.valueOf(item.placeId);
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsToCallback);

        }
    };
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsToCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                return;
            }
            // Selecting the first object buffer.
            final Place place = places.get(0);
            CharSequence attributions = places.getAttributions();
            autoPlaceTo.setSelection(0);
            autoPlaceTo.setFocusable(false);
            autoPlaceTo.setFocusableInTouchMode(false);

        }
    };
    public static void hideSoftKeyboard(Activity activity) {

        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText())
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }
    private void getBooking(final String llFrom, final String llTo) {
        mLocation = new GPSTracker(getActivity());
        longitude = mLocation.getLongitude();
        latitude = mLocation.getLatitude();
        vehicles = new ArrayList<>();
        if (!dialog.isShowing()){
            dialog = new ProgressDialog(getActivity());
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.setMessage("Đang tải dữ liệu");
            dialog.show();
        }
        dialog.setMessage("Đang tải dữ liệu");

        RequestParams params;
        params = new RequestParams();
        params.put("lat", latitude);
        params.put("lon", longitude);
        params.put("car_from", llFrom);
        params.put("car_to", llTo);
        params.put("car_hire_type", "Chiều về,Đi chung");
        params.put("order", 1);
        Log.e("TAG",params.toString());
        BaseService.getHttpClient().post(Defines.URL_GET_BOOKING_CUSTOMER, params, new AsyncHttpResponseHandler() {

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
                    if(vehicles.size()>0) {
                        adapter = new PassengerCarAdapter(getActivity(), vehicles);
                        vehicleView.setAdapter(adapter);
                        adapter.setOnRequestComplete( new PassengerCarAdapter.onClickListener() {
                            @Override
                            public void onItemClick() {
                                getBooking(llFrom,llTo);
                            }
                        });
                        //swipeToRefresh.setRefreshing(false);
                        vehicleView.setVisibility(View.VISIBLE);
                        txtNoResult.setVisibility(View.GONE);
                    }else{
                        txtNoResult.setVisibility(View.VISIBLE);
                        vehicleView.setVisibility(View.GONE);
                        txtNoResult.setText("Không có xe nào cho tuyến này");
                        //swipeToRefresh.setRefreshing(false);
                    }
                    dialog.dismiss();
                    //prepareDataSliding();
                    if (swipeToRefresh.isRefreshing())
                        swipeToRefresh.setRefreshing(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                swipeToRefresh.setRefreshing(false);
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
            int id              = jsonobject.getInt("id");
            String carFrom      = jsonobject.getString("car_from");
            String carTo        = jsonobject.getString("car_to");
            String carType      = jsonobject.getString("car_type");

            String carHireType  = jsonobject.getString("car_hire_type");
            String carWhoHire   = jsonobject.getString("car_who_hire");
            String fromDate     = jsonobject.getString("from_datetime");
            String toDate       = jsonobject.getString("to_datetime");
            String dateBook     = jsonobject.getString("datebook");

            int price           = jsonobject.getInt("book_price");
            int priceMax        = jsonobject.getInt("book_price_max");
            int currentPrice = 0;
            if (!jsonobject.getString("current_price").equals("null"))
                currentPrice    = jsonobject.getInt("current_price");
            String timeReduce   = jsonobject.getString("time_to_reduce");

            double lon1         = jsonobject.getDouble("lon1");
            double lat1         = jsonobject.getDouble("lat1");
            double lon2         = jsonobject.getDouble("lon2");
            double lat2         = jsonobject.getDouble("lat2");

            double distance     = jsonobject.getDouble("D");

            Booking busInfor = new Booking(id, carFrom,carTo,carType,carHireType,carWhoHire,fromDate,toDate,dateBook,price,priceMax,currentPrice, timeReduce, new LatLng(lat1,lon1), new LatLng(lat2, lon2),distance);
            vehicles.add(busInfor);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showOffline(String text) {
        txtNoResult.setVisibility(View.VISIBLE);
        txtNoResult.setText(text);
    }
   public interface CheckLocationListener{
        void onChecking();
    }
    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);
        // Make sure that container activity implement the callback interface
        try {
            mCallback = (ListPassengerBookingFragment.CheckLocationListener)activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement DataPassListener");
        }
    }
}
