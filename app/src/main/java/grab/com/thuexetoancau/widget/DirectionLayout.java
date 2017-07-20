package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.DirectionAdapter;
import grab.com.thuexetoancau.listener.OnStartDragListener;
import grab.com.thuexetoancau.listener.SimpleItemTouchHelperCallback;

/**
 * Created by DatNT on 7/18/2017.
 */

public class DirectionLayout extends LinearLayout implements View.OnClickListener, DirectionAdapter.ItemClickListener, OnStartDragListener {
    private Context mContext;
    public ImageView imgBack;
    private DirectionCallback mCallback;
    private LinearLayout layoutOneWay, layoutRoundTrip;
    private TextView txtOneWay, txtRoundTrip;
    private ImageView imgRoundTrip, imgOneWay;
    private RecyclerView listDirection;
    private ArrayList<String> routes;
    private DirectionAdapter adapter;
    private int listHeight;
    private ItemTouchHelper mItemTouchHelper;
    private SimpleItemTouchHelperCallback callback;

    public DirectionLayout(Context context, String endLocation) {
        super(context);
        this.mContext = context;
        routes = new ArrayList<>();
        routes.add("Vị trí của bạn");
        routes.add(endLocation);
        initLayout();
    }

    public DirectionLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        routes = new ArrayList<>();
        routes.add("Vị trí của bạn");
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.control_direction_layout, this, true);
        imgBack = (ImageView) view.findViewById(R.id.img_back);
        imgBack.setOnClickListener(this);
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
            routes.add(routes.size() - 1, location);
            adapter.notifyItemInserted(routes.size() - 1);
            adapter.notifyItemRangeChanged(position,routes.size());
            LinearLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, listHeight);
            listDirection.setLayoutParams(params);
            if (routes.size() > 2)
                callback.setItemSwipe(true);
        }else {
            routes.set(position, location);
            adapter.notifyItemChanged(position);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_back:
                if (mCallback != null)
                    mCallback.onBackDirectionClicked();
                break;
            case R.id.layout_one_way:
                layoutOneWay.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_shape));
                txtOneWay.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                imgOneWay.setColorFilter(ContextCompat.getColor(mContext,R.color.blue));
                layoutRoundTrip.setBackground(null);
                txtRoundTrip.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                imgRoundTrip.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
                break;
            case R.id.layout_round_trip:
                layoutRoundTrip.setBackground(ContextCompat.getDrawable(mContext,R.drawable.direction_type_shape));
                txtRoundTrip.setTextColor(ContextCompat.getColor(mContext,R.color.blue));
                imgRoundTrip.setColorFilter(ContextCompat.getColor(mContext,R.color.blue));
                layoutOneWay.setBackground(null);
                txtOneWay.setTextColor(ContextCompat.getColor(mContext,R.color.white));
                imgOneWay.setColorFilter(ContextCompat.getColor(mContext,R.color.white));
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
    }

    @Override
    public void onChangeLocation(int postion) {
        if (mCallback != null)
            mCallback.onDirectionTextClicked(postion);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public interface DirectionCallback {
        void onBackDirectionClicked();
        void onDirectionTextClicked(int type);
        void onNewStopPoint();
        void onSearchViewSearching();
        void onChangeTextSearch(CharSequence s, AutoCompleteTextView edtSearch);
        void getLayoutSearchHeight(int height);
    }
}
