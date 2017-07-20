package grab.com.thuexetoancau.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;

import grab.com.thuexetoancau.R;
import grab.com.thuexetoancau.adapter.TransportationAdapter;
import grab.com.thuexetoancau.model.Car;

/**
 * Created by DatNT on 7/20/2017.
 */

public class TransportationLayout extends LinearLayout {
    private  Context mContext;
    private ArrayList<Car> transports;
    private RecyclerView listTrans;
    private TransportationAdapter adapter;

    public TransportationLayout(Context context) {
        super(context);
        this.mContext = context;
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
        listTrans.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        listTrans.setLayoutManager(layoutManager);
        adapter = new TransportationAdapter(mContext, transports);
        listTrans.setAdapter(adapter);

    }

    private void dummyData() {
        transports = new ArrayList<>();
        transports.add(new Car(1,"Taxi 4 chỗ","http://www.pngall.com/wp-content/uploads/2016/07/Car-Download-PNG.png",41000));
        transports.add(new Car(1,"Taxi 7 chỗ","http://www.pngall.com/wp-content/uploads/2016/07/Car.png",43000));
        transports.add(new Car(1,"Taxi 16 chỗ","http://www.pngmart.com/files/4/Car-Transparent-PNG.png",64000));
        transports.add(new Car(1,"Xe 45 chỗ","http://www.luxury-carhire-marbella.com/upload/1419505226floating-car.png",104000));
    }
}
