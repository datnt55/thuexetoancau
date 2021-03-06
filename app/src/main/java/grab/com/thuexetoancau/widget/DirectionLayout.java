package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.DirectionAdapter;
import grab.com.thuexetoancau.listener.OnStartDragListener;
import grab.com.thuexetoancau.listener.SimpleItemTouchHelperCallback;
import grab.com.thuexetoancau.utilities.GPSTracker;

/**
 * Created by DatNT on 7/18/2017.
 */

public class DirectionLayout extends LinearLayout implements View.OnClickListener, DirectionAdapter.ItemClickListener, OnStartDragListener {
    private Context mContext;
    private DirectionCallback mCallback;
    private LinearLayout layoutOneWay, layoutRoundTrip;
    private TextView txtOneWay, txtRoundTrip;
    private ImageView imgRoundTrip, imgOneWay, imgMenu;
    private RecyclerView listDirection;
    private ArrayList<String> routes;
    private DirectionAdapter adapter;
    private int listHeight;
    private ItemTouchHelper mItemTouchHelper;
    private SimpleItemTouchHelperCallback callback;

    public DirectionLayout(Context context) {
        super(context);
        this.mContext = context;
        routes = new ArrayList<>();
        GPSTracker gpsTracker = new GPSTracker(mContext);
        routes.add(getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        routes.add("Bạn muốn đi đâu");
        initLayout();
    }


    public DirectionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        routes = new ArrayList<>();
        GPSTracker gpsTracker = new GPSTracker(mContext);
        routes.add(getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        routes.add("Bạn muốn đi đâu?");
        initLayout();
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                int index = addresses.get(0).getMaxAddressLineIndex();
                if (index == 0)
                    return address.getAddressLine(0);
                for (int i = 0; i < index; i++)
                    if (address.getAddressLine(i) != null) {
                        result.append(address.getAddressLine(i));
                        if (i < index - 1)
                            result.append(", ");
                    }
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }
        if (result.toString().equals(""))
            return "Vị trí của bạn";
        return result.toString();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.control_direction_layout, this, true);
        imgMenu = (ImageView) view.findViewById(R.id.img_menu);
        imgMenu.setOnClickListener(this);
        layoutOneWay = (LinearLayout) view.findViewById(R.id.layout_one_way);
        layoutRoundTrip = (LinearLayout) view.findViewById(R.id.layout_round_trip);
        txtOneWay = (TextView) view.findViewById(R.id.txt_one_way);
        txtRoundTrip = (TextView) view.findViewById(R.id.txt_round_trip);
        imgOneWay = (ImageView) view.findViewById(R.id.img_one_way);
        imgRoundTrip = (ImageView) view.findViewById(R.id.img_round_trip);
        layoutOneWay.setOnClickListener(this);
        layoutRoundTrip.setOnClickListener(this);
        listDirection = (RecyclerView) view.findViewById(R.id.list_direction);
        listDirection.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        listDirection.setLayoutManager(llm);
        adapter = new DirectionAdapter(mContext, routes, listDirection);
        listDirection.setAdapter(adapter);
        adapter.setOnItemClickListener(this);
        listHeight = measureView(listDirection)/2*3;
        adapter.setStandardHeight(listHeight);
        callback = new SimpleItemTouchHelperCallback(adapter);
        adapter.setItemTouchCallBack(callback);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(listDirection);
        // If less or equal 2 direction, disable swipe
        if (routes.size() > 2)
            callback.setItemSwipe(true);
        else
            callback.setItemSwipe(false);
    }

    public void setOnCallBackDirection(DirectionCallback callback){
        this.mCallback = callback;
    }

    public void updateLocation(String location, int position){
        if (position == -1) {
            routes.add(routes.size(), location);
            adapter.notifyItemInserted(routes.size());
            adapter.notifyItemRangeChanged(position,routes.size());
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listHeight);
            listDirection.setLayoutParams(params);
            if (routes.size() > 2)
                callback.setItemSwipe(true);
        }else {
            if (routes.size() == 2 && routes.get(1).equals("Bạn muốn đi đâu?")) {
                routes.set(position, location);
                adapter.notifyDataSetChanged();
            }else {
                routes.set(position, location);
                adapter.notifyItemChanged(position);
            }
        }
        listDirection.scrollToPosition(routes.size()-1);
    }

    private void checkSizeOfRecyclerView(RecyclerView list) {
        int curHeight = measureView(list);
        if (curHeight > listHeight){
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listHeight);
            list.setLayoutParams(params);
        }
        if (curHeight < listHeight){
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, curHeight);
            list.setLayoutParams(params);
        }
    }

    private int measureView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    public void resetListStopPoint(){
        routes.clear();
        GPSTracker gpsTracker = new GPSTracker(mContext);
        routes.add(getAddress(gpsTracker.getLatitude(), gpsTracker.getLongitude()));
        routes.add("Bạn muốn đi đâu?");
        adapter.notifyDataSetChanged();
        checkSizeOfRecyclerView(listDirection);
        if (routes.size()<= 2){
            callback.setItemSwipe(false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_menu:
                if (mCallback != null)
                    mCallback.onMenuClicked();
                break;
            case R.id.layout_one_way:
                layoutOneWay.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_select_shape));
                txtOneWay.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                imgOneWay.setColorFilter(ContextCompat.getColor(mContext,R.color.blue));
                layoutRoundTrip.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_no_select_shape));
                txtRoundTrip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                imgRoundTrip.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
                if (mCallback != null)
                    mCallback.onSetTripType(1);
                break;
            case R.id.layout_round_trip:
                layoutRoundTrip.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_select_shape));
                txtRoundTrip.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                imgRoundTrip.setColorFilter(ContextCompat.getColor(mContext,R.color.blue));
                layoutOneWay.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_no_select_shape));
                txtOneWay.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                imgOneWay.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
                if (mCallback != null)
                    mCallback.onSetTripType(0);
                break;
        }
    }

    @Override
    public void onNewStopPoint() {
        if (mCallback != null)
            mCallback.onNewStopPoint();
    }

    @Override
    public void onRemoveStopPoint(int position) {
        routes.remove(position);
        adapter.notifyDataSetChanged();
        checkSizeOfRecyclerView(listDirection);
        if (routes.size()<= 2){
            callback.setItemSwipe(false);
        }
        if (mCallback != null)
            mCallback.onRemoveStopPoint(position);
    }

    @Override
    public void onChangeLocation(int postion) {
        if (mCallback != null)
            mCallback.onDirectionTextClicked(postion);
    }

    @Override
    public void onSwapLocation(int fromPosition, int position) {
        if (mCallback != null)
            mCallback.onSwapLocation(fromPosition,position);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public interface DirectionCallback {
        void onMenuClicked();
        void onDirectionTextClicked(int position);
        void onNewStopPoint();
        void onRemoveStopPoint(int position);
        void onSwapLocation(int fromPosition, int position);
        void onSetTripType(int type);
    }
}
