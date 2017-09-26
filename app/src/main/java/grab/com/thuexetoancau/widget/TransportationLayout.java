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
import java.util.Iterator;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.activity.PassengerSelectActionActivity;
import grab.com.thuexetoancau.adapter.TransportationAdapter;
import grab.com.thuexetoancau.listener.ChangeTripInfo;
import grab.com.thuexetoancau.model.Car;
import grab.com.thuexetoancau.utilities.CommonUtilities;
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
    private ArrayList<Car> transportsAvailable;
    private ArrayList<Car> transports;
    private int totalDistance;
    private int tripType = 1;
    private Car carSelect;
    private LinearLayoutManager layoutManager;

    public TransportationLayout(PassengerSelectActionActivity activity, ArrayList<Car> carPrice) {
        super(activity);
        this.mContext = activity;
        this.listener = activity;
        this.transports = carPrice;
        carSelect = transports.get(0);
        transports.get(0).setSelected(true);
        activity.setOnChangeTripListener(this);
        transportsAvailable = new ArrayList<>();
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
        layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        listTrans.setLayoutManager(layoutManager);
       /* if (totalDistance < Defines.MAX_DISTANCE)
            filterCar();
        else
            fullCar();*/
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

    private void filterCar() {
        transportsAvailable.clear();
        for (int i = 0; i < transports.size(); i++){
            Car car = transports.get(i);
            car.setSelected(false);
            if (car.getSize() == 5 || car.getSize() == 8)
                transportsAvailable.add(car);
        }
        carSelect = transportsAvailable.get(0);
        transportsAvailable.get(0).setSelected(true);
    }

    private void fullCar() {
        transportsAvailable.clear();
        for (int i = 0; i < transports.size(); i++){
            Car car = transports.get(i);
            car.setSelected(false);
            transportsAvailable.add(car);
        }
        carSelect = transportsAvailable.get(0);
        transportsAvailable.get(0).setSelected(true);
    }

    private void resetCar() {
        for (int i = 0; i < transports.size(); i++){
            Car car = transports.get(i);
            car.setTotalPrice(0);
            car.setSelected(false);
        }
        carSelect = transports.get(0);
        transports.get(0).setSelected(true);
    }

    private void setCarPrice(int distance) {
        for (Car car : transports) {
            if (distance ==0) {
                car.setTotalPrice(0);
                continue;
            }
            if (car.getSize() != 5 && car.getSize() != 8) {
                if (totalDistance > Defines.MAX_DISTANCE)
                    if (tripType == Defines.ONE_WAY)
                        car.setTotalPrice(car.getPrice01way() * 200);
                    else
                        car.setTotalPrice(car.getPrice02way() * 200);
                else
                    car.setTotalPrice(car.getPrice11way() * 200);
            } else {
                if (totalDistance < Defines.MAX_DISTANCE)
                    car.setTotalPrice(car.getPrice11way() * (distance / 1000));
                else if (tripType == Defines.ONE_WAY)
                    car.setTotalPrice(car.getPrice01way() * (distance / 1000));
                else {
                    car.setTotalPrice((car.getPrice02way() + car.getPrice02way()) * (distance / 1000));
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_booking:
                if (btnBook.getText().equals("Đặt xe")) {
                    if (listener != null)
                        listener.onBookingClicked();
                }else if (btnBook.getText().equals("Chọn địa điểm đến")) {
                    if (listener != null)
                        listener.onSelectDestination();
                }
                break;
        }
    }

    @Override
    public void onChangeDistance(int distance) {
        this.totalDistance = distance;
       /* if (totalDistance < Defines.MAX_DISTANCE)
            filterCar();
        else
            fullCar();*/
        setCarPrice(distance);
        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(0);
        if (listener != null)
            listener.onSelectVehicle(carSelect);

        btnBook.setText("Đặt xe");
    }

    @Override
    public void onChangeTrip(int tripType) {
       /* if (totalDistance < Defines.MAX_DISTANCE)
            filterCar();
        else
            fullCar();*/
        this.tripType = tripType;
        setCarPrice(totalDistance);
        adapter.notifyDataSetChanged();
        layoutManager.scrollToPosition(0);
    }

    @Override
    public void onResetTrip() {
        resetCar();
        adapter.notifyDataSetChanged();
        btnBook.setText("Chọn địa điểm đến");
    }

    public interface OnTransportationListener{
        void onBookingClicked();
        void onSelectVehicle(Car car);
        void onSelectDestination();
    }
}
