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
import grab.com.thuexetoancau.listener.ChangeTripInfo;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.utilities.Defines;

/**
 * Created by DatNT on 7/20/2017.
 */

public class TransportationLayout extends LinearLayout implements View.OnClickListener,ChangeTripInfo{
    private  Context mContext;
    private RecyclerView listTrans;
    private TransportationAdapter adapter;
    private Button btnBook;
    private OnTransportationListener listener;
    private ArrayList<Car> transports;
    private int totalDistance;
    private int tripType = 1;
    private Car carSelect;
    public TransportationLayout(PassengerSelectActionActivity activity, ArrayList<Car> carPrice) {
        super(activity);
        this.mContext = activity;
        this.listener = activity;
        this.transports = carPrice;
        transports.get(0).setSelected(true);
        carSelect = transports.get(0);
        activity.setOnChangeTripListener(this);
        initLayout();
    }

    public TransportationLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
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
        setCarPrice(totalDistance);
        adapter = new TransportationAdapter(mContext, transports);
        listTrans.setAdapter(adapter);

        adapter.setOnClickListener(new TransportationAdapter.OnItemClickListener() {
            @Override
            public void onClicked(Car car) {
                carSelect = car;
                if (listener != null)
                    listener.onSelectVehicle(carSelect);
            }
        });
    }

    private void setCarPrice(int distance) {
        for (Car car : transports)
            if (totalDistance < Defines.MAX_DISTANCE)
                car.setTotalPrice(car.getPrice11way()*distance/1000);
            else if (tripType == 1)
                car.setTotalPrice(car.getPrice01way()*distance/1000);
            else {
                car.setTotalPrice((car.getPrice02way()*distance +car.getPrice02way() * distance)/ 1000);
            }
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

    @Override
    public void onChangeDistance(int distance) {
        this.totalDistance = distance;
        setCarPrice(distance);
        adapter.notifyDataSetChanged();
        if (listener != null)
            listener.onSelectVehicle(carSelect);
    }

    @Override
    public void onChangeTrip(int tripType) {
        this.tripType = tripType;
        setCarPrice(totalDistance);
        adapter.notifyDataSetChanged();
    }

    public interface OnTransportationListener{
        void onBookingClicked();
        void onSelectVehicle(Car car);
    }
}
