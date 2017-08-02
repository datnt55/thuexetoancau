package grab.com.thuexetoancau.widget;

import android.app.Activity;
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

/**
 * Created by DatNT on 7/20/2017.
 */

public class TransportationLayout extends LinearLayout implements View.OnClickListener{
    private  Context mContext;
    private ArrayList<Car> transports;
    private RecyclerView listTrans;
    private TransportationAdapter adapter;
    private Button btnBook;
    private OnTransportationListener listener;

    public TransportationLayout(PassengerSelectActionActivity activity) {
        super(activity);
        this.mContext = activity;
        this.listener = activity;
        initLayout();
    }

    public TransportationLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        initLayout();
    }

    private void initLayout() {
        dummyData();
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.layout_transportation, this, true);
        listTrans = (RecyclerView) view.findViewById(R.id.list_transport);
        btnBook = (Button) view.findViewById(R.id.btn_booking);
        btnBook.setOnClickListener(this);
        listTrans.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        listTrans.setLayoutManager(layoutManager);
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

    public void setOnCallBackListener(OnTransportationListener listener){
        this.listener = listener;
    }

    private void dummyData() {
        transports = new ArrayList<>();
        transports.add(new Car(4,"Taxi 4 chỗ",R.drawable.car_4_size,41000));
        transports.add(new Car(5,"Taxi 5 chỗ",R.drawable.car_5_size,43000));
        transports.add(new Car(8,"Taxi 8 chỗ",R.drawable.car_8_size,64000));
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
