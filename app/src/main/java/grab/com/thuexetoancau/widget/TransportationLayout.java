package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.PassengerSelectActionActivity;
import grab.com.thuexetoancau.adapter.TransportationAdapter;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.utilities.Defines;

/**
 * Created by DatNT on 7/20/2017.
 */

public class TransportationLayout extends LinearLayout implements View.OnClickListener{
    private  Context mContext;
    private RecyclerView listTrans;
    private TransportationAdapter adapter;
    private Button btnBook;
    private OnTransportationListener listener;
    private ArrayList<Car> transports;
    private int totalDistance;
    private int tripType;

    public TransportationLayout(PassengerSelectActionActivity activity, ArrayList<Car> carPrice, int totalDistance, int tripType ) {
        super(activity);
        this.mContext = activity;
        this.listener = activity;
        this.transports = carPrice;
        this.tripType = tripType;
        this.totalDistance = totalDistance;
        initLayout();
    }

    public TransportationLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.layout_transportation, this, true);
        listTrans = (RecyclerView) view.findViewById(R.id.list_transport);
        btnBook = (Button) view.findViewById(R.id.btn_booking);
        btnBook.setOnClickListener(this);
        listTrans.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        listTrans.setLayoutManager(layoutManager);
        setCarPrice();
        adapter = new TransportationAdapter(mContext, transports);
        listTrans.setAdapter(adapter);
        adapter.setOnClickListener(new TransportationAdapter.OnItemClickListener() {
            @Override
            public void onClicked(Car car) {
                if (listener != null)
                    listener.onSelectVehicle(car);
            }
        });
    }

    private void setCarPrice() {
        for (Car car : transports)
            if (totalDistance < Defines.MAX_DISTANCE)
                car.setTotalPrice(car.getPrice11way()*totalDistance);
            else if (tripType == 1)
                car.setTotalPrice(car.getPrice01way()*totalDistance);
            else
                car.setTotalPrice(car.getPrice02way()*totalDistance);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_booking:
                if (listener != null)
                    listener.onBookingClicked();
                break;
        }
    }

    public interface OnTransportationListener{
        void onBookingClicked();
        void onSelectVehicle(Car car);
    }
}
